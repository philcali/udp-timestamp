package me.philcali.udp.timestamp.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class TimestampPtuDecoder extends MessageToMessageDecoder<DatagramPacket> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket, List<Object> list) throws Exception {
        ByteBuf in = datagramPacket.content();
        int readableBytes = in.readableBytes();
        if (readableBytes <= 40) {
            return;
        }
        byte[] timestamp = new byte[36];
        in.readBytes(timestamp);
        byte index = in.readByte();
        byte shard = in.readByte();
        byte[] disambiguator = new byte[2];
        in.readBytes(disambiguator);

        TimestampHeader header = new TimestampHeader();
        header.setTimestamp(timestamp);
        header.setIndex(index);
        header.setShard(shard);
        header.setDisambiguator(disambiguator);

        TimestampPtu ptu = new TimestampPtu();
        ptu.setHeader(header);
        byte[] payload = new byte[readableBytes - 40];
        in.readBytes(payload);
        ptu.setPayload(payload);

        list.add(ptu);
    }
}
