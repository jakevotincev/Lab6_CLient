import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * <p>Класс парсер XML файлов</p>
 *
 * @author Вотинцев Евгений
 * @version 1.0
 */
public class ParserXML {


    public String readXML(String filename) throws FileNotFoundException,NullPointerException {
        FileReader reader = new FileReader(filename);
        Scanner scanner = new Scanner(reader);
        String filestring = "";
        while (scanner.hasNextLine()) {
            filestring += scanner.nextLine() + "\n";
        }
        try {
            reader.close();
        } catch (IOException e) {
            System.err.println("Ошибка");
        }
        return filestring;
    }
}
