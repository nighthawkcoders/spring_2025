package com.nighthawk.spring_portfolio.mvc.mortevision;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
import dev.onvoid.webrtc.SetSessionDescriptionObserver;
import dev.onvoid.webrtc.media.MediaStream;
import dev.onvoid.webrtc.media.MediaStreamTrack;

@RestController
@RequestMapping("/webrtc")
public class SFU implements PeerConnectionObserver {

    MediaStream broadcaster;

    @PostMapping("/consume")
    public JSONObject consumer(String stp) {
        if (broadcaster == null) {
            return new JSONObject("{'error':'no broadcast'}");
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
        return payload;
    }

    @PostMapping(value="/broadcast",consumes ="application/json")
    public Map<String, String> broadcast(@RequestBody String body) {
        String sdp = new JSONObject(body).getJSONObject("sdp").getString("sdp");
        RTCIceServer iceServer = new RTCIceServer();
        iceServer.urls = Collections.singletonList("stun:stun.l.google.com:19302");
        RTCConfiguration config = new RTCConfiguration();
        config.iceServers = Collections.singletonList(iceServer);
        System.out.println("I HATE PEOPLE I HATE PEOPLE I HATE PEOPLE");
        PeerConnectionFactory PCF = new PeerConnectionFactory();
        System.out.println("FU");
        RTCPeerConnection peerConnection = PCF.createPeerConnection(config, this);
        System.out.println("FUDGE");
        System.out.println(sdp);
        RTCSessionDescription sessionDescription = new RTCSessionDescription(RTCSdpType.OFFER, sdp);
        peerConnection.setRemoteDescription(sessionDescription, new SetSessionDescriptionObserver() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                // throw new UnsupportedOperationException("Unimplemented method 'onSuccess'");
            }

            @Override
            public void onFailure(String error) {
                // TODO Auto-generated method stub
                // throw new UnsupportedOperationException("Unimplemented method 'onFailure'");
            }
            
        });
        System.out.println("kill myself arc");

        peerConnection.createAnswer(new RTCAnswerOptions(), new CreateSessionDescriptionObserver() {

            @Override
            public void onSuccess(RTCSessionDescription description) {
                peerConnection.setLocalDescription(description, new SetSessionDescriptionObserver() {

                    @Override
                    public void onSuccess() {
                        // throw new UnsupportedOperationException("Unimplemented method 'onSuccess'");
                    }

                    @Override
                    public void onFailure(String error) {
                        // throw new UnsupportedOperationException("Unimplemented method 'onFailure'");
                    }
                    
                });
            }

            @Override
            public void onFailure(String error) {
                // throw new UnsupportedOperationException("Unimplemented method 'onFailure'");
            }

        });
        HashMap<String, String> map = new HashMap<>();
        map.put("type",peerConnection.getLocalDescription().sdpType.name().toLowerCase());
        map.put("sdp",peerConnection.getLocalDescription().sdp);
        return map;
    }

    @Override
    public void onAddTrack(RTCRtpReceiver receiver, MediaStream[] mediaStreams) {
        broadcaster = mediaStreams[0];
    }

    @Override
    public void onIceCandidate(RTCIceCandidate candidate) {
        // throw new UnsupportedOperationException("Unimplemented method 'onIceCandidate'");
    }
}