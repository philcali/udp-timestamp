package me.philcali.udp.timestamp.buffer;

import me.philcali.udp.timestamp.protocol.TimestampHeader;
import me.philcali.udp.timestamp.protocol.TimestampPtu;

import java.time.Instant;

import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;


public class TimestampPtuQueue {
    private final long bufferInMillis;
    private final PriorityBlockingQueue<TimestampPtu> internalQueue;
    private final AtomicReference<TimestampHeader> lastSeen;

    public TimestampPtuQueue(long bufferInMillis) {
        this.internalQueue = new PriorityBlockingQueue<>(1000, Comparator.reverseOrder());
        this.lastSeen = new AtomicReference<>();
        this.bufferInMillis = bufferInMillis;
    }

    public void push(TimestampPtu ptu) {
        this.internalQueue.add(ptu);
    }

    public TimestampPtu poll() {
        try {
            TimestampPtu ptu = this.internalQueue.take();
            Instant threshold = Instant.now().minusMillis(this.bufferInMillis);
            if (ptu.getHeader().toTimestamp().isAfter(threshold)) {
                TimestampHeader oldValue = lastSeen.getAndSet(ptu.getHeader());
                if (Objects.equals(oldValue, ptu.getHeader())) {
                    return null;
                }
                return ptu;
            }
            this.internalQueue.add(ptu);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public int unprocessedSize() {
        return this.internalQueue.size();
    }
}
