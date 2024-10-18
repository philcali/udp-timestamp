package me.philcali.udp.timestamp.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.philcali.udp.timestamp.buffer.TimestampPtuQueue;
import me.philcali.udp.timestamp.protocol.TimestampPtu;

public class TimestampPtuCombiner extends SimpleChannelInboundHandler<TimestampPtu> {
    private final TimestampPtuQueue queue;

    TimestampPtuCombiner(final TimestampPtuQueue queue) {
        this.queue = queue;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TimestampPtu timestampPtu) throws Exception {
        System.out.println(timestampPtu);
        queue.push(timestampPtu);
    }
}
