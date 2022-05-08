package org.daeint.icarus;

import com.google.common.eventbus.Subscribe;
import vip.floatationdevice.guilded4j.G4JClient;
import vip.floatationdevice.guilded4j.event.ChatMessageCreatedEvent;
import vip.floatationdevice.guilded4j.event.GuildedWebSocketClosedEvent;
import vip.floatationdevice.guilded4j.event.GuildedWebSocketInitializedEvent;
import vip.floatationdevice.guilded4j.object.ChatMessage;

//This class handles events that happen between Guilded and Icarus.
// Functions within this class should only flow directly into an instance of Icarus
public class GuildedEventHandler {

    Icarus icarus;

    public GuildedEventHandler(G4JClient clientI) {
        //Instantiate required objects
        icarus = new Icarus(clientI);
    }


    @Subscribe  //This function lets the user know when Icarus has successfully connected
    public void onConnected(GuildedWebSocketInitializedEvent event) {
        System.out.println("Connected to Guilded server");
    }

    @Subscribe  //This function lets the user know when Icarus has been disconnected
    public void onDisconnected(GuildedWebSocketClosedEvent event) {
        System.out.println("Disconnected from Guilded:\n\tCode:\t" + event.getCode() + "\n\tReason:\t" + event.getReason());
    }

    @Subscribe  //This function detects when a user sends a message in any channel
    public void onMessage(ChatMessageCreatedEvent event) {
        //If statement prevents unwanted calls to icarus.messageReceived
        if (event.getChatMessageObject().getContent().contains("@Icarus")) {
            icarus.giveResponse(event.getChatMessageObject());
        }
    }

}
