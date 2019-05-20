package relayserver;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.DefaultEventExecutor;
import javafx.util.Pair;

import javax.naming.OperationNotSupportedException;
import java.nio.charset.Charset;
import java.util.ArrayList;


public class ServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static ArrayList<Pair<String, Channel>> clients = new ArrayList<Pair<String, Channel>>();
    Gson gson = new Gson();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        Channel incoming = ctx.channel();

        System.out.println("[SERVER] New client "+incoming.id().toString()+" connected, isWritable: "+incoming.isWritable());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf inBuffer) throws Exception {
        char op = (char)(inBuffer.getByte(7));
        //{"op":"c","dst":"","data":""}

        switch(op) {
            case 'n': {
                // read client name
                final ByteBufInputStream bis = new ByteBufInputStream(inBuffer);
                String strbuf = bis.readLine();
                ExampleMsg msg = gson.fromJson(strbuf, ExampleMsg.class);

                // msg.data is the name
                Pair client = new Pair(msg.data, ctx.channel());
                for (Pair<String, Channel> p : clients) {
                  if(p.getKey().equals(msg.data)){
                      ExampleMsg reply = new ExampleMsg();
                      reply.op = "f";
                      reply.data = "Client connection failed. Client name already in use. Please restart the client";
                      ctx.writeAndFlush(Unpooled.copiedBuffer(gson.toJson(reply, ExampleMsg.class), CharsetUtil.UTF_8));
                      System.out.println("[SERVER] data forwarding "+reply.data);
                      return ;
                  }
                }
                clients.add(client);
                System.out.println("[SERVER] new client added: "+msg.data);
                break;
            }
            case 'r': {
                final ByteBufInputStream bis = new ByteBufInputStream(inBuffer);
                String strbuf = bis.readLine();
                ExampleMsg msg = gson.fromJson(strbuf, ExampleMsg.class);

                Pair client = new Pair(msg.data, ctx.channel());
                clients.remove(client);
                System.out.println("[SERVER] removed "+msg.data);
//                inBuffer.release();
                break;
            }
            case 's': {
                CharSequence seq = inBuffer.getCharSequence(12, 20, CharsetUtil.UTF_8);
                inBuffer.retain();
                // dst":"abcde....",
                String dst = seq.toString().split(":")[1].substring(1);
                dst = dst.substring(0, dst.indexOf('\"'));
                System.out.println("[SERVER] data forward to " + dst);

                // send msg
                int flag = 0;
                for (Pair<String, Channel> p : clients) {
                    if (p.getKey().equals(dst)) {
                        p.getValue().writeAndFlush(inBuffer);
                        flag = 1;
                    }
                }
                // Reply success or not
                ExampleMsg reply = new ExampleMsg();
                reply.op = "f";
                reply.dst = dst;
                reply.data = flag == 1 ? "Success" : "Failure. Invalid destination "+dst;
                ctx.writeAndFlush(Unpooled.copiedBuffer(gson.toJson(reply, ExampleMsg.class), CharsetUtil.UTF_8));
                System.out.println("[SERVER] data forwarding "+reply.data);
                break;
            }
            default:
                throw new OperationNotSupportedException();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //channels.close();
        ctx.close();
    }

}

