package com.company;

import java.net.Socket;

import java.io.*;

/**
 * This class implements the client side of the model specified in assignment 1)
 */
public class GreetClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    /**
     * This method creates a socket, an input stream and an output stream
     * @param ip a String specifying the IP-Address for the socket to connect to
     * @param port an Integer specifying the port number for the socket to connect to
     * @return a Boolean specifying if the connection of the socket was successful
     * @throws IOException in case of errors regarding socket input/output streams
     */
    public boolean startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        return clientSocket.isConnected();
    }

    /**
     * This method handles sending messages via the output stream as well as
     * storing and returning messages received in response over the input stream
     * @param msg a String holding the text for the output stream of the client
     * @return a StringBuilder holding the text read from the input stream of the client
     * @throws IOException in case of errors regarding input/output streams
     */
    public StringBuilder sendMessage(String msg) throws IOException {
        out.println(msg);
        String resp = "";
        StringBuilder stringBuilder = new StringBuilder();
        if(msg.length() >3 && msg.substring(0,3).equals("GET")) {
            while (true) {
                String newLine;
                newLine = in.readLine();

                if (newLine.equals("~~~END~~~")) {
                    return stringBuilder;
                }
                else{stringBuilder.append(newLine);
                    stringBuilder.append(System.getProperty("line.separator"));
                }
            }
        }
            else {
                stringBuilder.append(in.readLine());


            }
        return stringBuilder;

    }

    /**
     * This method stops the connection by closing the input stream, output stream and
     * lastly the socket
     * @throws IOException in case of errors regarding input/output streams
     */
    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}