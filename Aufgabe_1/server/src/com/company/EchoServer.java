package com.company;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.*;
import com.company.FileContentReader;

/**
 * The EchoServer class implements the server side of the model specified in assignment 1)
 */
public class EchoServer {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    /**
     * The start method creates a new server socket, listens for and accepts connections from clients to
     * the socket, creates input and output streams for client-server communication and
     * utilizes an instance of the FileContentReader class to handle local files
     * @param port an Integer holding the port number for the server to listen on
     * @throws IOException
     */
    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server listening on port: " + port);


        while (true) {
            clientSocket = serverSocket.accept();
            System.out.println("New connection to client");
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            FileContentReader fileReader = new FileContentReader();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                //if LIST is received from client write list of file names to output stream
                if ("LIST".equals(inputLine)) {
                    out.println(fileReader.getFileNames());
                    System.out.println("Client requested List of Files");

                }
                //if GET is received from client, print content of specified file to output stream
                else if (inputLine.length() > 3 && "GET".equals(inputLine.substring(0, 3))) {

                        String fileName = inputLine.substring(4);
                        for (int i = 0; i < fileReader.getFileContent(fileName).size(); i++) {
                            out.println(fileReader.getFileContent(fileName).get(i));
                        }
                        out.println("~~~END~~~");
                        System.out.println("client requested specific filename: " + inputLine.substring(4));
                }
                //if QUIT is received from client break from writing the input stream text commands
                else if ("QUIT".equals(inputLine)) {
                    out.println("good bye");
                    System.out.println("client closed Connection");
                    break;
                }
                else {
                    out.println("i don't understand");
                }

                System.out.println(inputLine);
            }
        }
    }
}
