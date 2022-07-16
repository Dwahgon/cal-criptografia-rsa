package criptografiarsa.controller;

import criptografiarsa.model.RSAKeyPair;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class RSAKeyGeneratorTest {

    private static final int FILE_CONTENT_SIZE = 1024;
    private static final int BLOCK_SIZE = 4096;
    private static final int KEY_SIZE = 2046;
    private static final String TEMP_SOURCE_FILE_NAME = "tempTestFile";

    public RSAKeyGeneratorTest() {
    }

    @org.junit.jupiter.api.Test
    public void testDecryptFile() {
        try {
            File sourceFile = createSourceTestFile();
            File encryptedFile = File.createTempFile(TEMP_SOURCE_FILE_NAME + ".enc", ".tmp");
            File decryptedFile = File.createTempFile(TEMP_SOURCE_FILE_NAME + ".dec", ".tmp");

            RSAKeyPair rsaKeyPair = RSAKeyGenerator.generateKeyPair(2046);
            RSAKeyGenerator.encryptFile(sourceFile, encryptedFile, rsaKeyPair.getPublicKey());

            PrintStream originalOut = System.out;
            try ( FileOutputStream fos = new FileOutputStream(decryptedFile)) {
                System.setOut(new PrintStream(fos));
                RSAKeyGenerator.decryptFile(encryptedFile, rsaKeyPair.getPrivateKey());
            }
            System.setOut(originalOut);
            assertFalse(compareFiles(sourceFile, encryptedFile, false), "O arquivo fonte e o arquivo criptografado são iguais");
            assertFalse(compareFiles(encryptedFile, decryptedFile, false), "O arquivo criptografado e o arquivo descriptografado são iguais");
            assertTrue(compareFiles(sourceFile, decryptedFile), "O arquivo fonte e o arquivo descriptografado são diferentes");

            sourceFile.delete();
            encryptedFile.delete();
            decryptedFile.delete();
        } catch (IOException ex) {
            fail("Could not create temp file");
        }
    }

    private File createSourceTestFile() throws IOException {
        File sourceFile = File.createTempFile(TEMP_SOURCE_FILE_NAME, ".tmp");
        try ( FileOutputStream fos = new FileOutputStream(sourceFile)) {
//            fos.write("abcdefghijklmnopqrstuvwxyz123456789".getBytes());
            int qntBytesToWrite = FILE_CONTENT_SIZE;
            String buffer = "";

            while (qntBytesToWrite > 0) {
                if (qntBytesToWrite / BLOCK_SIZE > 0) {
                    qntBytesToWrite -= BLOCK_SIZE;
                    buffer = randomString(BLOCK_SIZE);
                } else if (qntBytesToWrite % BLOCK_SIZE > 0) {
                    buffer = randomString(qntBytesToWrite % BLOCK_SIZE);
                    qntBytesToWrite = 0;
                }
                fos.write(buffer.getBytes());
            }
        }

        return sourceFile;
    }

    private String randomString(int stringSize) {
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String upper = lower.toUpperCase();
        String nums = "123456789";
        String symbols = "!@#$%*()[]{}/;.,<>:?|";
        char[] symbs = (lower + upper + nums + symbols).toCharArray();
        char buffer[] = new char[stringSize];
        Random random = new Random();
        for (int i = 0; i < stringSize; i++) {
            buffer[i] = symbs[random.nextInt(symbs.length)];
        }
        return new String(buffer);
    }

    private boolean compareFiles(File file1, File file2, boolean printIfFalse) throws FileNotFoundException, IOException {
        try ( FileInputStream fis1 = new FileInputStream(file1)) {
            try ( FileInputStream fis2 = new FileInputStream(file2)) {
                byte buffer1[] = new byte[BLOCK_SIZE];
                byte buffer2[] = new byte[BLOCK_SIZE];
                int readBytes1;
                int readBytes2;
                do {
                    readBytes1 = fis1.read(buffer1);
                    readBytes2 = fis2.read(buffer2);
                    if (!Arrays.equals(buffer1, buffer2)) {
                        if (printIfFalse) {
                            System.out.println("Buffers are different");
                            System.out.println("Buffer 1: " + Arrays.toString(buffer1));
                            System.out.println("Buffer 2: " + Arrays.toString(buffer2));
                        }
                        return false;
                    }
                } while (readBytes1 > 0 || readBytes2 > 0);
            }
        }

        return true;
    }

    private boolean compareFiles(File file1, File file2) throws IOException {
        return compareFiles(file1, file2, true);
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
