package com.company;
import com.company.EchoServer;

import java.io.IOException;

/**
 * The main class of this project starts off the server side of assignment 1)
 */
public class Main {
    /**
     * The main method simply instantiates an EchoServer object and runs it's
     * start method
     * @param args passing of String arguments to main method is not utilized
     * @throws IOException in case of errors regarding input/output streams
     */
    public static void main(String[] args) throws IOException {
        EchoServer server2 = new EchoServer();
        server2.start(50000);
    }
}
