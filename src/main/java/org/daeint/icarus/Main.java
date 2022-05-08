package org.daeint.icarus;

import vip.floatationdevice.guilded4j.G4JClient;

import java.util.Scanner;

//Main acts as a foundation for both Icarus and the GuildedEventHandler
//This class contains the main() function, as well as an interactive testing console
public class Main {

    static Scanner in = new Scanner(System.in);
    static G4JClient client = new G4JClient("VM2Whjd++2MJIjPuv5j5nMudLcJGeTBdEA4+NuydcOxQ8u3GGEAEwIxfLSuG9KGdSjRgQndMXNeAHQiHsKJL/g==");

    public static void main(String[] args) {

        client.ws.eventBus.register(new GuildedEventHandler(client));
        System.out.println("Connecting");
        client.ws.connect();

        for (; ; ) {    //This loop is what creates the testing console

            String input = in.nextLine();

            switch(input) {

                case "exit":
                    client.ws.close();
                    System.exit(0);
                    break;
                case "reconnect":
                    System.out.println("Reconnecting");
                    client.ws.reconnect();
                    break;
                default:
                    System.out.println("Available commands: exit, reconnect");

            }

        }

    }

}
