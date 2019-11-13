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

import SceneKit

@objc open class UiAudioNode: TransformNode {

    @objc var fileName: URL? {
        get { return soundNode.url }
        set { soundNode.url = newValue }
    }
    @objc var action: AudioAction = .stop {
        didSet { performAction() }
    }
    @objc var soundLooping: Bool {
        get { return soundNode.loop }
        set { soundNode.loop = newValue }
    }
    @objc var soundMute: Bool {
        get { return soundNode.mute }
        set { soundNode.mute = newValue }
    }
    // The range of the pitch is 0.5 to 2.0, with 0.5 being one octave down
    // and 2.0 being one octave up (i.e., the pitch is a frequency multiple).
    // A pitch of 1.0 is the default and means no change.
    @objc var soundPitch: CGFloat {
        get { return soundNode.pitch }
        set { soundNode.pitch = newValue }
    }
    // The range of the volume is 0 to 8, with 0 for silence, 1 for unity gain,
    // and 8 for 8x gain.
    @objc var soundVolumeLinear: CGFloat {
        get { return soundNode.volume * 8.0 }
        set { soundNode.volume = newValue / 8.0 }
    }
    @objc var spatialSoundEnable: Bool {
        get { return soundNode.spatial }
        set { soundNode.spatial = newValue }
    }
    @objc var streamedFileOffset: CGFloat = 0

    var spatialSoundPosition: SpatialSoundPosition? {
        didSet { soundNode.position = spatialSoundPosition?.position ?? SCNVector3Zero }
    }
    var spatialSoundDirection: SpatialSoundDirection? {
        didSet { soundNode.direction = spatialSoundDirection?.direction ?? SCNQuaternionIdentity }
    }
    //var spatialSoundDistance: SpatialSoundDistance?
    //var spatialSoundRadiation: SpatialSoundRadiation?
    //var spatialSoundDirectSendLevels: SpatialSoundSendLevels?
    //var spatialSoundRoomSendLevels: SpatialSoundSendLevels?

    fileprivate var soundNode: SoundNode!

    @objc override func setupNode() {
        super.setupNode()
        soundNode = SoundNode()
        soundNode.soundLoaded = { [weak self] in
            self?.performAction()
        }
        contentNode.addChildNode(soundNode)
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let fileName = Convert.toFileURL(props["fileName"]) {
            self.fileName = fileName
        }

        if let soundLooping = Convert.toBool(props["soundLooping"]) {
            self.soundLooping = soundLooping
        }

        if let soundMute = Convert.toBool(props["soundMute"]) {
            self.soundMute = soundMute
        }

        if let soundPitch = Convert.toCGFloat(props["soundPitch"]) {
            self.soundPitch = soundPitch
        }

        if let soundVolumeLinear = Convert.toCGFloat(props["soundVolumeLinear"]) {
            self.soundVolumeLinear = soundVolumeLinear
        }

        if let spatialSoundEnable = Convert.toBool(props["spatialSoundEnable"]) {
            self.spatialSoundEnable = spatialSoundEnable
        }

        if let streamedFileOffset = Convert.toCGFloat(props["streamedFileOffset"]) {
            self.streamedFileOffset = streamedFileOffset
        }

        if let spatialSoundPositionParams = props["spatialSoundPosition"] as? [String: Any] {
            if let channel = Convert.toInt(spatialSoundPositionParams["channel"]),
                let position = Convert.toVector3(spatialSoundPositionParams["channelPosition"]) {
                spatialSoundPosition = SpatialSoundPosition(channel: channel, position: position)
            }
        }

        if let spatialSoundDirectionParams = props["spatialSoundDirection"] as? [String: Any] {
            if let channel = Convert.toInt(spatialSoundDirectionParams["channel"]),
                let direction = Convert.toQuaternion(spatialSoundDirectionParams["channelDirection"]) {
                spatialSoundDirection = SpatialSoundDirection(channel: channel, direction: direction)
            }
        }

        if let action = Convert.toAudioAction(props["action"]) {
            self.action = action
        }
    }

    @objc override func setDebugMode(_ debug: Bool) {
        super.setDebugMode(debug)
    }

    fileprivate func performAction() {
        switch action {
        case .start:
            soundNode.start()
        case .pause:
            soundNode.pause()
        case .resume:
            soundNode.resume()
        case .stop:
            soundNode.stop()
        }
    }
}

// MARK: - Internal sctructures
extension UiAudioNode {
    struct SpatialSoundPosition {
        let channel: Int
        let position: SCNVector3
    }

    struct SpatialSoundDirection {
        let channel: Int
        let direction: SCNQuaternion
    }

    struct SpatialSoundDistance {
        let channel: Int
        let minDistance: CGFloat
        let maxDistance: CGFloat
        let rolloffFactor: CGFloat
    }

    struct SpatialSoundRadiation {
        let channel: Int
        let innerAngle: CGFloat
        let outerAngle: CGFloat
        let outerGain: CGFloat
        let outerGainHf: CGFloat
    }

    struct SpatialSoundSendLevels {
        let channel: Int
        let gain: CGFloat
        let gainHf: CGFloat
        let gainLf: CGFloat
        let gainMf: CGFloat
    }
}
