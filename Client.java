import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client 
{
    public static BigInteger bigPow(BigInteger base, BigInteger exponent) 
    {
        BigInteger result = BigInteger.ONE;

        while (exponent.signum() > 0) 
        {
            if (exponent.testBit(0)) 
                result = result.multiply(base);
            base = base.multiply(base);
            exponent = exponent.shiftRight(1);
        }

        return result;
    }

    public static void main(String args[]) throws IOException 
    {
        // attributes
        BigInteger xB = new BigInteger("7");
        BigInteger publicYB = null;
        BigInteger publicYA = null;
        BigInteger sessionKBA = null;

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

            KeyGenerator keyGen = new KeyGenerator();

            // receiving message from server
            String receivedMessage = reader.readLine();
            System.out.println("\n RECEIVED Server yA: " + receivedMessage);
            publicYA = new BigInteger(receivedMessage);

            // set up message to send to server
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            // calculate public key yB
            System.out.println("\n calculating public key yB...");
            publicYB = keyGen.powmod4(keyGen.getG(), xB, keyGen.getP());
            System.out.println("\n publicYB:" + publicYB);

            // calculate session key
            System.out.println("\n calculating session key");
            sessionKBA = publicYA.pow(xB.intValue());
            System.out.println("\n SESSION KBA: " + sessionKBA);
            keyGen.setDHPublicKey(sessionKBA);

            // send public key yA to client
            writer.println(publicYB);

            // receiving rsa public key from server
            String ServerRsaPublicKey = reader.readLine();
            System.out.println("\n PUBLIC KEY RECEIVED FROM SERVER:" + ServerRsaPublicKey);

            // getting "hello" input from client
            System.out.print("\n Client_Hello (input client ID): ");
            String clientID = userInput.nextLine();
            writer.println(clientID);         // send "hello" to server

            // receiving "hello" input from server
            String server_Hello = reader.readLine();
            System.out.println("\n RECEIVED HELLO FROM SERVER:" + server_Hello);
        }
        // server could not be found
        catch(UnknownHostException ex)
        {
            System.out.println("\n SERVER NOT FOUND: " + ex.getMessage());
        }
        // input error
        catch(IOException ex)
        {
            System.out.println("\n I/O ERROR: " + ex.getMessage());
        }

        userInput.close();
    }
}