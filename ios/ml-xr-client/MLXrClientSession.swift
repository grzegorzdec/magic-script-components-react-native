//
//  MLXrClientSession.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 17/07/2019.
//  Copyright © 2019 MagicLeap. All rights reserved.
//

import Foundation
import mlxr_ios_client
import ARKit

@objc(MLXrClientSession)
class MLXrClientSession: NSObject {

    static fileprivate var arSession: ARSession?
    fileprivate var session: mlxr_ios_client.MLXrClientSession?
    fileprivate var interval: TimeInterval

    @objc public func registerARSession(_ arSession: ARSession) {
        MLXrClientSession.arSession = arSession
    }

    @objc
    public func connect(address: String, deviceId: String, token: String, callback: RCTResponseSenderBlock) {
        assert(self.session != nil, "Session should not be nil")
        session = mlxs_ios_client.MLXrClientSession(nil, MLXrClientSession.arSession);
        let result: Bool = session.connect(address, deviceId, token)
        callback([NSNull(), result])
    }

    func setUpdateInterval(_ interval: TimeInterval) {
        self.interval = interval
    }

    @objc
    public func update(_ callback: RCTResponseSenderBlock) {
//        let frame: ARFrame = arView.session.currentFrame
//        let location: CLLocation =
        let result: Bool = self.session.update(frame, location)
        callback([NSNull(), result])
    }

    @objc
    public func getAllAnchors(_ callback: RCTResponseSenderBlock) {
//        let anchors: [mlxr_ios_client.MLXrClientAnchorData] = MLXrClientSession.theSession.getAllAnchors()
//        let results: [[String : Any]] = anchors.map({ MLXrClientAnchorData($0).getJSONRepresenation() })
        let results: [[String : Any]] = [MLXrClientAnchorData().getJsonRepresentation()]
        callback([NSNull(), results])
    }

    @objc
    public func getAnchorByPcfId(id: String, callback: RCTResponseSenderBlock) {
        guard let uuid = UUID(uuidString: id) else {
            callback(["Incorrect id", NSNull()])
            return
        }

        guard let anchorData: mlxr_ios_client.MLXrClientAnchorData? = session.getAnchorByPcfId(uuid) else {
            callback([NSNull(), NSNull()])
            return
        }

        let result: [String : Any] = MLXrClientAnchorData(anchorData: anchorData).getJsonRepresentation()
//        let result: [String : Any] = MLXrClientAnchorData().getJsonRepresentation()
        callback([NSNull(), result])
    }

    @objc
    public func getLocalizationStatus(_ callback: RCTResponseSenderBlock) {
        let status: MLXrClientLocalization = MLXrClientLocalization(localizationStatus: session.getLocalizationStatus())
//        let status: MLXrClientLocalization = MLXrClientLocalization.localized
        callback([NSNull(), status.rawValue])
    }

    @objc
    static func requiresMainQueueSetup() -> Bool {
        return true
    }
}
