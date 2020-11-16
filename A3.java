class A3 {
    public static void main(String args[])
    {
        // initiatlise threads
        Server server = new Server();
        Client client = new Client();

        // run threads
        server.start();
        client.start();
    }
}
