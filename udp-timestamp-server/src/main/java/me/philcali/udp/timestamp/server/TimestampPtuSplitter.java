package me.philcali.udp.timestamp.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import me.philcali.udp.timestamp.protocol.SandboxPtu;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.IntStream;

public class TimestampPtuSplitter extends SimpleChannelInboundHandler<SandboxPtu> {
    private final int shards;
    private final InetSocketAddress combiner;
    private final DateTimeFormatter formatter;

    TimestampPtuSplitter(final InetSocketAddress combiner, final int shards) {
        this.combiner = combiner;
        this.shards = shards;
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssa-DDD.SSSSSSSSS'Z'")
                .withZone(ZoneId.systemDefault());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SandboxPtu timestampPtu) {
        System.out.println(timestampPtu);

        String timestampString = formatter.format(Instant.now());
        IntStream.range(0, this.shards).forEach(shard -> {
            ByteBuf buffer = Unpooled.buffer();
            buffer.writeBytes(timestampString.getBytes());
            buffer.writeByte(0);
            buffer.writeByte(shard);
            buffer.writeBytes(new byte[]{0, 0});
            buffer.writeBytes(timestampPtu.getHeader());
            buffer.writeBytes(timestampPtu.getPayload());
            ctx.channel().writeAndFlush(new DatagramPacket(buffer, combiner));
        });
    }
}
