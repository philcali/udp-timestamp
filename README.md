# Inefficient Timestamp Forward Error Correction (FEC)

This repo is an example demonstrating how to properly correct
order and errors in a bit stream utilizing a timestamp header
for a strict data protocol. This is largely an inefficient
stream processor, but it works fine enough for low data rates.

## Why?

The User Datagram Protocol (UDP) is inherrently unreliable due
to its connectionless nature, but it allows amazingly high data
rates. Some problems of UDP:

- Packet drops: packets will drop for a variety of reasons. DISCARD in routers, buffer overflows in compute, and many more.
- Out of order delivery: packets routing over long distance and multiple hops will allow the packets to receive in different places.

A more efficient way to handle these problems is to wrap an underlying protocol with erasure bits using something
like a Reed-Solomon code implementation.

## How?

Unfortunately, some servers will reject any protocol that alters an expected format.
This is why this repo exists, to simply demonstrate the "art of the possible" in such cases. If a
protocol allows *some* form of timestamp wtermarking, then it is possible to shard
the stream, buffer, and combine as efficiently as possible. Notable problems arise
when determining the buffer size (can it process faster than input?) and network
saturation (desired data rate is multiplicative by number of sharding).

At any rate, this naive approach simply states to run a splitter server a
long line before the server and a combiner server on the other side of
a long line. The UDP receiver and sender would be run close the stream
processor.
