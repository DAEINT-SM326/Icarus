package org.daeint.icarus;

import vip.floatationdevice.guilded4j.G4JClient;
import vip.floatationdevice.guilded4j.object.*;

//Icarus is the class that contains all of Icarus' functionality
public class Icarus {
    String serverID;

    G4JClient client;
    Fleetyards fleet;
    Embed[] embedTemplates;
    ServerMemberSummary[] guildedUsers;

    public Icarus(G4JClient clientI) {
        client = clientI;
        fleet = new Fleetyards();
        serverID = "rRpyx5dl";

        embedTemplates = new Embed[1];
        guildedUsers = client.getServerMembers(serverID);

        embedTemplates[0] = new Embed();
    }

    //This function takes the input message and chooses which command to execute
    public void giveResponse(ChatMessage message) {
        String command = parseCommand(message).toLowerCase();
        String operator = parseArgument(message);

        switch (command) {

            case "showfleet":
                showFleet(message, operator);
                break;
            default:
                break;

        }

    }

    //This function returns ONLY the command in String format
    private String parseCommand(ChatMessage message) {
        try {
            return message.getContent().substring(8).substring(0,message.getContent().substring(8).indexOf(" "));
        } catch (StringIndexOutOfBoundsException e) {
            return message.getContent().substring(8);
        }
    }

    //This function returns ONLY the argument(s) in String format
    private String parseArgument(ChatMessage message) {
        return message.getContent().substring(8).substring(message.getContent().substring(8).indexOf(" ") + 1);
    }

    //This function sends a message containing a list of every ship the given user owns
    private void showFleet(ChatMessage message, String username) {
        client.createChannelMessage(message.getChannelId(), "Fetching ships...", null, null, false, false);

        if (Fleetyards.listUserShips(username).contains("unknown_user")) {
            changeEmbedTemplates(fleet.listUsers(), "Current Fleetyards Users", "https://fleetyards.net/fleets/df1/members", getGuildedUser("Icarus").getAvatar());
            client.createChannelMessage(message.getChannelId(), "Unknown user, see list below for available users:", embedTemplates, null, false, false);
        } else {
            try {
                changeEmbedTemplates(Fleetyards.listUserShips(username), username + "'s Ships", "https://fleetyards.net/hangar/" + username, getGuildedUser(username).getAvatar());
                client.createChannelMessage(message.getChannelId(), " ", embedTemplates,null, false, false);
            } catch (java.lang.NullPointerException e) {
                changeEmbedTemplates(Fleetyards.listUserShips(username), username + "'s Ships", "https://fleetyards.net/hangar/" + username, getGuildedUser("Icarus").getAvatar());
                client.createChannelMessage(message.getChannelId(), " ", embedTemplates, null, null, false);
            } catch (Exception e) {
                client.createChannelMessage(message.getChannelId(), "Error: " + e, null, null, false, false);
            }
        }

    }

    //This functions returns the requested Guilded user's UserSummary if they exist
    private UserSummary getGuildedUser(String username) {
        for (int i = 0; i < guildedUsers.length; i++) {
            if (guildedUsers[i].getUser().getName().equals(username)) {
                return guildedUsers[i].getUser();
            }
        }

        return null;
    }

    //This functions sets the embedTemplate with the proper information before usage
    private void changeEmbedTemplates(String description, String title, String footer, String url) {
        embedTemplates[0].setDescription(description);
        embedTemplates[0].setTitle(title);
        embedTemplates[0].setFooterText(footer);
        embedTemplates[0].setThumbnailUrl(url);
    }

}
