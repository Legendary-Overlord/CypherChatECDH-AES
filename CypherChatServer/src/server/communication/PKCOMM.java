package server.communication;

public class PKCOMM {
    private String uuid;
    private byte[] publicKey;

    public PKCOMM(String uuid, byte[] publicKey) {
        this.uuid = uuid;
        this.publicKey = publicKey;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }
}
