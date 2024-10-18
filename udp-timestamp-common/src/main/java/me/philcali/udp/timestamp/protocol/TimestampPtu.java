package me.philcali.udp.timestamp.protocol;

import java.util.Objects;

public class TimestampPtu implements Comparable<TimestampPtu> {
    private TimestampHeader header;
    private byte[] payload;

    public TimestampHeader getHeader() {
        return header;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setHeader(TimestampHeader header) {
        this.header = header;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    @Override
    public int compareTo(TimestampPtu timestampPtu) {
        return header.compareTo(timestampPtu.header);
    }

    @Override
    public int hashCode() {
        return header.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (Objects.isNull(obj)) {
            return false;
        }
        if (!(obj instanceof TimestampPtu)) {
            return false;
        }
        TimestampPtu ptu = (TimestampPtu) obj;
        return Objects.equals(header, ptu.header);
    }
}
