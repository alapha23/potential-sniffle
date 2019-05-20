package client;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import relayserver.ExampleMsg;

import java.util.Scanner;

import static client.client.canSend;

public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf>{
    Gson gson = new Gson();
    public static String clientID = "";

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext){
        Scanner sc = new Scanner(System.in);
        ExampleMsg msg = new ExampleMsg();

        while(clientID.length() == 0) {
            System.out.print("Enter Client Name: ");
            clientID = sc.nextLine();
        }
        // register new client to the server
        msg.op = "n";
        msg.data = clientID;

        channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer(gson.toJson(msg, ExampleMsg.class), CharsetUtil.UTF_8));
        canSend = 1;
   }

    @Override
    public void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf bytebuf) throws Exception{
        // s - send msg to existing client; - not needed now, since client sends to server only for messaging
        // f - flag on whether previous instruction was sucessful or not
        final ByteBufInputStream bis = new ByteBufInputStream(bytebuf);

        String strbuf = bis.readLine();
        ExampleMsg msg = gson.fromJson(strbuf, ExampleMsg.class);
        switch (msg.op.charAt(0)) {
            case 's':
                // receive msg from others
                System.out.println("[CLIENT] " + msg.data);
                break;
            case 'f':
                System.out.println(msg.data);
                break;
            default:
                throw new UnsupportedOperationException();
        }
  //      bytebuf.release();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause){
        cause.printStackTrace();
        channelHandlerContext.close();
    }

}
