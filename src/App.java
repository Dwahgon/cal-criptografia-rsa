import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;

import controller.RSAKeyGenerator;
import model.PrivateKey;
import model.RSAKeyPair;
import persistance.BigIntegersDAO;
import persistance.PlainTextDAO;
import view.Prompts;

public class App {
    private static final int OPTION_GENERATE_KEYS = 1;
    private static final int OPTION_ENCRYPT_FILE = 2;
    private static final int OPTION_DECRYPT_FILE = 3;
    private static final int OPTION_BREAK_KEYS = 4;
    private static final int OPTION_EXIT = 5;
    public static Scanner scanner;

    public static void main(String[] args) throws Exception {
        scanner = new Scanner(System.in);

        loop: do {
            System.out.println(OPTION_GENERATE_KEYS + ") Gerar chaves");
            System.out.println(OPTION_ENCRYPT_FILE + ") Criptografar arquivo");
            System.out.println(OPTION_DECRYPT_FILE + ") Descriptografar arquivo");
            System.out.println(OPTION_BREAK_KEYS + ") Quebrar chave (força bruta)");
            System.out.println(OPTION_EXIT + ") Sair");
            switch (scanner.nextInt()) {
                case OPTION_GENERATE_KEYS:
                    generateKeys();
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
        } while (true);
        scanner.close();
    }

    private static RSAKeyPair generateKeys() {
        System.out.println("Digite o número de bits das chaves que serão geradas");
        int bits = scanner.nextInt();
        System.out.println("Gerando chaves...");
        RSAKeyPair rsaKeyPair = RSAKeyGenerator.generateKeyPair(bits / 2);
        System.out.println(rsaKeyPair.getPublicKey() + "\n");
        System.out.println(rsaKeyPair.getPrivateKey());
        return rsaKeyPair;
    }

    private static void encryptFile() {
        File file = Prompts.promptSelectFile();
        String fileContents;
        try {
            fileContents = PlainTextDAO.getInstance().readFileAsString(file);
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado");
            return;
        } catch (IOException e) {
            System.out.println("Erro de entrada e saída ao tentar ler arquivo");
            return;
        }

        RSAKeyPair rsaKeyPair = generateKeys();
        // try {
        // saveBigIntegers(file.getAbsolutePath() + ".pubkey",
        // rsaKeyPair.getPublicKey().toBigIntegers());
        // saveBigIntegers(file.getAbsolutePath() + ".privkey",
        // rsaKeyPair.getPrivateKey().toBigIntegers());
        // } catch (IOException e) {
        // System.out.println("Erro de entrada e saida ao tentar salvar chaves");
        // return;
        // }

        System.out.println("Criptografando...");
        BigInteger[] fileContentsEncryptedBigIntegers = RSAKeyGenerator.encryptString(fileContents,
                rsaKeyPair.getPublicKey());
        try {
            BigIntegersDAO.getInstance().saveBigIntegers(file.getAbsolutePath() + ".enc",
                    fileContentsEncryptedBigIntegers);
        } catch (IOException e) {
            System.out.println("Erro de entrada e saida ao tentar salvar arquivo criptografado");
            return;
        }
    }

    private static void decryptFile() {

        File encryptedFile = Prompts.promptSelectEncryptedFile();
        if (encryptedFile == null)
            return;

        BigInteger[] encryptedFileContents;
        try {
            encryptedFileContents = BigIntegersDAO.getInstance().loadBigIntegers(encryptedFile);
        } catch (IOException e1) {
            System.out.println("Erro de entrada e saída ao tentar ler arquivo criptografada");
            return;
        }

        // System.out.println("Selecione o arquivo da chave privada");
        // jFileChooser.setFileFilter(new FileNameExtensionFilter(
        // "Private key file", "privkey"));
        // if (jFileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
        // System.out.println("Cancelando operação...");
        // return;
        // }
        // File privateKeyFile = jFileChooser.getSelectedFile();

        PrivateKey privateKey = Prompts.promptPrivateKey(scanner);

        String decryptedFileContents = RSAKeyGenerator.decryptString(encryptedFileContents, privateKey);
        System.out.println("Arquivo descriptografado:");
        System.out.println(decryptedFileContents);
    }

    private static void breakKeys() {
        RSAKeyPair rsaKeyPair = generateKeys();
        System.out.println("Quebrando chave pública...");
        System.out.println("Chave privada (encontrada)\n" + RSAKeyGenerator.breakPublicKey(rsaKeyPair.getPublicKey()));

    }

}
