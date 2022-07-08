package model;

import java.math.BigInteger;

public class PrivateKey {
    private final BigInteger d, n;

    public PrivateKey(BigInteger d, BigInteger n) {
        this.d = d;
        this.n = n;
    }

    public PrivateKey(BigInteger[] bigIntegers) {
        this.d = bigIntegers[0];
        this.n = bigIntegers[1];
    }

    public BigInteger getD() {
        return d;
    }

    public BigInteger getN() {
        return n;
    }

    @Override
    public String toString() {
        return "PrivateKey [d=" + d + ", n=" + n + "]";
    }

    public BigInteger[] toBigIntegers() {
        BigInteger[] bigIntegers = new BigInteger[2];
        bigIntegers[0] = this.d;
        bigIntegers[1] = this.n;
        return bigIntegers;
    }
}
