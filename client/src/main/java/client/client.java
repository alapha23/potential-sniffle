package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

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
            ChannelFuture channelFuture = clientBootstrap.connect().sync();
            channelFuture.channel().closeFuture().sync();
        } catch(Exception e) {
            System.out.println("Bootstrap creation failed or channel init failed");
        }finally {
            try {
                group.shutdownGracefully().sync();
            } catch(Exception e) {System.out.println("Shutdown gracelessly");}
        }
    }
}
