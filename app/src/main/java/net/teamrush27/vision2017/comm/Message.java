package net.teamrush27.vision2017.comm;

/**
 * Created by cyocom on 12/29/16.
 */
public abstract class Message {

    public abstract MessageType getType();

    public abstract String getMessage();

    public boolean isValid(){
        if(getType() == null){
            return false;
        } else {
            return true;
        }
    }
}
