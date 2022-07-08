package criptografiarsa.view;

import criptografiarsa.model.PrivateKey;
import criptografiarsa.model.PublicKey;
import java.io.File;
import java.math.BigInteger;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Prompts {

    private static PublicKey promptPublicKey(Scanner scanner) {
        System.out.println("Digite o valor e da chave publica");
        BigInteger e = new BigInteger(scanner.nextLine());
        System.out.println("Digite o valor n da chave publica");
        BigInteger n = new BigInteger(scanner.nextLine());
        PublicKey publicKey = new PublicKey(e, n);
        return publicKey;
    }

    public static PrivateKey promptPrivateKey(Scanner scanner) {
        System.out.println("Digite o valor d da chave privada");
        BigInteger d = new BigInteger(scanner.nextLine());
        System.out.println("Digite o valor n da chave privada");
        BigInteger n = new BigInteger(scanner.nextLine());
        PrivateKey privateKey = new PrivateKey(d, n);
        return privateKey;
    }

    public static File promptSelectEncryptedFile() {
        JFileChooser jFileChooser = new JFileChooser();
        System.out.println("Selecione o arquivo que deseja descriptografar");
        jFileChooser.setFileFilter(new FileNameExtensionFilter(
                "Encrypted file", "enc"));
        if (jFileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            System.out.println("Cancelando operação...");
            return null;
        }
        return jFileChooser.getSelectedFile();
    }

    public static File promptSelectFile() {
        System.out.println("Selecione o arquivo que deseja criptografar");
        JFileChooser jFileChooser = new JFileChooser();
        if (jFileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            System.out.println("Cancelando operação...");
            return null;
        }
        return jFileChooser.getSelectedFile();
    }
}
