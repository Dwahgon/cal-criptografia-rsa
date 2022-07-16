package criptografiarsa.controller;

import criptografiarsa.model.PrivateKey;
import criptografiarsa.model.PublicKey;
import criptografiarsa.model.RSAKeyPair;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Random;

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
            if (isValidE(e, phi)) {
                break;
            }
        }

        BigInteger d = calcD(e, phi);

        return new RSAKeyPair(
                new PrivateKey(d, n),
                new PublicKey(e, n));
    }

// C = M ^ e mod n
    public static BigInteger calculateC(byte[] M, PublicKey publicKey) {
        BigInteger charNum = new BigInteger(M);
        return charNum.modPow(publicKey.getE(), publicKey.getN());
    }

// M = C ^ d mod n
    public static byte calculateM(BigInteger M, PrivateKey privateKey) {
        return M.modPow(privateKey.getD(), privateKey.getN()).byteValue();
    }

    public static void encryptFile(File sourceFile, File destinationFile, PublicKey publicKey) throws IOException {
        final int numBytesN = publicKey.getN().toByteArray().length;
        int numBytesC;
        BigInteger c;
        byte[] buff = new byte[1], cByteArray;

        try ( InputStream inputStream = new FileInputStream(sourceFile)) {
            try ( OutputStream outputStream = new FileOutputStream(destinationFile)) {
                while ((inputStream.read(buff)) != -1) {
                    c = calculateC(buff, publicKey);
                    cByteArray = c.toByteArray();
                    numBytesC = cByteArray.length;
                    for (int i = 0; i < numBytesN - numBytesC; i++) {
                        outputStream.write(0);
                    }
                    outputStream.write(cByteArray);
                }
            }
        }
    }

    public static void decryptFile(File sourceFile, PrivateKey privateKey) throws IOException {
        final int numBytesN = privateKey.getN().toByteArray().length;
        BigInteger bigInt;
        byte m;
        byte[] buff = new byte[numBytesN];

        try ( InputStream inputStream = new FileInputStream(sourceFile)) {
            while ((inputStream.read(buff)) != -1) {
                bigInt = new BigInteger(buff);
                m = calculateM(bigInt, privateKey);
                System.out.print((char) m);
            }
        }
//        System.out.println();
    }

// Algoritmos. Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, Cliford
// Stein.
    private static BigInteger[] extendedEuclidianAlgorithm(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) {
            return new BigInteger[]{a, BigInteger.ONE, BigInteger.TWO};
        }

        BigInteger r[] = extendedEuclidianAlgorithm(b, a.mod(b));
        return new BigInteger[]{r[0], r[2], r[1].subtract(a.divide(b).multiply(r[2]))};
    }

// phi(p,q) = (p-1)*(q-1)
    private static BigInteger calcPhi(BigInteger p, BigInteger q) {
        return p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
    }

// n = p*q
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

    private static BigInteger nextPrime(BigInteger n) {
        n = n.add(BigInteger.ONE).setBit(0);
        while (!millerRabins(n, 2)) {
            n = n.add(BigInteger.TWO);
        }
        return n;
    }

    public static PrivateKey breakPublicKey(PublicKey publicKey) {
        if (millerRabins(publicKey.getN(), 2)) {
            return null;
        }

        BigInteger p = BigInteger.TWO;
        BigInteger q;
        do {
            if (!publicKey.getN().remainder(p).equals(BigInteger.ZERO)) {
                p = nextPrime(p);
                continue;
            }
            q = publicKey.getN().divide(p);
            if (calcN(p, q).equals(publicKey.getN()) && isValidE(publicKey.getE(), calcPhi(p, q))) {
                return new PrivateKey(calcD(publicKey.getE(), calcPhi(p, q)), calcN(p, q));
            }
            p = nextPrime(p);
        } while (true);
    }

    private static BigInteger generateRandomPrimeNumber(int numBitsPrime) {
        BigInteger prime;
        Random random = new Random();
        prime = new BigInteger(numBitsPrime, random).setBit(0);
        while (!millerRabins(prime, 2)) {
            prime = prime.add(BigInteger.TWO);
        }

        return prime;
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
    private static boolean millerRabins(BigInteger n, int s) {
        Random random = new Random();
        BigInteger a;
        int nLesserOrEqualToTwo = n.compareTo(BigInteger.TWO);

        if (nLesserOrEqualToTwo < 0) // Test if its lesser than two
        {
            return false;
        }
        if (nLesserOrEqualToTwo == 0) // test if its equal to two
        {
            return true;
        }
        if (!n.testBit(0)) // test if its even
        {
            return false;
        }

        while (s-- > 0) {
            do {
                a = new BigInteger(n.bitCount(), random);
            } while (a.compareTo(n) >= 0 || a.equals(BigInteger.ZERO));
            if (witness(a, n)) {
                return false;
            }
        }
        return true;
    }

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
            if (xi.equals(BigInteger.ONE) && !x0.equals(BigInteger.ONE) && !x0.equals(nMinusOne)) {
                return true;
            }
            x0 = xi;
        }
        return !x0.equals(BigInteger.ONE);
    }
}
