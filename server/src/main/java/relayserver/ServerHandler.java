package relayserver;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.DefaultEventExecutor;


public class ServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final ChannelGroup channels = new DefaultChannelGroup(new DefaultEventExecutor());
    Gson gson = new Gson();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        Channel incoming = ctx.channel();

        // DEBUG
        incoming.writeAndFlush("DEBUG\r\n");
        System.out.println("New client "+incoming.id().toString()+" connected, isWritable: "+incoming.isWritable());
        // broadcast new joined client channel id to all channels
        ExampleMsg msg =  new ExampleMsg();
        msg.op = "n";
        msg.data = incoming.id().toString();
        for (Channel channel : channels) {
            channel.writeAndFlush(gson.toJson(msg, ExampleMsg.class));
        }

        // reply with a list of existing clients
        msg.op = "c";
        msg.data = "";
        for (Channel channel : channels) {
                msg.data += channel.id().toString()+" ";
        }
        // DEBUG
        System.out.println("Send contacts "+msg.data);
        incoming.writeAndFlush(gson.toJson(msg, ExampleMsg.class));

        // update channel list
        channels.add(incoming);
        // TODO: check for channel id collision?
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();

        // broadcast newly left channel to all
        ExampleMsg msg = new ExampleMsg();
        msg.op = "r";
        msg.data = incoming.id().toString();
        for (Channel channel : channels) {
            channel.writeAndFlush(gson.toJson(msg, ExampleMsg.class));
        }
        // update channel list
        channels.remove(incoming);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf inBuffer) throws Exception {
        // Expects a json containing dst, op, data

        ClientOp n = new ClientOp(inBuffer);

        // parse json
        String dst = n.parseDst();
        int flag = 0;

        // send msg
        for (Channel channel : channels) {
            if(channel.id().toString().equals(dst)) {
                channel.writeAndFlush(Unpooled.copiedBuffer(inBuffer.toString(), CharsetUtil.UTF_8));
                flag = 1;
            }
        }

        // Reply success or not
        ExampleMsg msg = new ExampleMsg();
        msg.op = "f";
        msg.data = flag==1?"Success":"Failure. Invalid command";
        ctx.writeAndFlush(Unpooled.copiedBuffer(gson.toJson(msg, ExampleMsg.class), CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        channels.close();
        ctx.close();
    }

}

