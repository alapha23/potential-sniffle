package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class client {

    public static void main(String[]args){
        System.out.println("I am a client!");
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

            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.print("> ");
                String buffer = sc.nextLine();

                channel.writeAndFlush(Unpooled.copiedBuffer(buffer, CharsetUtil.UTF_8));
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
