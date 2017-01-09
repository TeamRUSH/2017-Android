package net.teamrush27.vision2017.comm.impl;

import android.util.Log;

import com.google.gson.Gson;

import net.teamrush27.vision2017.comm.IMessagePoller;
import net.teamrush27.vision2017.comm.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by cyocom on 12/29/16.
 */

public class SendMessagePoller implements IMessagePoller, Runnable {

    private BlockingQueue<Message> messageQueue = new ArrayBlockingQueue<Message>(50);
    private static boolean enabled = false;
//    private static final Logger LOG = LoggerFactory.getLogger(SendMessagePoller.class);
    volatile private Socket socket;

    public SendMessagePoller(Socket socket){
        this.socket = socket;
    }

    @Override
    public void processNext() {
        Message nextToSend = null;
        try {
            nextToSend = messageQueue.poll(250, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Log.e(this.getClass().getSimpleName(),"Couldn't poll queue");
        }
        if (nextToSend == null) {
            return;
        }
        boolean sent = sendToWire(nextToSend);
        Log.w(this.getClass().getSimpleName(),String.format("Message sent? : %s", sent));
    }

    @Override
    public void run() {
        while(enabled) {
            processNext();
        }
    }

    private synchronized boolean sendToWire(Message message) {
        Gson gson = new Gson();
        String messsageToSend = gson.toJson(message);
        if (socket != null && socket.isConnected()) {
            try {
                OutputStream os = socket.getOutputStream();
                os.write(messsageToSend.getBytes());
                return true;
            } catch (IOException e) {
                Log.w(this.getClass().getSimpleName(),"Could not send data to socket, try to reconnect");
                socket = null;
            }
        }
        return false;
    }

    synchronized public void addMessage(Message message) {
        messageQueue.offer(message);
    }

}
