package relayserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.DefaultEventExecutor;


public class ServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final ChannelGroup channels = new DefaultChannelGroup(new DefaultEventExecutor());

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();

        incoming.writeAndFlush("[SERVER] - " + "WELCOME!\r\n");

        System.out.println("[SERVER] - " + incoming.remoteAddress() + " has joined\r\n");
        // broadcast new joined address to all channels
        for (Channel channel : channels) {
            channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " has joined\r\n");
        }
        channels.add(incoming);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        // broadcast newly left channel to all
        for (Channel channel : channels) {
            channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + "has left\r\n");
        }
        channels.remove(incoming);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf inBuffer) throws Exception {
        // add to client list
        ClientNode n = new ClientNode(inBuffer);

        String received = inBuffer.toString(CharsetUtil.UTF_8);
        System.out.println("Received: " + received);

        // write back to the channel
        ctx.writeAndFlush(Unpooled.copiedBuffer("Server says " + received, CharsetUtil.UTF_8));
    }

   /* @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("[SERVER] channelReadComplete");
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }*/

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // this channel gets Active
    }
}

