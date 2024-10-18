package me.philcali.udp.timestamp.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import me.philcali.udp.timestamp.protocol.SandboxPtuDecoder;

import java.net.InetSocketAddress;

public class TimestampSplittingServer {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossLoopGroup = new NioEventLoopGroup();
        ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(bossLoopGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.AUTO_CLOSE, true)
                .option(ChannelOption.SO_BROADCAST, true);

        bootstrap.handler(new ChannelInitializer<DatagramChannel>() {
            @Override
            protected void initChannel(DatagramChannel datagramChannel) throws Exception {
                ChannelPipeline pipeline = datagramChannel.pipeline();
                SandboxPtuDecoder decoder = new SandboxPtuDecoder();
                pipeline.addLast("decoder", decoder);

                TimestampPtuSplitter handler = new TimestampPtuSplitter(new InetSocketAddress("0.0.0.0", 8084), 3);
                pipeline.addLast(new DefaultEventExecutorGroup(Runtime.getRuntime().availableProcessors() - 1), "handler", handler);
            }
        });

        ChannelFuture future = bootstrap.bind(new InetSocketAddress("0.0.0.0", 8080)).sync();
        channelGroup.add(future.channel());
    }
}
