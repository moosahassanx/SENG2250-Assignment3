import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class Server
{

    public static void main(String args[]) throws NoSuchAlgorithmException, NoSuchProviderException 
    {
        // attributes
        BigInteger xA = new BigInteger("13");
        BigInteger publicYA = null;
        BigInteger publicYB = null;
        BigInteger sessionKAB = null;


        // user must input a port number through command line
        if (args.length < 1) 
        {
            return;
        }

        int port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port))
        {
            System.out.println("Server is listening on port: " + port);

            while (true) 
            {
                KeyGenerator keyGen = new KeyGenerator();

                // every time a new client joins the server, output join message
                Socket socket = serverSocket.accept();
                System.out.println("Setup_Request: Hello");

                // setting up READING and WRITING
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                // calculate public key yA
                System.out.println("\n calculating public key yA...");
                publicYA = keyGen.powmod4(keyGen.getG(), xA, keyGen.getP());
                System.out.println("\n publicYA:" + publicYA);
                writer.println(publicYA);                           // send public key yA to client

                // receive public key yB
                String receivedMessage = reader.readLine();
                System.out.println("\n RECEIVED public key yB: " + receivedMessage);
                publicYB = new BigInteger(receivedMessage);

                // calculate session key
                System.out.println("\n calculating session key");
                sessionKAB = keyGen.powmod4(publicYB, xA, keyGen.getP());
                System.out.println("\n SESSION KAB: " + sessionKAB);
                keyGen.setDHPublicKey(sessionKAB);

                // generate keys             
                String rsaPublicKey = keyGen.generateRSAPublic("raris and rovers, these hoes love chief sosa, hit em widda cobra");
                String rsaPrivateKey = keyGen.generateRSAPrivate();
                System.out.println("\n PRIVATE KEY: " + rsaPrivateKey);

                // send to client
                writer.println(rsaPublicKey);
                System.out.println("\n Public key has been issued and sent to client");

                // receive client ID from client
                receivedMessage = reader.readLine();
                System.out.println("\n RECEIVED Client_Hello: " + receivedMessage);

                // send Server_Hello
                String server_Hello = "24F9782, 0H8FD1";
                writer.println(server_Hello);
                System.out.println("\n Server_Hello has been sent to client.");

            }
        }
        catch (IOException ex)
        {
            System.out.println("\n Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}