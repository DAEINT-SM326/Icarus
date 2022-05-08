package org.daeint.icarus;

import java.util.ArrayList;

public class FleetUser {

    private final ArrayList<String> ships;
    private final String username;

    public FleetUser() {
        ships = new ArrayList<>();
        username = "none";
    }

    public FleetUser(String usernameI, ArrayList<String> shipsI) {
        ships = new ArrayList<>();

        for (int i = 0; i < shipsI.size(); i++) {
            ships.add(shipsI.get(i));
        }

        username = usernameI;
    }

    public String listUserShips() {
        String ret = "| ";

        for (String ship : ships) {
            ret += "`" + ship + "` | ";
        }

        return ret;
    }

    public String getUsername() {
        return username;
    }
}
