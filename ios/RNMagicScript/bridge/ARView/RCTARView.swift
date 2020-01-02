//
//  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

import UIKit
import ARKit
import SceneKit

@objc public class RCTARView: UIView {

    fileprivate(set) var arView: ARSCNView!
    fileprivate var inputResponder: UITextField?
#if targetEnvironment(simulator)
    fileprivate var rayCastNode: SCNNode?
#endif

    static fileprivate var instance: RCTARView!
    @objc static public var arSession: ARSession {
        return instance.arView.session
    }

    public var scene: SCNScene {
        return arView.scene
    }

    public var cameraNode: SCNNode? {
        return arView.pointOfView
    }

    @objc public var debug: Bool {
        get { return arView.showsStatistics }
        set {
            arView.showsStatistics = newValue
            arView.debugOptions = newValue ? [.showWorldOrigin, .showFeaturePoints] : []
        }
    }

    @objc public var rendersContinuously: Bool {
        get { return arView.rendersContinuously }
        set { arView.rendersContinuously = newValue }
    }

    fileprivate var _configuration: ARWorldTrackingConfiguration?
    fileprivate var configuration: ARWorldTrackingConfiguration? {
        guard ARWorldTrackingConfiguration.isSupported else { return nil }

        guard let _ = _configuration else  {
            _configuration = ARWorldTrackingConfiguration()
            // _configuration.planeDetection = ARPlaneDetectionHorizontal;
            if #available(iOS 11.3, *) {
                let videoFormatCount = ARWorldTrackingConfiguration.supportedVideoFormats.count
                if videoFormatCount > 0 {
                    _configuration!.videoFormat = ARWorldTrackingConfiguration.supportedVideoFormats[videoFormatCount - 1]
                }
                _configuration!.isAutoFocusEnabled = false
            }

            _configuration!.worldAlignment = ARConfiguration.WorldAlignment.gravity
            _configuration!.providesAudioData = false

            return _configuration
        }

