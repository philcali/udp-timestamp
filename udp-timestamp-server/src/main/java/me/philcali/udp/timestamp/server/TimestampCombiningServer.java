package me.philcali.udp.timestamp.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import me.philcali.udp.timestamp.buffer.TimestampPtuQueue;
import me.philcali.udp.timestamp.protocol.TimestampPtu;
import me.philcali.udp.timestamp.protocol.TimestampPtuDecoder;

import java.net.InetSocketAddress;

public class TimestampCombiningServer {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossLoopGroup = new NioEventLoopGroup();
        ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(bossLoopGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.AUTO_CLOSE, true)
                .option(ChannelOption.SO_BROADCAST, true);

        TimestampPtuQueue queue = new TimestampPtuQueue(100);

        bootstrap.handler(new ChannelInitializer<DatagramChannel>() {
            @Override
            protected void initChannel(DatagramChannel datagramChannel) {
                ChannelPipeline pipeline = datagramChannel.pipeline();
                TimestampPtuDecoder decoder = new TimestampPtuDecoder();
                pipeline.addLast("decoder", decoder);

                TimestampPtuCombiner handler = new TimestampPtuCombiner(queue);
                pipeline.addLast(new DefaultEventExecutorGroup(Runtime.getRuntime().availableProcessors() - 1), "handler", handler);
            }
        });

        ChannelFuture future = bootstrap.bind(new InetSocketAddress("0.0.0.0", 8084));
        channelGroup.add(future.channel());

        Thread pollingThread = new Thread(() -> {
            while (true) {
                TimestampPtu ptu = queue.poll();
                if (ptu != null) {
                    future.channel().writeAndFlush(new DatagramPacket(Unpooled.wrappedBuffer(ptu.getPayload()), new InetSocketAddress("0.0.0.0", 8085)));
                }
            }
        });

        pollingThread.start();
    }
}
