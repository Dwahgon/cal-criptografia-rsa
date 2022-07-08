package criptografiarsa.controller;

public class RSAKeyGeneratorTest {

    public RSAKeyGeneratorTest() {
    }

//    @org.junit.jupiter.api.Test
//    public void benchmarkGenerateKeyPair() {
//        int bits = 64;
//        System.out.println("TAMANHO CHAVE (bits)\tTEMPO (ms)");
//        while (bits <= 1024) {
//            long startTime = System.nanoTime();
//            RSAKeyGenerator.generateKeyPair(bits / 2);
//            long endTime = System.nanoTime() - startTime;
//            System.out.println(bits + "\t" + endTime / 1000000);
//            bits += 64;
//        }
//    }
//
//    @org.junit.jupiter.api.Test
//    public void benchmarkBreakKey() {
//        int bits = 8;
//        System.out.println("TAMANHO CHAVE (bits)\tTEMPO (ms)");
//        while (bits <= 56) {
//            RSAKeyPair rsaKeyPair = RSAKeyGenerator.generateKeyPair(bits / 2);
//            long startTime = System.nanoTime();
//            RSAKeyGenerator.breakPublicKey(rsaKeyPair.getPublicKey());
//            long endTime = System.nanoTime() - startTime;
//            System.out.println(bits + "\t" + endTime / 1000000);
//            bits += 2;
//        }
//    }
//
//    @org.junit.jupiter.api.Test
//    public void benchmarkFileEncrypt() throws IOException {
//        int bits = 64;
//        System.out.println("TAMANHO CHAVE (bits)\tTEMPO (ms)");
//        File file = new File("/media/data/Downloads/test.txt");
//        File destFile = new File("/media/data/Downloads/test.enc");
//        while (bits <= 1024) {
//            RSAKeyPair rsaKeyPair = RSAKeyGenerator.generateKeyPair(bits / 2);
//            long startTime = System.nanoTime();
//            RSAKeyGenerator.encryptFile(file, destFile, rsaKeyPair.getPublicKey());
//            long endTime = System.nanoTime() - startTime;
//            System.out.println(bits + "\t" + endTime / 1000000);
//            bits += 64;
//        }
//    }
//
//    @org.junit.jupiter.api.Test
//    public void benchmarkFileDecrypt() throws IOException {
//        int bits = 64;
//        System.out.println("TAMANHO CHAVE (bits)\tTEMPO (ms)");
//        File file = new File("/media/data/Downloads/test.txt");
//        File destFile = new File("/media/data/Downloads/test.enc");
//        while (bits <= 1024) {
//            RSAKeyPair rsaKeyPair = RSAKeyGenerator.generateKeyPair(bits / 2);
//            RSAKeyGenerator.encryptFile(file, destFile, rsaKeyPair.getPublicKey());
//            long startTime = System.nanoTime();
//            RSAKeyGenerator.decryptFile(destFile, rsaKeyPair.getPrivateKey());
//            long endTime = System.nanoTime() - startTime;
//            System.out.println(bits + "\t" + endTime / 1000000);
//            bits += 64;
//        }
//    }
}
