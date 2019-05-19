package client;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import relayserver.ExampleMsg;

import java.util.ArrayList;
import java.util.Arrays;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    Gson gson = new Gson();
    ArrayList clients = new ArrayList<String>();
    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext){

   }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object inBuf) {
        // n - new client, whose id specified by data; add it to our channel list
        // s - send msg to existing client; - not needed now, since client sends to server only for messaging
        // r - remove client, whose id specified by data.
        // f - flag on whether previous instruction was sucessful or not
        // c - receive list of all clients, ids in data. needed for new clients
        ByteBuf bytebuf = (ByteBuf)inBuf;
        ExampleMsg msg = gson.fromJson(bytebuf.toString(), ExampleMsg.class);
        System.out.println("Received "+bytebuf.toString());
        switch (msg.op.charAt(0)) {
            case 'n':
                clients.add(msg.data);
                break;
            case 's':
                // receive msg from others
                System.out.println(msg.data);
                break;
            case 'r':
                clients.remove(msg.data);
                break;
            case 'f':
                System.out.println("Message sending " + msg.data);
                // TODO: exception handling
                break;
            case 'c':
                String contacts[] = msg.data.split(" ");
                clients = new ArrayList<String>(Arrays.asList(contacts));

                // DEBUG
                System.out.println("Received list of all connected clients");
                System.out.println(Arrays.toString(clients.toArray()));
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause){
        cause.printStackTrace();
        channelHandlerContext.close();
    }

}
