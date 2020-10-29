package com.company2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <h1>Peer-to-Peer weather station</h1>
 * The Main program implements an application that simply starts a new weather station which is able to communicate
 * with other weather stations and exchanges the newest data.
 *
 * @author  Carlos Koenig, Jonas Richter
 * @version 1.0
 * @since   2020-01-15
 */

public class Main {

    /**
     * This is the main method which makes use of the Client Class.
     * It initializes one instance of Client and schedules a timer to refresh the DataSnippet of the Client every second
     * as well as sending new request to other Clients for the latest DataSnippets.
     * It also initializes the Clients receive() method to receive messages.
     * If "end" is typed into the console the Client will stop and will return the currently hold values.
     *
     * @param args used to give the Current weather station a name/location and make the weather station stop and
     *            print out its current values in the console.
     * @return Nothing.
     * @exception IOException On input error.
     * @see IOException
     */
    public static void main(String[] args) throws IOException {

        System.out.println("Zum Starten bitte einen Standort angeben. Zum Beenden 'end' eingeben.");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String location = reader.readLine();
        Client client = new Client(location);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                client.setCurrentSnippet();
                try {
                    client.sendRequest();
                } catch (IOException ignore) {}
            }
        }, 1000, 1000);

        Runnable runnable = () -> {
            try {
                client.receiveRequest();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

        Scanner input = new Scanner(System.in);
        String output = "";
        while(true) {
            String line = input.nextLine();
            if (line.equals("end")) {
                output = output + "Standort: " + client.location
                    + " Zeitstempel: " + client.getCurrentSnippet().getTimestamp()
                    + " aktuellste Temperatur: " + client.getCurrentSnippet().getTemperature() + "°C"
                    + " aktuellste Luftfeuchtigkeit: " + client.getCurrentSnippet().getHumidity() + "%";
                timer.cancel();
                for (DataSnippet snippet : client.getAllSnippets()
                ) {
                    output = output + "\n"
                            + "Standort: " + snippet.getLocation()
                            + " Zeitstempel: " + snippet.getTimestamp()
                            + " aktuellste Temperatur: " + snippet.getTemperature() + "°C"
                            + " aktuellste Luftfeuchtigkeit: " + snippet.getHumidity() + "%";
                }
                System.out.println(output);
                thread.interrupt();
                break;
            }
        }
        System.out.println("ending");
        System.exit(0);
    }
}
