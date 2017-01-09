package net.teamrush27.vision2017.comm;

/**
 * Created by cyocom on 1/8/17.
 */
public interface MessageHandler {

    boolean supports(MessageType messageType);

    boolean handle(Message message);

}
