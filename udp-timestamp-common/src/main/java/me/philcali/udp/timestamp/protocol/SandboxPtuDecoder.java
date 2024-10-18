package me.philcali.udp.timestamp.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class SandboxPtuDecoder extends MessageToMessageDecoder<DatagramPacket> {
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket in, List<Object> list) {
        ByteBuf content = in.content();
        int readableBytes = content.readableBytes();
        if (readableBytes <= 16) {
            return;
        }

        SandboxPtu ptu = new SandboxPtu();
        byte[] header = new byte[16];
        content.readBytes(header);
        ptu.setHeader(header);
        byte[] payload = new byte[readableBytes - 16];
        content.readBytes(payload);
        ptu.setPayload(payload);

        list.add(ptu);
    }
}
