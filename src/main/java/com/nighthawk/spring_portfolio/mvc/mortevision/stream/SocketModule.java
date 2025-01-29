package com.nighthawk.spring_portfolio.mvc.mortevision.stream;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

@Component
public class SocketModule {


    private final SocketIOServer server;
    private final SocketService socketService;
    private SocketIOClient streamer;

    public SocketModule(SocketIOServer server, SocketService socketService) {
        this.server = server;
        this.socketService = socketService;
        server.addConnectListener(onConnected());
        server.addDisconnectListener(onDisconnected());

        server.addEventListener("streamReady", SDPData.class, streamReady());

        server.addEventListener("viewOfferClient", SDPData.class,viewOfferClient());
        server.addEventListener("viewAcceptClient", SDPData.class, viewAcceptClient());



    }

    private DataListener<SDPData> streamReady() {
        return (senderClient, data, ackSender) -> {
            if(streamer!=null)
            {
                return;
            }
            streamer = senderClient;
            // System.out.println(streamer);
        };
    }

    private DataListener<SDPData> viewOfferClient() {
        return (senderClient, data, ackSender) -> {
            if(streamer==null)
            {
                return;
            }
            System.out.println(data.getSdp());
            SDPData toSend = new SDPData();
            toSend.setSdp(data.getSdp());
            toSend.setType(data.getType());
            toSend.setSenderUID(senderClient.getSessionId().toString());
            System.out.println(toSend.getSenderUID());

            socketService.sendData("mortevision","viewOfferServer", streamer, toSend);
        };
    }

    private DataListener<SDPData> viewAcceptClient() {
        return (senderClient, data, ackSender) -> {
            if(streamer==null)
            {
                return;
            }
            SDPData toSend = new SDPData();
            toSend.setSdp(data.getSdp());
            toSend.setType(data.getType());
            // toSend.setSenderUID(senderClient.getSessionId().toString());
            // System.out.println(data.getSdp());
            System.out.println(server.getClient(UUID.fromString(data.getSenderUID())));

            socketService.sendData("mortevision","viewAcceptServer", 
            server.getClient(UUID.fromString(data.getSenderUID())), toSend);
        };
    }


    private ConnectListener onConnected() {
        return (client) -> {
            String room = "mortevision";//technically this could be anything in case i wanted to make the 6C 77 68 61 74 65 63 6C 75 62 room
            client.joinRoom(room);
            System.out.println("Socket ID[{}]  Connected to socket"+ client.getSessionId().toString());
        };

    }

    private DisconnectListener onDisconnected() {
        return client -> {
            System.out.println("Client[{}] - Disconnected from socket"+  client.getSessionId().toString());
        };
    }

}