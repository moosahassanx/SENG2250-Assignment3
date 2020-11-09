import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client 
{
    // attributes
    private static String clientID;

    // constructor
    public Client(String c) 
    {
        Client.clientID = c;
    }

    public static void main(String args[]) throws IOException
    {
        // user has not inputted values in command line
        if (args.length < 2) 
        {
            return;
        }

        // java Clint [hostname] [port]
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        Scanner userInput = new Scanner(System.in);

        try (Socket socket = new Socket(hostname, port)) 
        {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            // receiving message from server
            String receivedMessage = reader.readLine();
            System.out.println(receivedMessage);

            // set up message to send to server
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            // receiving rsa public key from server
            String ServerRsaPublicKey = reader.readLine();
            System.out.println("PUBLIC KEY RECEIVED FROM SERVER:" + ServerRsaPublicKey);

            // getting "hello" input from client
            System.out.print("Client_Hello (input client ID): ");
            clientID = userInput.nextLine();
            writer.println(clientID);         // send "hello" to server            

        }
        // server could not be found
        catch(UnknownHostException ex)
        {
            System.out.println("SERVER NOT FOUND: " + ex.getMessage());
        }
        // input error
        catch(IOException ex)
        {
            System.out.println("I/O ERROR: " + ex.getMessage());
        }

        userInput.close();
    }
}