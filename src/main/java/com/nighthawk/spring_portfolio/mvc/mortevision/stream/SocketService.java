package com.nighthawk.spring_portfolio.mvc.mortevision.stream;

import org.springframework.stereotype.Service;

import com.corundumstudio.socketio.SocketIOClient;

@Service
public class SocketService {

    public void sendData(String room, String eventName, SocketIOClient reciever, SDPData data) {

        reciever.sendEvent(eventName,
                data);
    }

}