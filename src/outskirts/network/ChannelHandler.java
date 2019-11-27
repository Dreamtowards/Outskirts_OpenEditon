package outskirts.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import outskirts.event.EventBus;
import ext.testing.CPacketStrMsg;
import outskirts.network.codec.FrameDecoder;
import outskirts.network.codec.FrameEncoder;
import outskirts.network.codec.PacketDecoder;
import outskirts.network.codec.PacketEncoder;
import outskirts.util.Side;
import outskirts.util.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class ChannelHandler extends SimpleChannelInboundHandler<Packet> {

    private Side side;

    private Channel channel;

    /**
     * Packet EventBus in this Channel
     */
    private EventBus channelBus = new EventBus();

    public ChannelHandler(Side side) {
        this.side = side;
        channelBus.register(CPacketStrMsg.class, p -> {
            Log.info(side+": "+p.getMsg());
        });
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channel = ctx.channel();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        cause.printStackTrace();

        closeChannel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        channelBus.post(packet);
    }

    public Channel channel() {
        return channel;
    }

    public void closeChannel() {
        if (channel.isOpen()) {
            channel.close().syncUninterruptibly();
        }
    }

    public ChannelFuture sendPacket(Packet packet) {
        return channel.writeAndFlush(packet);
    }



    private static final EventLoopGroup CLIENT_NIO_EVENTLOOP = new NioEventLoopGroup(0, new DefaultThreadFactory("Netty Client IO #", true));
    private static final EventLoopGroup SERVER_NIO_EVENTLOOP = new NioEventLoopGroup(0, new DefaultThreadFactory("Netty Server IO #", true));

    /**
     * both Server AND Client connections
     */
    private static final List<ChannelHandler> connections = new ArrayList<>();

    @SuppressWarnings("all")
    public static void bindServerEndpoint(int port) throws InterruptedException {

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(SERVER_NIO_EVENTLOOP, SERVER_NIO_EVENTLOOP)
                .channel(NioServerSocketChannel.class) //SO_KEEPALIVE, AUTO_READ, TCP_NODELAY
//                .handler(new LoggingHandler())
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelHandler channelHandler = new ChannelHandler(Side.SERVER);
                        channel.pipeline()
                                .addLast("frame_decoder", new FrameDecoder())
                                .addLast("frame_encoder", new FrameEncoder())
                                .addLast("packet_decoder", new PacketDecoder())
                                .addLast("packet_encoder", new PacketEncoder())
                                .addLast("handler", channelHandler);

                        synchronized (connections) {
                            connections.add(channelHandler);
                        }
                    }
                });

        bootstrap.bind(port).sync();
    }

    @SuppressWarnings("all")
    public static ChannelHandler createConnection(String host, int port) throws InterruptedException {

        ChannelHandler channelHandler = new ChannelHandler(Side.CLIENT);

        Bootstrap bootstrap = new Bootstrap()
                .group(CLIENT_NIO_EVENTLOOP)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast("frame_decoder", new FrameDecoder())
                                .addLast("frame_encoder", new FrameEncoder())
                                .addLast("packet_decoder", new PacketDecoder())
                                .addLast("packet_encoder", new PacketEncoder())
                                .addLast("handler", channelHandler);

                        synchronized (connections) {
                            connections.add(channelHandler);
                        }
                    }
                });

        bootstrap.connect(host, port).sync();

        //TODO waiting channel for open..?

        return channelHandler;
    }
}
