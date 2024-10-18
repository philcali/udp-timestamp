package me.philcali.udp.timestamp.protocol;

import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class TimestampPtuHeaderTest {

    @Test
    public void testHeaderEquals() {
        TimestampHeader headerA = new TimestampHeader();
        TimestampHeader headerB = new TimestampHeader();

        byte[] timestampA = new byte[36];
        Arrays.fill(timestampA, (byte) 1);
        headerA.setTimestamp(timestampA);
        headerA.setIndex((byte) 1);

        byte[] timestampB = new byte[36];
        headerB.setTimestamp(timestampB);
        headerB.setIndex((byte) 1);

        Assert.assertNotEquals(headerA, null);
        Assert.assertNotEquals(headerA, headerB);

        headerB.setTimestamp(timestampA);
        Assert.assertEquals(headerA, headerB);
    }

    @Test
    public void testHeaderCompareTo() {
        TimestampHeader headerA = new TimestampHeader();
        TimestampHeader headerB = new TimestampHeader();

        Assert.assertEquals(headerA.compareTo(headerB), 0);

        headerA.setIndex((byte) 0);
        headerB.setIndex((byte) 1);
        Assert.assertEquals(headerA.compareTo(headerB), -1);

        byte[] timestamp = new byte[36];
        Arrays.fill(timestamp, (byte) 1);
        headerA.setTimestamp(timestamp);

        Assert.assertEquals(headerA.compareTo(headerB), 1);
    }

    @Test
    public void testHeaderToTimestamp() {
        Instant instant = Instant.now();

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssa-DDD.SSSSSSSSS'Z'")
                .withZone(ZoneId.systemDefault());
        String timestampString = format.format(instant);

        TimestampHeader headerA = new TimestampHeader();
        headerA.setTimestamp(timestampString.getBytes());
        TimestampHeader headerB = new TimestampHeader();
        headerB.setTimestamp(timestampString.getBytes());

        Assert.assertEquals(headerA, headerB);
        Assert.assertEquals(headerA.toTimestamp(), instant);
    }
}
