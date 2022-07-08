package persistance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BigIntegersDAO {
    private static final byte[] BIG_INT_BYTE_ARRAY_DELIMITER = { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff };

    private static BigIntegersDAO instance;

    private BigIntegersDAO() {
    }

    public static BigIntegersDAO getInstance() {
        if (instance == null)
            instance = new BigIntegersDAO();
        return instance;
    }

    public BigInteger[] loadBigIntegers(File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        byte[] fileBytes = inputStream.readAllBytes();
        inputStream.close();
        List<byte[]> bytesList = splitBytes(fileBytes, BIG_INT_BYTE_ARRAY_DELIMITER);
        BigInteger[] bigIntegers = new BigInteger[bytesList.size()];
        for (int i = 0; i < bytesList.size(); i++)
            bigIntegers[i] = new BigInteger(bytesList.get(i));
        return bigIntegers;
    }

    public void saveBigIntegers(String filePath, BigInteger[] bigIntegers) throws IOException {
        OutputStream fileOutputStream = new FileOutputStream(filePath);
        for (BigInteger bigInteger : bigIntegers) {
            fileOutputStream.write(bigInteger.toByteArray());
            fileOutputStream.write(BIG_INT_BYTE_ARRAY_DELIMITER);
        }
        fileOutputStream.close();
    }

    // https://stackoverflow.com/a/29084734
    private static List<byte[]> splitBytes(byte[] array, byte[] delimiter) {
        List<byte[]> byteArrays = new LinkedList<>();
        if (delimiter.length == 0) {
            return byteArrays;
        }
        int begin = 0;

        outer: for (int i = 0; i < array.length - delimiter.length + 1; i++) {
            for (int j = 0; j < delimiter.length; j++) {
                if (array[i + j] != delimiter[j])
                    continue outer;
            }
            byteArrays.add(Arrays.copyOfRange(array, begin, i));
            begin = i + delimiter.length;
        }
        if (begin != array.length)
            byteArrays.add(Arrays.copyOfRange(array, begin, array.length));
        return byteArrays;
    }
}
