package persistance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class PlainTextDAO {
    private static PlainTextDAO instance;

    private PlainTextDAO() {
    }

    public static PlainTextDAO getInstance() {
        if (instance == null)
            instance = new PlainTextDAO();
        return instance;
    }

    // https://stackoverflow.com/a/4716623
    public String readFileAsString(File file) throws FileNotFoundException, IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            do {
                line = bufferedReader.readLine();
                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
                line = bufferedReader.readLine();
            } while (line != null);

            return stringBuilder.toString();
        }
    }
}
