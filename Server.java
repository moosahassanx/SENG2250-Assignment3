import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.LocalDate;

public class Server
{
    public static void main(String args[]) throws NoSuchAlgorithmException, NoSuchProviderException 
    {
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
                // every time a new client joins the server, output join message
                Socket socket = serverSocket.accept();
                System.out.println("Setup_Request: Hello");

                // send message to client of when joined
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                LocalDate dateObj = LocalDate.now();
                writer.println("Connection date: " + dateObj.toString());

                // setting up writing and reading
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                // generate keys
                KeyGenerator keyGen = new KeyGenerator();
                String rsaPublicKey = keyGen.generateRSAPublic("raris and rovers, these hoes love chief sosa, hit em widda cobra");
                String rsaPrivateKey = keyGen.generateRSAPrivate();
                System.out.println("\n PRIVATE KEY: " + rsaPrivateKey);

                // send to client
                writer.println(rsaPublicKey);
                System.out.println("\n Public key has been issued and sent to client");

                // receive client ID from client
                String receivedMessage = reader.readLine();
                System.out.println("\n RECEIVED Client_Hello: " + receivedMessage);

                // send Server_Hello
                String server_Hello = "24F9782, 0H8FD1";
                writer.println(server_Hello);
                System.out.println("\n Server_Hello has been sent to client.");

                // DIFFIE HELLMAN

            }
        }
        catch (IOException ex)
        {
            System.out.println("\n Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}