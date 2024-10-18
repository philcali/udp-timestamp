package me.philcali.udp.timestamp.protocol;

public class SandboxPtu {
    private byte[] header;
    private byte[] payload;

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public void setHeader(byte[] header) {
        this.header = header;
    }

    public byte[] getPayload() {
        return payload;
    }

    public byte[] getHeader() {
        return header;
    }
}
