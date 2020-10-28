class A3 {
    public static void main(String args[])
    {
        System.out.println("testing main file");

        // Server serveObj = new Server("server_1");
        // Client clientObj = new Client("client_1");
        
        
        System.out.println("Hash function: RANDOM FROM SHA256? OR SMN?");
        System.out.println("Diffie-Hellman Key Exchange parameters (𝒑, 𝒈)");
        System.out.println("𝒑=");
        System.out.println("RANDOM NUMBERS HERE");
        System.out.println("𝒈=");
        System.out.println("RANDOM NUMBERS HERE \n");

        System.out.println("Setup_request: Hello -->" );
        System.out.println("<-- Setup: Server's RSA public key \n");

        System.out.println("=================== HANDSHAKE =======================");
        System.out.println("Client_Hello: IDc -->");
        System.out.println("<-- Server_Hello: IDs, SID");
        System.out.println("<-- Ephemeral DH exchange");
        System.out.println("<-- Finished , check the shared key");
        System.out.println("=================== HANDSHAKE ======================= \n");
        
        System.out.println("<-- Data exchange -->");

    }
}
