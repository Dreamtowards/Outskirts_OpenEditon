package outskirts.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import outskirts.util.logging.Log;

public class FrameEncoder extends MessageToByteEncoder<ByteBuf> {

    /**
     * +---------------------+
     * |  data.length int32  |
     * +---------------------+
     * |                     |
     * |  data               |
     * |                     |
     * +---------------------+
     */

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        try {

            int frameLength = msg.readableBytes();
            out.writeInt(frameLength);
            out.writeBytes(msg);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
