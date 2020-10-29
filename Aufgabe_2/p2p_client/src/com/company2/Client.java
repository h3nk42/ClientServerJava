package com.company2;

import java.io.IOException;
import java.net.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * <h1>Client</h1>
 * The Client class implements a Client that is able to bind itself to a free port in the port range 50001 to 50010,
 * to send request for the data of other clients via a DatagramSocket in the port range and to receive the requested data and requests of
 * other clients.
 * <p>
 * <b>Note:</b> There can only be 10 different Clients working parallel on one Computer due to its characteristics that
 * it bounds itself to one free port.
 *
 * @author Carlos Koenig, Jonas Richter
 * @version 1.0
 * @since 2020-01-15
 */
public class Client {

    public String location;
    private DataSnippet currentSnippet;
    private ArrayList<DataSnippet> allSnippets = new ArrayList<>();
    private float[] tempRange;
    private float[] humRange;
    private DatagramSocket receiver;
    private int iterator = 0;

    /**
     * This method is used to get the latest DataSnippet of this Client.
     *
     * @return the latest DataSnippet
     */

    public DataSnippet getCurrentSnippet() {
        return currentSnippet;
    }

    /**
     * This method is used to get all DataSnippets that have been collected from other Clients.
     * <b>Note:</b> This will  not return any Snippet which has the same location as the variable location.
     *
     * @return ArrayList of DataSnippets which contains all DataSnippets that hat have been collected.
     */
    public ArrayList<DataSnippet> getAllSnippets() {
        return allSnippets;
    }

    public Client(String location) {
        System.out.println("Wetterstation aufgestellt.");
        this.location = location;
        // constructs the cyclic changing values of the Client
        tempRange = new float[10];
        humRange = new float[10];
        for (int i = 0; i < tempRange.length; i++) {
            float randTemp = (float) Math.random();
            float randHum = (float) Math.random();
            tempRange[i] = randTemp * 60 - 20;
            humRange[i] = randHum * 45 + 50;
        }
        currentSnippet = new DataSnippet(this.location, tempRange[0], humRange[0], LocalDateTime.now());
        for (int j = 50001; j <= 50010; j++) {
            try {
                this.receiver = new DatagramSocket(j, InetAddress.getLocalHost());
                break;
            } catch (SocketException | UnknownHostException ignored) {

            }
        }
    }

    /**
     * This method is used to generate a new DataSnippet.
     *
     * @return nothing
     */
    public void setCurrentSnippet() {
        if (iterator == tempRange.length - 1) {
            iterator = 0;
        } else {
            iterator++;
        }
        this.currentSnippet = new DataSnippet(location, tempRange[iterator], humRange[iterator], LocalDateTime.now());
    }

    /**
     * This method is used to read and process the current valid messages that have been received.
     * In case of a data request it answers the request by sending the latest snippet of itself together with all the
     * DataSnippets it hold from other Clients.
     * In case of new Data it parses the byte array into DataSnippets and checks if it already has the DataSnippets that
     * has been received or if the current DataSnippets are up-to-date.
     *
     * @return nothing
     * @throws IOException if sending or receiving a message via send() or received() failed.
     * @see DatagramSocket
     */
    public void receiveRequest() throws IOException {
        while (true) {
            // set datagramPacket with a length of 10000 bytes and set timeout to 3 seconds
            DatagramPacket message = new DatagramPacket(new byte[10000], 10000);
            receiver.setSoTimeout(3000);

            // receive message
            try {
                receiver.receive(message);
            } catch (SocketTimeoutException ignored) {}
            // extract the string of the incoming message
            String textMessage = new String(message.getData()).trim();
            // checks if message is empty
            if (!textMessage.equals("")) {
                // checks if the message is a DataRequest
                if (textMessage.equals("SendDATA")) {
                    // formats the response for the DataRequest in string response
                    String stringResponse = "D" + currentSnippet.getLocation() + "," + currentSnippet.getTemperature()
                            + "," + currentSnippet.getHumidity() + "," + currentSnippet.getTimestamp().toString();
                    for (DataSnippet snippet : allSnippets) {
                        stringResponse = stringResponse + "~" + snippet.getLocation() + "," + snippet.getTemperature()
                                + "," + snippet.getHumidity() + "," + snippet.getTimestamp().toString();
                    }
                    // constructs the DatagramPacket for the response of the DataRequest and sends it
                    DatagramPacket response = new DatagramPacket(stringResponse.getBytes(),
                            stringResponse.getBytes().length, message.getAddress(), message.getPort());
                    receiver.send(response);

                } else if (textMessage.charAt(0) == 'D') {
                    String dataString = textMessage.substring(1);
                    // splits the string of the response into its string representations of a instance of DataSnippet
                    String[] dataStringArray = dataString.split("~");
                    for (String dataSnippetString : dataStringArray) {
                        // splits every string representation of a instance of DataSnippet into its values
                        String[] dataSnippetStringArray = dataSnippetString.split(",");
                        DataSnippet newSnippet = new DataSnippet(dataSnippetStringArray[0],
                                Float.parseFloat(dataSnippetStringArray[1]),
                                Float.parseFloat(dataSnippetStringArray[2]),
                                LocalDateTime.parse(dataSnippetStringArray[3]));
                        // checks if the client has other DataSnippets than his own
                        if (allSnippets.size() != 0) {
                            ArrayList<DataSnippet> temp = new ArrayList<>();

                            // iterates over all DataSnippets to check if there is a newer DataSnippet
                            for (DataSnippet snippet : allSnippets) {

                                // checks if snippets location is the same than the new one
                                if (snippet.getLocation().equals(newSnippet.getLocation())) {

                                    // if the newSnippet is more up-to-date than snippet then it will be replaced with
                                    // newSnippet.
                                    if (newSnippet.getTimestamp().compareTo(snippet.getTimestamp()) == 1) {
                                        allSnippets.set(allSnippets.indexOf(snippet), newSnippet);
                                    }
                                } else {

                                    // if the newSnippet is not the same as snippet it checks if the location of
                                    // newSnippet is already in allSnippets and adds it if its new to the Client
                                    ArrayList<String> locations = new ArrayList<>();
                                    for (DataSnippet locationSnippet : allSnippets) {
                                        locations.add(locationSnippet.getLocation());
                                    }
                                    for (DataSnippet tmpSnip : temp) {
                                        locations.add(tmpSnip.getLocation());
                                    }
                                    if (!newSnippet.getLocation().equals(location) &&
                                            !locations.contains(newSnippet.getLocation())) {
                                        temp.add(newSnippet);
                                    }
                                }

                            }
                            allSnippets.addAll(temp);
                        } else {
                            // allSnippets is empty it just adds the newSnippet to allSnippets
                            allSnippets.add(newSnippet);
                        }
                    }
                }
            }
        }
    }

    /**
     * This method is used to send data request to every other port in the port range except the port the socket is
     * currently bound to.
     *
     * @return nothing
     * @throws IOException if sending a data request via the send() method failed.
     * @see DatagramSocket
     */
    public void sendRequest() throws IOException {
        for (int i = 50001; i <= 50010; i++) {

            // sends a DatagramPacket that contains the byte Code to the String "SendDATA"
            if (i != receiver.getLocalPort()) {
                String rq = "SendDATA";
                DatagramPacket request = new DatagramPacket(rq.getBytes(), rq.getBytes().length, receiver.getLocalAddress(), i);
                try {
                    receiver.send(request);
                } catch (PortUnreachableException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
