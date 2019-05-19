package relayserver;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

public class ClientOp {

    //String src;
    //String dest;
    String remoteAddr;
    String msg;

    public ClientOp(ByteBuf inbuf) {
        // expecting json containing srcID, dstID, data
        this.msg = inbuf.toString(CharsetUtil.UTF_8);
    }

    String parseDst() {
        Gson gson = new Gson();
        try {
            ExampleMsg msg = gson.fromJson(this.msg, ExampleMsg.class);
            return msg.dst;
        } catch (Exception e) {
            System.out.println("Unexpected msg " + this.msg);
        }
        // TODO grace exit needed
        System.exit(1);
        return "";
    }
}
