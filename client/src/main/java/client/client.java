package client;

import com.google.gson.Gson;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import relayserver.ExampleMsg;

import java.net.InetSocketAddress;
import java.util.Scanner;

import static client.ClientHandler.clientID;


public class client {

    public static volatile int canSend = 0;
    public static void main(String[]args){
        // create event loop group

        EventLoopGroup group = new NioEventLoopGroup();
        try{
            // create and configure bootstrap
            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap.group(group);
            clientBootstrap.channel(NioSocketChannel.class);
            clientBootstrap.remoteAddress(new InetSocketAddress("localhost", 9999));
            clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new ClientHandler());
                }
            });
            // create channelinitializer
            Channel channel = clientBootstrap.connect().sync().channel();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run()
                {
                    System.out.println("Interrupted. Exit.");
                    ExampleMsg msg = new ExampleMsg();
                    Gson gson = new Gson();
                    msg.op = "r";
                    msg.data = clientID;

                    channel.writeAndFlush(
                            Unpooled.copiedBuffer(gson.toJson(msg, ExampleMsg.class), CharsetUtil.UTF_8));
                    try{
                    group.shutdownGracefully().sync();} catch(Exception e){}
                }
            });

            Scanner sc = new Scanner(System.in);
            while (true) {
                if(canSend == 1) {
                    System.out.println("Please enter destination and data, " +
                        "data surrounded by \", e.g., client2\"This is a data\"");
                    String cli = sc.nextLine();
                    if(cli.length()!=0) {
                        String dst = cli.split("\"")[0];
                        String data = cli.split("\"")[1];

                        ExampleMsg msg = new ExampleMsg();
                        Gson gson = new Gson();

                        msg.op = "s";
                        msg.data = data;
                        msg.dst = dst;
                        channel.writeAndFlush(
                                Unpooled.copiedBuffer(gson.toJson(msg, ExampleMsg.class), CharsetUtil.UTF_8));
                    }
                }
            }
            //channelFuture.channel().closeFuture().sync();
        } catch(Exception e) {
            System.out.println("Bootstrap creation failed or channel init failed. Did you start the server?");
        }finally {
            try {
                group.shutdownGracefully().sync();
            } catch(Exception e) {System.out.println("Shutdown gracelessly");}
        }
    }
}
