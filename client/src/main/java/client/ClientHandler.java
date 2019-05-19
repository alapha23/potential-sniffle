package client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.io.RandomAccessFile;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    //private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext){
        //System.out.println("Channel activated!");
        //Scanner sc =  new Scanner(System.in);
        //executorService.execute(() -> {
        //    while(true) {
//                System.out.print("> ");
//                String buffer = sc.nextLine();
//                channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer(buffer, CharsetUtil.UTF_8));
        //    }
        //});
   }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("[SERVER] " + in.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause){
        cause.printStackTrace();
        channelHandlerContext.close();
    }

}
