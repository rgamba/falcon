package com.rgamba.falcon;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Function;

public class HttpServer {
    private Function<Request, Response> _handler;

    public void setHandler(Function<Request, Response> handler) {
        _handler = handler;
    }

    public void listen(int port) {
        try (
                ServerSocket socket = new ServerSocket(port)
        ) {
            while (true) {
                try (
                        Socket client = socket.accept()
                ) {
                    System.out.println("Accepting connection from " + client.getRemoteSocketAddress().toString());
                    Runnable httpThread = new HttpThread(client, _handler);
                    Thread thread = new Thread(httpThread);
                    thread.run();
                } catch (Exception threadEx) {
                    System.out.println("Thread ex: " + threadEx.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
