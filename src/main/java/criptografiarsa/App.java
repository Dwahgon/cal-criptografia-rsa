package criptografiarsa;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import criptografiarsa.controller.RSAKeyGenerator;
import criptografiarsa.model.PrivateKey;
import criptografiarsa.model.RSAKeyPair;
import criptografiarsa.view.Prompts;

public class App {

    private static final int OPTION_GENERATE_KEYS = 1;
    private static final int OPTION_ENCRYPT_FILE = 2;
    private static final int OPTION_DECRYPT_FILE = 3;
    private static final int OPTION_BREAK_KEYS = 4;
    private static final int OPTION_EXIT = 5;
    public static Scanner scanner;

    public static void main(String[] args) throws Exception {
        scanner = new Scanner(System.in);

        loop:
        do {
            System.out.println(OPTION_GENERATE_KEYS + ") Gerar chaves");
            System.out.println(OPTION_ENCRYPT_FILE + ") Criptografar arquivo");
            System.out.println(OPTION_DECRYPT_FILE + ") Descriptografar arquivo");
            System.out.println(OPTION_BREAK_KEYS + ") Quebrar chave (força bruta)");
            System.out.println(OPTION_EXIT + ") Sair");
            try {
                switch (Integer.valueOf(scanner.nextLine())) {
                    case OPTION_GENERATE_KEYS:
                    try {
                        generateKeys();
                    } catch (NumberFormatException e) {
                        System.out.println("Entrada inválida");
                    }
                    break;
                    case OPTION_ENCRYPT_FILE:
                        encryptFile();
                        break;
                    case OPTION_DECRYPT_FILE:
                        decryptFile();
                        break;
                    case OPTION_BREAK_KEYS:
                        breakKeys();
                        break;
                    case OPTION_EXIT:
                        break loop;
                    default:
                        System.out.println("Opção inválida");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida");
            }
        } while (true);
        scanner.close();
    }

    private static RSAKeyPair generateKeys() {
        System.out.println("Digite o número de bits das chaves que serão geradas");
        int bits = Integer.valueOf(scanner.nextLine());
        System.out.println("Gerando chaves...");
        RSAKeyPair rsaKeyPair = RSAKeyGenerator.generateKeyPair(bits / 2);
        System.out.println(rsaKeyPair.getPublicKey() + "\n");
        System.out.println(rsaKeyPair.getPrivateKey());
        return rsaKeyPair;
    }

    private static void encryptFile() {
        File sourceFile = Prompts.promptSelectFile();
        if (sourceFile == null) {
            return;
        }
        File destinationFile = new File(sourceFile.getAbsolutePath() + ".enc");

        RSAKeyPair rsaKeyPair;
        rsaKeyPair = generateKeys();
        System.out.println("Entrada inválida");

        System.out.println("Criptografando...");
        try {
            RSAKeyGenerator.encryptFile(sourceFile, destinationFile, rsaKeyPair.getPublicKey());
        } catch (IOException e) {
            System.out.println("Erro de entrada e saida ao tentar salvar arquivo criptografado");
        }
    }

    private static void decryptFile() {
        File encryptedFile = Prompts.promptSelectEncryptedFile();
        if (encryptedFile == null) {
            return;
        }

        PrivateKey privateKey;
        privateKey = Prompts.promptPrivateKey(scanner);

        try {
            RSAKeyGenerator.decryptFile(encryptedFile, privateKey);
        } catch (IOException e) {
            System.out.println("Erro de entrada e saida ao tentar salvar arquivo criptografado");
        }
    }

    private static void breakKeys() {
        RSAKeyPair rsaKeyPair;
        rsaKeyPair = generateKeys();
        System.out.println("Quebrando chave pública...");
        System.out.println("Chave privada (encontrada)\n" + RSAKeyGenerator.breakPublicKey(rsaKeyPair.getPublicKey()));

    }

}
