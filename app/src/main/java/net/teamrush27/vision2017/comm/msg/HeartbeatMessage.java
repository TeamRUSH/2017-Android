package net.teamrush27.vision2017.comm.msg;

import net.teamrush27.vision2017.comm.Message;
import net.teamrush27.vision2017.comm.MessageType;

/**
 * Created by cyocom on 12/29/16.
 */

public class HeartbeatMessage extends Message {

    @Override
    public MessageType getType() {
        return MessageType.HEARTBEAT;
    }

    @Override
    public String getMessage() {
        return null;
    }
}
