package com.nighthawk.spring_portfolio.mvc.mortevision.stream;

import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

@Component
public class SocketModule {


    private final SocketIOServer server;
    private final SocketService socketService;

    public SocketModule(SocketIOServer server, SocketService socketService) {
        this.server = server;
        this.socketService = socketService;
        server.addConnectListener(onConnected());
        server.addDisconnectListener(onDisconnected());

        server.addEventListener("streamReady", Message.class, onChatReceived());
        server.addEventListener("clientViewRequest", Message.class, onChatReceived());



    }


    private DataListener<Message> onChatReceived() {
        return (senderClient, data, ackSender) -> {
            System.out.println(data.toString());
            socketService.sendMessage(data.getRoom(),"get_message", senderClient, data.getMessage());
        };
    }


    private ConnectListener onConnected() {
        return (client) -> {
            String room = client.getHandshakeData().getSingleUrlParam("room");
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