package com.company;
import com.company.GreetClient;
import java.io.IOException;
import java.util.Scanner;

/**
 * The main class of this project implements the control flow
 * for the client side of assignment 1)
 */
public class Main {
    /**
     * The main method handles user input from the terminal and (depending on said input):
     *  - the creation of a new client
     *  - it's connection to a server at a specific port number
     *  - the communication over said connection
     *  - the closing of the connection
     * @param args there are no String arguments passed to the main method
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        String await = "";
        boolean connectionMade = false;
        GreetClient client = null;
        Integer port = 0;

        System.out.println("Welcome! \n start connection to a Server? -> enter y || Y ");
        while (!(await.equals("y") || await.equals("Y"))) {
            await = scanner.nextLine();
        }


        boolean success = false;
        /*
        while no connection is established, request the user to input a valid port
        number, then create a new client and try to connect to a local server on
        the port number provided by the user
         */
        while(connectionMade == false) {


            while(!success || (port < 50000 || port > 51001)) {
                System.out.println("Enter a port number between 50000 and 51000!");
                success = false;
                try {
                    port = scanner.nextInt();
                    success = true;
                    scanner.nextLine();

                } catch (Exception e) {
                    System.err.format("Bitte Zahl eingeben! \n");
                    scanner.next();
                }
            }


                client = new GreetClient();
                try {
                    connectionMade = client.startConnection("127.0.0.1", port);
                } catch (Exception e) {
                    System.err.format("Fehler beim Verbinden! \n");
                    e.printStackTrace();
                    port = 0;
                }
        }

        String message = "";
        System.out.println("- LIST => listFiles \n - GET filename => getFile \n - QUIT => endConnection");
        /*
        if a connection to the server was established, allow the user to send commands
        via the client's output stream, print response from input stream after
        every user input, when the user enters "QUIT" command close connection to the server
         */
        if(connectionMade) {
            while (!(message.equals("QUIT"))) {
                System.out.println("enter your command!");
                message = scanner.nextLine();
                StringBuilder response = client.sendMessage(message);
                if (message == "QUIT") break;

                System.out.println(response);
            }
            client.stopConnection();
        }
    }
}
