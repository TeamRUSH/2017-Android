package net.teamrush27.vision2017.comm.impl;

import android.util.Log;

import net.teamrush27.vision2017.comm.MessageHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyocom on 12/29/16.
 */

public class PollerHandler implements Runnable{

    volatile private Socket socket;
    private ReceiveMessagePoller receiveMessagePoller;
    private SendMessagePoller sendMessagePoller;
    private ConnectionMonitor connectionMonitor;
//    private static final Logger LOG = LoggerFactory.getLogger(PollerHandler.class);
    private static final String HOST = "localhost";
    private static final int PORT = 6969;
    private boolean enabled = false;

    public void init() {
        tryConnect(HOST, PORT);
        Log.i(this.getClass().getSimpleName(),"init pollerhandler");


        sendMessagePoller = new SendMessagePoller(socket);
        connectionMonitor = new ConnectionMonitor(sendMessagePoller);
        List<MessageHandler> messageHandlers = new ArrayList<MessageHandler>();
        messageHandlers.add(connectionMonitor);

        receiveMessagePoller = new ReceiveMessagePoller(socket, messageHandlers, this);

        Log.i(this.getClass().getSimpleName(),"starting threads");

        new Thread(receiveMessagePoller).start();
        new Thread(sendMessagePoller).start();
        new Thread(connectionMonitor).start();
        enabled = true;
    }

    synchronized private void tryConnect(String host, int port) {
        Log.i(this.getClass().getSimpleName(),"connecting...");
        if (!isConnected()) {
            try {
                Log.i(this.getClass().getSimpleName(),"trying to connect");
                socket = new Socket(host, port);
                socket.setSoTimeout(100);
            } catch (IOException e) {
                Log.w(this.getClass().getSimpleName(),"Could not connect");
                socket = null;
            }
        }
    }

    @Override
    public void run() {
        while(enabled){
            if(isConnected()){
                poll();
            } else {
                tryConnect(HOST, PORT);
            }
        }
    }

    private void poll() {
        try {
            Thread.sleep(100, 0);
        } catch (InterruptedException e) {
        }
    }


    public boolean isConnected(){
        return socket != null && socket.isConnected() && connectionMonitor.isConnected();
    }
}
