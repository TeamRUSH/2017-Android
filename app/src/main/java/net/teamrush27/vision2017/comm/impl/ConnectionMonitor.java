package net.teamrush27.vision2017.comm.impl;

import net.teamrush27.vision2017.comm.Message;
import net.teamrush27.vision2017.comm.MessageHandler;
import net.teamrush27.vision2017.comm.MessageType;
import net.teamrush27.vision2017.comm.msg.HeartbeatMessage;

/**
 * Created by cyocom on 12/29/16.
 */

public class ConnectionMonitor implements Runnable, MessageHandler {


    private static boolean enabled = false;
    private static boolean connected = false;
    private long lastHeartbeatSent = System.currentTimeMillis();
    private long lastHeartbeatRecieved = 0;
    private static long HEATBEAT_RATE = 250;
    volatile private SendMessagePoller sendMessagePoller;


    public ConnectionMonitor(SendMessagePoller sendMessagePoller){
        this.sendMessagePoller = sendMessagePoller;
        enabled = true;
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public void run() {
        while (enabled) {
            try {
                long now = System.currentTimeMillis();

                if (now - lastHeartbeatSent > HEATBEAT_RATE) {
                    sendMessagePoller.addMessage(new HeartbeatMessage());
                    lastHeartbeatSent = now;
                }

                if (Math.abs(lastHeartbeatRecieved - lastHeartbeatSent) > HEATBEAT_RATE && connected) {
                    connected = false;
                }
                if (Math.abs(lastHeartbeatRecieved - lastHeartbeatSent) < HEATBEAT_RATE && !connected) {
                    connected = true;
                }

                Thread.sleep(HEATBEAT_RATE, 0);
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public boolean supports(MessageType messageType) {
        return MessageType.HEARTBEAT.equals(messageType);
    }

    @Override
    public boolean handle(Message message) {
        lastHeartbeatRecieved = System.currentTimeMillis();
        return true;
    }
}
