package com.nighthawk.spring_portfolio.mvc.mortevision;

import java.util.Collections;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.onvoid.webrtc.CreateSessionDescriptionObserver;
import dev.onvoid.webrtc.PeerConnectionFactory;
import dev.onvoid.webrtc.PeerConnectionObserver;
import dev.onvoid.webrtc.RTCAnswerOptions;
import dev.onvoid.webrtc.RTCConfiguration;
import dev.onvoid.webrtc.RTCIceCandidate;
import dev.onvoid.webrtc.RTCIceServer;
import dev.onvoid.webrtc.RTCPeerConnection;
import dev.onvoid.webrtc.RTCRtpReceiver;
import dev.onvoid.webrtc.RTCSdpType;
import dev.onvoid.webrtc.RTCSessionDescription;
import dev.onvoid.webrtc.media.MediaStream;
import dev.onvoid.webrtc.media.MediaStreamTrack;

@RestController
@RequestMapping("/webrtc")
public class SFU implements PeerConnectionObserver {

    MediaStream broadcaster;

    @PostMapping("/consume")
    public String consumer(String stp) {
        if (broadcaster == null) {
            return new JSONObject("{'error':'no broadcast'}").toString();
        }
        RTCIceServer iceServer = new RTCIceServer();
        iceServer.urls = Collections.singletonList("stun:stun.l.google.com:19302");

        RTCConfiguration config = new RTCConfiguration();
        config.iceServers = Collections.singletonList(iceServer);

        PeerConnectionFactory PCF = new PeerConnectionFactory();
        RTCPeerConnection peerConnection = PCF.createPeerConnection(config, null);

        RTCSessionDescription sessionDescription = new RTCSessionDescription(RTCSdpType.OFFER, stp);
        peerConnection.setRemoteDescription(sessionDescription, null); // this is most likley the problem
        peerConnection.addTrack((MediaStreamTrack) broadcaster.getAudioTracks()[0], null);
        peerConnection.addTrack((MediaStreamTrack) broadcaster.getVideoTracks()[0], null);
        peerConnection.createAnswer(new RTCAnswerOptions(), null);
        peerConnection.setLocalDescription(peerConnection.getLocalDescription(), null);
        JSONObject payload = new JSONObject().put("sdp", peerConnection.getLocalDescription());
        return payload.toString();
    }

    @PostMapping("/broadcast")
    public String broadcast(String stp) {
        RTCIceServer iceServer = new RTCIceServer();
        iceServer.urls = Collections.singletonList("stun:stun.l.google.com:19302");
        RTCConfiguration config = new RTCConfiguration();
        config.iceServers = Collections.singletonList(iceServer);
        PeerConnectionFactory PCF = new PeerConnectionFactory();
        RTCPeerConnection peerConnection = PCF.createPeerConnection(config, this);

        RTCSessionDescription sessionDescription = new RTCSessionDescription(RTCSdpType.OFFER, stp);
        peerConnection.setRemoteDescription(sessionDescription, null);

        peerConnection.createAnswer(new RTCAnswerOptions(), new CreateSessionDescriptionObserver() {

            @Override
            public void onSuccess(RTCSessionDescription description) {
                peerConnection.setLocalDescription(description, null);
            }

            @Override
            public void onFailure(String error) {
                throw new UnsupportedOperationException("Unimplemented method 'onFailure'");
            }

        });
        JSONObject payload = new JSONObject().put("sdp", peerConnection.getLocalDescription());
        return payload.toString();
    }

    @Override
    public void onAddTrack(RTCRtpReceiver receiver, MediaStream[] mediaStreams) {
        broadcaster = mediaStreams[0];
    }

    @Override
    public void onIceCandidate(RTCIceCandidate candidate) {
        throw new UnsupportedOperationException("Unimplemented method 'onIceCandidate'");
    }
}