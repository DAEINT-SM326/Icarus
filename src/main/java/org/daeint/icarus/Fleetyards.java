package org.daeint.icarus;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;


import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

//This class updates and stores user data from the Fleetyards API
public class Fleetyards {

    private static ArrayList<String> fleetUsernames;
    private static ArrayList<String> userShips;
    private static ArrayList<FleetUser> fleetUsers;

    public Fleetyards() {
        fleetUsernames = new ArrayList<>();
        userShips = new ArrayList<>();
        fleetUsers = new ArrayList<>();

        buildFleetUsers();
    }

    //This function populates the fleetUsers ArrayList with every member listed on Fleetyards
    private static void buildFleetUsers() {
        System.out.println("Gathering user ships...\nThis will take a few minutes");
        requestUsers();
        for (int i = 0; i < fleetUsernames.size(); i++) {
            userShips.clear();
            requestShips(fleetUsernames.get(i));
            try { Thread.sleep(2000); } catch (InterruptedException e) {}
            fleetUsers.add(new FleetUser(fleetUsernames.get(i), userShips));
        }
    }

    //This function requests the users from Fleetyards
    public static void requestUsers() {

        HttpClient jClient = HttpClient.newBuilder()
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                .build();

        //If this HttpRequest fails, check the cookie as it might have expired
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.fleetyards.net/v1/fleets/df1/members/"))
                .header("Cookie", "FLTYRD=bf81f24d2dac6a0e3f43ae01669f1b03; FLTYRD_USER_STORED=eyJfcmFpbHMiOnsibWVzc2FnZSI6Ilcxc2lObVJrWWpRMk9Ua3RabVkzWXkwMFlUY3lMVGhqT0dNdE1HSXhPVE15Wm1Ka09EZGpJbDBzSWlReVlTUXhNQ1JrZDBkNGJVczNRbTVzVUZaYU5ubG5NMU4xYkV0UElpd2lNVFkxTVRNM05UQTRNaTQ1TnpnMU1qQTBJbDA9IiwiZXhwIjoiMjAyMi0xMS0wMVQwNDoxODowMi45NzhaIiwicHVyIjoiY29va2llLkZMVFlSRF9VU0VSX1NUT1JFRCJ9fQ%3D%3D--a863a8f08076c04ccf18802f98a9e8ac0e4f197d")
                .build();

        jClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(Fleetyards::updateUsers)
                .join();

    }

    //This function requests the specified user's ships from Fleetyards
    public static void requestShips(String username) {
        HttpClient jClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.fleetyards.net/v1/vehicles/" + username + "?page=1&perPage=200")).build();

        userShips.clear();
        jClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(Fleetyards::updateUserShips);
    }

    //This function updates the fleetUsers ArrayList and should only be called by requestUsers()
    private static String updateUsers(String responseBody) {
        JSONArray users = new JSONArray(responseBody);
        fleetUsernames.clear();

        for (int i = 0; i < users.size(); i++) {
            JSONObject user = users.getJSONObject(i);

            try {
                fleetUsernames.add(user.getStr("username"));
            } catch (Exception e) {
                fleetUsernames.add("Unknown" + i);
            }
        }

        return null;
    }

    //This functions gathers the name of every ship a user owns and stores it in temporary ArrayList userShips
    private static String updateUserShips(String responseBody) {
        if (responseBody.equals("{\"code\":\"not_found\",\"message\":\"Not found\"}")) {
            userShips.add("break");
            return null;
        }

        JSONArray ships = new JSONArray(responseBody);

        for (int i = 0; i < ships.size(); i++) {
            JSONObject ship = ships.getJSONObject(i);
            JSONObject data = ship.getJSONObject("model");

            try { userShips.add(data.getStr("name")); } catch (Exception e) { userShips.add("Unknown"); }
        }
        return null;
    }

    //This function returns a formatted String containing the names of every Fleetyards user in DAEINT
    public String listUsers() {
        String ret = "| ";
        for (String fleetUsername : fleetUsernames) {
            ret += "`" + fleetUsername + "` | ";
        }
        return ret;
    }

    //This function returns a formatted list of the specified user's ships for use in an embed
    public static String listUserShips(String username) {
        for (int i = 0; i < fleetUsers.size(); i++) {
            if (fleetUsers.get(i).getUsername().equals(username)) {
                return fleetUsers.get(i).listUserShips();
            }
        }
        return "unknown_user";
    }
}
