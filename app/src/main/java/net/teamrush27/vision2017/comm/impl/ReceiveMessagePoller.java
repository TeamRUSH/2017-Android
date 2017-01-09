package net.teamrush27.vision2017.comm.impl;

import android.util.Log;

import com.google.gson.Gson;

import net.teamrush27.vision2017.comm.IMessagePoller;
import net.teamrush27.vision2017.comm.Message;
import net.teamrush27.vision2017.comm.MessageHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by cyocom on 12/29/16.
 */

public class ReceiveMessagePoller implements IMessagePoller, Runnable {

    private static boolean enabled = false;
    private volatile Socket socket;
    private List<MessageHandler> messageHandlers = new ArrayList<>();
    private PollerHandler pollerHandler;
    private static final int POLL_TIMER = 100;

//    private final static Logger LOG = LoggerFactory.getLogger(ReceiveMessagePoller.class);

    public ReceiveMessagePoller(Socket socket, List<MessageHandler> messageHandlers, PollerHandler pollerHandler){
        this.socket = socket;
        this.messageHandlers.addAll(messageHandlers);
        this.pollerHandler = pollerHandler;
    }

    @Override
    public void processNext() {
        if (pollerHandler.isConnected()) {
            BufferedReader reader;
            try {
                InputStream is = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));
            } catch (IOException e) {
                Log.e(this.getClass().getSimpleName(),"Could not get input stream", e);
                return;
            } catch (NullPointerException npe) {
                Log.e(this.getClass().getSimpleName(),"socket was null", npe);
                return;
            }
            String jsonMessage = null;
            try {
                jsonMessage = reader.readLine();
            } catch (IOException e) {
            }
            if (jsonMessage != null) {
                Gson gson = new Gson();
                Message parsedMessage =  gson.fromJson(jsonMessage, Message.class);
                if (parsedMessage.isValid()) {
                    handleMessage(parsedMessage);
                }
            }
        } else {
            try {
                Thread.sleep(POLL_TIMER, 0);
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public void run() {
        while(enabled){
            processNext();
        }
    }

    public void handleMessage(Message message){
        for(MessageHandler messageHandler : messageHandlers){
            if(messageHandler.supports(message.getType())){
                messageHandler.handle(message);
            }
        }
        Log.w(this.getClass().getSimpleName(),message.getType() + " " + message.getMessage());
    }
}
