package criptografiarsa.model;

import java.math.BigInteger;

public class PublicKey {

    private final BigInteger e, n;

    public PublicKey(BigInteger e, BigInteger n) {
        this.e = e;
        this.n = n;
    }

    public PublicKey(BigInteger[] bigIntegers) {
        this.e = bigIntegers[0];
        this.n = bigIntegers[1];
    }

    public BigInteger getE() {
        return e;
    }

    public BigInteger getN() {
        return n;
    }

    @Override
    public String toString() {
        return "PublicKey [e=" + e + ", n=" + n + "]";
    }

    public BigInteger[] toBigIntegers() {
        BigInteger[] bigIntegers = new BigInteger[2];
        bigIntegers[0] = this.e;
        bigIntegers[1] = this.n;
        return bigIntegers;
    }
}
