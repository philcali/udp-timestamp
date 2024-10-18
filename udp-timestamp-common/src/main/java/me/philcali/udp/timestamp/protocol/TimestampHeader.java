package me.philcali.udp.timestamp.protocol;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Objects;

public class TimestampHeader implements Comparable<TimestampHeader> {
    private byte[] timestamp;
    private byte index;
    private byte shard;
    private byte[] disambiguator;

    public byte[] getTimestamp() {
        return timestamp;
    }

    public byte[] getDisambiguator() {
        return disambiguator;
    }

    public byte getIndex() {
        return index;
    }

    public byte getShard() {
        return shard;
    }

    public void setTimestamp(byte[] timestamp) {
        this.timestamp = timestamp;
    }

    public void setDisambiguator(byte[] disambiguator) {
        this.disambiguator = disambiguator;
    }

    public void setIndex(byte index) {
        this.index = index;
    }

    public void setShard(byte shard) {
        this.shard = shard;
    }

    public Instant toTimestamp() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssa-DDD.SSSSSSSSS'Z'")
                .withZone(ZoneId.systemDefault());
        TemporalAccessor accessor = format.parse(new String(timestamp, StandardCharsets.UTF_8));
        return Instant.from(accessor);
    }

    @Override
    public int compareTo(TimestampHeader timestampHeader) {
        int tsCompare = Arrays.compare(timestamp, timestampHeader.getTimestamp());
        if (tsCompare != 0) {
            return tsCompare;
        }
        return Integer.compare(index, timestampHeader.index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(timestamp), index);
    }

    @Override
    public boolean equals(Object obj) {
        if (Objects.isNull(obj)) {
            return false;
        }
        if (!(obj instanceof TimestampHeader)) {
            return false;
        }
        TimestampHeader header = (TimestampHeader) obj;
        return Arrays.equals(timestamp, header.getTimestamp()) && Objects.equals(index, header.getIndex());
    }
}
