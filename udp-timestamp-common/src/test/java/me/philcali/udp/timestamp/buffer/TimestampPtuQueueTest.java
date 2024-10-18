package me.philcali.udp.timestamp.buffer;

import me.philcali.udp.timestamp.protocol.TimestampHeader;
import me.philcali.udp.timestamp.protocol.TimestampPtu;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class TimestampPtuQueueTest {

    @Test
    public void testQueue() {
        TimestampPtuQueue queue = new TimestampPtuQueue(0);

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssa-DDD.SSSSSSSSS'Z'")
                        .withZone(ZoneId.systemDefault());

        Set<TimestampPtu> expected = new HashSet<>();
        IntStream.range(0, 5).forEach(index -> {
            Instant now = Instant.now();
            String timestampString = format.format(now);
            IntStream.range(0, 3).forEach(shard -> {
                TimestampHeader header = new TimestampHeader();
                header.setTimestamp(timestampString.getBytes());
                header.setIndex((byte) index);
                header.setShard((byte) shard);

                TimestampPtu ptu = new TimestampPtu();
                ptu.setHeader(header);
                ptu.setPayload(new byte[1000]);
                queue.push(ptu);
                if (shard == 0) {
                    expected.add(ptu);
                }
            });
        });

        Assert.assertEquals(15, queue.unprocessedSize());
        IntStream.range(0, 15).forEach(index -> {
            TimestampPtu ptu = queue.poll();
            if (ptu != null) {
                Assert.assertTrue(expected.contains(ptu));
            }
        });
        Assert.assertNull(queue.poll());
    }
}
