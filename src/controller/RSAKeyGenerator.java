package controller;

import java.math.BigInteger;
import java.util.Random;

import model.PrivateKey;
import model.PublicKey;
import model.RSAKeyPair;

public abstract class RSAKeyGenerator {
    public static RSAKeyPair generateKeyPair(int numBitsPrime) {
        BigInteger p;
        BigInteger q;
        do {
            p = generateRandomPrimeNumber(numBitsPrime);
            q = generateRandomPrimeNumber(numBitsPrime);
        } while (p.equals(q));

        // System.out.println("P = " + p);
        // System.out.println("Q = " + q);

        BigInteger n = calcN(p, q);

        // System.out.println("N = " + n);

        BigInteger phi = calcPhi(p, q);

        // System.out.println("(P-1)*(Q-1) = " + a);

        BigInteger e;

        Random r = new Random();

        while (true) {
            e = new BigInteger(128, r).setBit(0);
            if (isValidE(e, phi))
                break;
        }

        BigInteger d = calcD(e, phi);

        return new RSAKeyPair(
                new PrivateKey(d, n),
                new PublicKey(e, n));
    }

    public static BigInteger[] encryptString(String input, PublicKey key) {
        char[] charArray = input.toCharArray();
        BigInteger[] encryptedString = new BigInteger[input.length()];
        for (int i = 0; i < input.length(); i++) {
            BigInteger charNum = new BigInteger(String.valueOf((int) charArray[i]));
            BigInteger encryptedChar = charNum.modPow(key.getE(), key.getN());
            encryptedString[i] = encryptedChar;
        }
        return encryptedString;
    }

    public static String decryptString(BigInteger[] input, PrivateKey key) {
        char[] newString = new char[input.length];
        for (int i = 0; i < input.length; i++) {
            BigInteger decryptedChar = input[i].modPow(key.getD(), key.getN());
            int val = decryptedChar.intValue();
            char n = (char) val;
            newString[i] = n;
        }
        return String.valueOf(newString);
    }

    // Algoritmos. Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, Cliford
    // Stein. p. 976
    public static PrivateKey breakPublicKey(PublicKey publicKey) {
        if (millerRabins(publicKey.getN(), 2))
            return null;

        Random rand = new Random();
        BigInteger i = BigInteger.ONE;
        BigInteger x1;
        do {
            x1 = new BigInteger(publicKey.getN().bitCount(), rand);
        } while (x1.compareTo(publicKey.getN()) >= 0);
        BigInteger y = x1;
        BigInteger k = BigInteger.TWO;
        BigInteger xi = x1;
        BigInteger d;
        BigInteger p = null;

        while (true) {
            i = i.add(BigInteger.ONE);
            xi = xi.pow(2).subtract(BigInteger.ONE).mod(publicKey.getN());
            d = extendedEuclidianAlgorithm(y.subtract(xi), publicKey.getN())[0];
            if (!d.equals(BigInteger.ONE) && !d.equals(publicKey.getN())) {
                if (millerRabins(d, 2)) {
                    if (p == null)
                        p = d;
                    else if (!p.equals(d)) {
                        BigInteger phi = calcPhi(p, d);
                        BigInteger n = calcN(p, d);
                        if (n.equals(publicKey.getN()) && isValidE(publicKey.getE(), phi))
                            return new PrivateKey(calcD(publicKey.getE(), phi), n);
                    }
                }
            }
            if (i.equals(k)) {
                y = xi;
                k = k.multiply(BigInteger.TWO);
            }
        }
    }

    private static BigInteger generateRandomPrimeNumber(int numBitsPrime) {
        BigInteger prime;
        Random random = new Random();
        prime = new BigInteger(numBitsPrime, random).setBit(0);
        while (!millerRabins(prime, 2))
            prime = prime.add(BigInteger.TWO);

        return prime;
    }

    private static BigInteger calcPhi(BigInteger p, BigInteger q) {
        return p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
    }

    private static BigInteger calcN(BigInteger p, BigInteger q) {
        return p.multiply(q);
    }

    private static BigInteger calcD(BigInteger e, BigInteger phi) {
        BigInteger[] res = extendedEuclidianAlgorithm(e, phi);
        return res[1].mod(phi);
    }

    private static boolean isValidE(BigInteger e, BigInteger phi) {
        return extendedEuclidianAlgorithm(e, phi)[0].equals(BigInteger.ONE);
    }

    // Algoritmos. Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, Cliford
    // Stein.
    private static BigInteger[] extendedEuclidianAlgorithm(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) {
            return new BigInteger[] { a, BigInteger.ONE, BigInteger.TWO };
        }

        BigInteger r[] = extendedEuclidianAlgorithm(b, a.mod(b));
        return new BigInteger[] { r[0], r[2], r[1].subtract(a.divide(b).multiply(r[2])) };
    }

    // Algoritmos. Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, Cliford
    // Stein.
    // private static boolean isPseudoprime(BigInteger number) {
    // return number.equals(BigInteger.TWO)
    // || BigInteger.TWO.modPow(number.subtract(BigInteger.ONE),
    // number).equals(BigInteger.ONE);
    // }

    // Algoritmos. Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, Cliford
    // Stein.
    private static boolean witness(BigInteger a, BigInteger n) {
        BigInteger nMinusOne = n.subtract(BigInteger.ONE);
        // n-1 = 2^t * u
        int t = nMinusOne.getLowestSetBit();
        BigInteger u = nMinusOne.shiftRight(t);

        BigInteger x0 = a.modPow(u, n);

        for (int i = 1; i <= t; i++) {
            BigInteger xi = x0.modPow(BigInteger.TWO, n);
            if (xi.equals(BigInteger.ONE) && !x0.equals(BigInteger.ONE) && !x0.equals(nMinusOne))
                return true;
            x0 = xi;
        }
        return !x0.equals(BigInteger.ONE);
    }

    // Algoritmos. Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, Cliford
    // Stein.
    private static boolean millerRabins(BigInteger n, int s) {
        Random random = new Random();
        BigInteger a;
        int nLesserOrEqualToTwo = n.compareTo(BigInteger.TWO);

        if (nLesserOrEqualToTwo < 0) // Test if its lesser than two
            return false;
        if (nLesserOrEqualToTwo == 0) // test if its equal to two
            return true;
        if (!n.testBit(0)) // test if its even
            return false;

        while (s-- > 0) {
            do {
                a = new BigInteger(n.bitCount(), random);
            } while (a.compareTo(n) >= 0 || a.equals(BigInteger.ZERO));
            if (witness(a, n))
                return false;
        }
        return true;
    }
}
