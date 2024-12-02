import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import ru.StemmerPorterRU;

import java.io.FileInputStream;
import java.io.IOException;

/**
 *  Класс для работы с Docx и Doc файлами
 * @author Котелин Пётр "petya.kotelin@mail.ru"
 * @see FindWords
 */

public class TextFileReader {

    /** Поле файла с текстом */
    public StringBuilder text_file = new StringBuilder();
    public String filePath;

    /**
     * Конструктор класса
     * @param filePath путь к заданному файлу
     */
    public TextFileReader(String filePath){
        this.filePath = filePath;
    }
    /**
     *
     * @return Преобразованный текст из Doc файла
     * @throws IOException ошибка открытия файла
     */
    private StringBuilder readDocFile() throws IOException {
        try (FileInputStream fis = new FileInputStream(this.filePath);
             HWPFDocument document = new HWPFDocument(fis);
             WordExtractor extractor = new WordExtractor(document)) {


            String[] words = extractor.getText().split(" ");
            for (int i = 0; i < words.length; i++) {
                words[i] = words[i].replaceAll("[\\pP\\s]", "").toLowerCase();
                if (words[i].length() > 3) {
                    words[i] = StemmerPorterRU.stem(words[i]);
                    this.text_file.append(words[i]);
                    this.text_file.append(" ");
                }
            }
            return this.text_file;
        }
    }
    /**
     *
     * @return Преобразованный текст из Docx файла
     * @throws IOException ошибка открытия файла
     */
    private StringBuilder readDocxFile() throws IOException {
        try (FileInputStream fis = new FileInputStream(this.filePath);
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {


            String[] words = extractor.getText().split(" ");
            for (int i = 0; i < words.length; i++) {
                words[i] = words[i].replaceAll("[\\pP\\s]", "").toLowerCase();
                if (words[i].length() > 3) {
                    words[i] = StemmerPorterRU.stem(words[i]);
                    this.text_file.append(words[i]);
                    this.text_file.append(" ");
                }
            }
            return this.text_file;
        }
    }

    /**
     *
     * @return Проверяет, допустимые ли расширение у файла
     */
    public boolean isDoc_or_isDocx() {
        return this.filePath.endsWith(".doc") || (this.filePath.endsWith(".docx"));
    }

    /**
     *
     * @return Преобразованные файлы
     * @throws IOException ошибка открытия файла
     */
    public StringBuilder readFile() throws IOException {
        if (this.filePath.endsWith(".doc")) {
            return readDocFile();
        } else{
            return readDocxFile();
        }
    }
}