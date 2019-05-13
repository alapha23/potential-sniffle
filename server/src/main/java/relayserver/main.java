package relayserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class main {

    public static void main(String args[]) {

        System.out.println("Hello World");

        // 1. create event loop group
        EventLoopGroup group = new NioEventLoopGroup();

        try{
            // 2. create and configure ServerBootStrap
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.localAddress(new InetSocketAddress("localhost", 9999));

            // 3. create ChannelInitializer and attach it to the ServerBootStrap instance
            // initChannel is called whenever a new incoming TCP connection is accepted by tcp server
            // each accepted channel socket is a child of the server socket
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new HelloServerHandler());
                }
            });
            // boot the Netty server
            // sync() blocks until server has started
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            channelFuture.channel().closeFuture().sync();
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            //
            try {
                group.shutdownGracefully().sync();
            } catch(Exception e)
            {
                System.out.println("Failed to shut down relay server");
            }
        }

    }


}