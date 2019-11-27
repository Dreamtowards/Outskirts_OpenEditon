package outskirts.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import outskirts.util.logging.Log;

import java.util.List;

public class FrameDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {

        if (in.readableBytes() < 4) { //not enough a Integer
            return;
        }

        in.markReaderIndex();

        int frameLength = in.readInt();
        if (in.readableBytes() < frameLength) { //not a completed frame
            in.resetReaderIndex();
            return;
        }

        list.add(in.readBytes(frameLength));
    }
}
