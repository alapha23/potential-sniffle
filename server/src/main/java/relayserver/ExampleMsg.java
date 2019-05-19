package relayserver;

public class ExampleMsg {
    public String dst = ""; // channel id
//    String src = ""; // we don't need src for now.
    public String op = "";
    // n - new client, whose id specified by data;
    // s - send msg to existing client;
    // r - remove client, whose id specified by data.
    // f - flag on whether previous intruction was sucessful or not
    // c - list of all clients, ids in data
    public String data = "";
}