        return _configuration
    }

    //MARK: RCTARView Observable
    fileprivate(set) var observers: [WeakReference<RCTARViewObserving>] = []

    func register(_ observer: RCTARViewObserving) {
        observers.append(WeakReference(value: observer))
    }

    func unregister(_ observer: RCTARViewObserving) {
        observers.removeAll { storedObserver -> Bool in
            return storedObserver.value === observer
        }
    }

    public init() {
        super.init(frame: CGRect.zero)
        self.arView = createARView()
        setupNodesManager(self.arView)
        setupGestureRecognizers(self.arView)
        RCTARView.instance = self
        resume()
    }

    required public init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    fileprivate func createARView() -> ARSCNView {
        let view = ARSCNView()
        view.autoenablesDefaultLighting = true
        view.automaticallyUpdatesLighting = true
        view.backgroundColor = UIColor(white: 55.0 / 255.0, alpha: 1.0)
        view.rendersContinuously = true
        view.scene.rootNode.name = "root"
        view.delegate = self // ARSCNViewDelegate
        
        // Add AR view as a child
        view.translatesAutoresizingMaskIntoConstraints = false
        addSubview(view)
        NSLayoutConstraint.activate([
            view.leftAnchor.constraint(equalTo: leftAnchor),
            view.topAnchor.constraint(equalTo: topAnchor),
            view.rightAnchor.constraint(equalTo: rightAnchor),
            view.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])

    #if targetEnvironment(simulator)
        // Allow for basic orbit gestures if we're running in the simulator
        view.allowsCameraControl = true
        view.defaultCameraController.interactionMode = SCNInteractionMode.orbitTurntable
        view.defaultCameraController.maximumVerticalAngle = 45
        view.defaultCameraController.inertiaEnabled = true
        view.defaultCameraController.translateInCameraSpaceBy(x: 0.0, y: 0.0, z: 1.5)
    #endif

        // Set camera's range
        view.pointOfView?.camera?.zNear = 0.001
        view.pointOfView?.camera?.zFar = 10

        return view
    }

    fileprivate func setupNodesManager(_ view: ARSCNView) {
        // Resgister scene in nodes manager
        UiNodesManager.instance.registerARView(self)
        UiNodesManager.instance.onInputFocused = { [weak self] input in
            self?.presentInput(input)
        }
        UiNodesManager.instance.onInputUnfocused = { [weak self] in
            self?.dismissInput()
        }

        if let rootView = UIApplication.shared.keyWindow?.rootViewController?.view {
            UiNodesManager.instance.dialogPresenter = DialogPresenter(parentView: rootView)
        }
    }

    fileprivate func setupGestureRecognizers(_ view: ARSCNView) {
        // Add tap gesture
        let tapGestureRecognizer = TapGestureRecognizer(nodeSelector: UiNodesManager.instance.nodeSelector, target: self, action: #selector(handleTapAction(_:)))
        tapGestureRecognizer.getCameraNode = { [weak self] in return self?.arView.pointOfView }
        addGestureRecognizer(tapGestureRecognizer)

        // Add drag gesture
        let dragGestureRecognizer = DragGestureRecognizer(nodeSelector: UiNodesManager.instance.nodeSelector, target: self, action: #selector(handleDragAction(_:)))
        dragGestureRecognizer.getCameraNode = { [weak self] in return self?.arView.pointOfView }
        addGestureRecognizer(dragGestureRecognizer)

        // Add long press gesture
        let longPressGestureRecogrnizer = LongPressGestureRecognizer(nodeSelector: UiNodesManager.instance.nodeSelector, target: self, action: #selector(handleLongPressAction(_:)))
        longPressGestureRecogrnizer.getCameraNode = { [weak self] in return self?.arView.pointOfView }
        addGestureRecognizer(longPressGestureRecogrnizer)
    }

    fileprivate func presentInput(_ input: DataProviding) {
        if (inputResponder == nil) {
            inputResponder = UITextField()
            inputResponder!.isHidden = true
            addSubview(inputResponder!)
        }

        let inputAccessoryView = InputAccessoryViewFactory.createView(for: input, onFinishEditing: {
            UiNodesManager.instance.handleNodeTap(nil)
        })

        let inputView = InputViewFactory.createView(for: input, onFinishEditing: {
            UiNodesManager.instance.handleNodeTap(nil)
        })

        inputResponder!.inputAccessoryView = inputAccessoryView
        inputResponder!.inputView = inputView
        inputResponder!.becomeFirstResponder()
        inputAccessoryView?.becomeFirstResponder()
    }

    fileprivate func dismissInput() {
        // NOTE: This line generates the following warning:
        // First responder warning: '<GrowingTextView...>' rejected resignFirstResponder
        // when being removed from hierarchy.
        inputResponder?.inputAccessoryView?.resignFirstResponder()

        inputResponder?.inputView = nil
        inputResponder?.inputAccessoryView = nil
        inputResponder?.resignFirstResponder()
    }

    public func pause() {
        arView.session.pause()
    }

    public func resume() {
        if let configuration = self.configuration {
            arView.session.run(configuration, options: [])
        }
    }

    public func reset() {
        if let configuration = self.configuration {
            arView.session.run(configuration, options: [.removeExistingAnchors, .resetTracking])
        }
    }
}

// MARK: - Event handlers
extension RCTARView {
    @objc fileprivate func handleTapAction(_ sender: TapGestureRecognizer) {
        print("BUKA \(self.classForCoder) \(#function) \(sender.state.rawValue)")
        if sender.state == .ended {
            UiNodesManager.instance.handleNodeTap(sender.tappedNode)
            #if targetEnvironment(simulator)
            guard let cameraNode = cameraNode,
                let ray = Ray(gesture: sender, cameraNode: cameraNode) else { return }

            if debug && rayCastNode == nil {
                rayCastNode = NodesFactory.createLinesNode(vertices: [SCNVector3(), SCNVector3(0,1,0)], color: UIColor.green)
                scene.rootNode.addChildNode(rayCastNode!)
            }

            if let node = rayCastNode {
                node.position = ray.begin
                node.orientUpVectorAlong(ray.direction)
                node.scale = SCNVector3(1, ray.length, 1)
            }
            #endif
        }
    }

    @objc fileprivate func handleDragAction(_ sender: DragGestureRecognizer) {
        print("BUKA \(self.classForCoder) \(#function) \(sender.state.rawValue)")
        if sender.state == UIGestureRecognizer.State.changed {
            sender.dragNode?.dragValue = sender.beginDragValue + sender.dragDelta
        }
    }

    @objc fileprivate func handleLongPressAction(_ sender: LongPressGestureRecognizer) {
        print("BUKA \(self.classForCoder) \(#function) \(sender.state.rawValue)")
        UiNodesManager.instance.handleNodeLongPress(sender.longPressedNode, sender.state)
    }
}
