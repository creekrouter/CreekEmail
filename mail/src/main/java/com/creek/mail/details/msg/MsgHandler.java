package com.creek.mail.details.msg;


import java.util.ArrayList;
import java.util.List;

public class MsgHandler {

    private List<WatchDog> mDispatcher =  new ArrayList<>();


    public void addWatchDog(WatchDog dog) {
        if (mDispatcher.contains(dog)){
            return;
        }
        mDispatcher.add(dog);
    }

    private void send(Event event){
        for (WatchDog dog:mDispatcher){
            dog.onMsgCome(event);
        }
    }

    public void sendEvent(Integer eventID) {
        Event event = new Event();
        event.eventID = eventID;
        send(event);
    }

    public void sendEvent(Integer eventID, String info) {
        Event event = new Event();
        event.eventID = eventID;
        event.info = info;
        send(event);
    }

    public void sendEvent(Integer eventID, int arg1) {
        Event event = new Event();
        event.eventID = eventID;
        event.arg1 = arg1;
        send(event);
    }
}
