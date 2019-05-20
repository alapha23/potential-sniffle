package relayserver;

public class ExampleMsg {
    public String op = "";
    // n - new client, whose id specified by data; from client to server
    // s - send msg to existing client;
    // f - flag on whether previous intruction was sucessful or not
    public String dst = ""; // channel id
    //    String src = ""; // we don't need src for now.

    public String data = "";
}
