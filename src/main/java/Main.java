import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 *  Основной класс приложения для классификации текстов по темам
 * @author Котелин Пётр <petya.kotelin@mail.ru>
 * @version 1.0
 * @see FindWords
 * @see JFrame_Windows
 */

public class Main extends JFrame_Windows {

    /** Поле ссылки на внутренние словари */
    protected static String MAIN_LINK = "src/main/Files_with_words_for_topics/";

    /** Поле словаря для статистики по темам */
    public static Map<String, Map<String,Integer>> dictionary_for_statistic = new HashMap<>();

    /** Поле класс, реализующий поиск слов в тексте */
    public static FindWords Find_Words_In_Topic;

    /** Поле словаря для численной статистики */
    public static ArrayList<Float> Array_for_Statistic = new ArrayList<>();

    private static final Map<String,String> dictionary = new HashMap<>();
    private static final String[] array_of_topics = new String[] {"medical_topics", "historical_topics", " network_topics", "cryptography_topics", "finance_topics", "programming_topics"};
    private static final String[] array_of_topics_russian = new String[] {"Медицина", "История", "Сети", "Криптография", "Финансы", "Программирование"};

    /**
     * Метод, заполняющий словарь ссылками на внутренние словари
     */
    private static void FillDictionary(){
        for (String topic: array_of_topics) {
            dictionary.put(topic, MAIN_LINK + topic + ".txt");
        }
    }

    /**
     * Метод, реализующий заполнение словаря для статистики по темам
     * @param LINK_TO_TEXT_FILE ссылка на текст
     */
    public static void Find_Words(String LINK_TO_TEXT_FILE) throws IOException {
        FillDictionary();

        for (int i=0; i < array_of_topics.length; i++) {
            Find_Words_In_Topic = new FindWords(dictionary.get(array_of_topics[i]), LINK_TO_TEXT_FILE);
            Map<String,Integer> array_for_find_topic_words = Find_Words_In_Topic.Find_Same_words();
            Array_for_Statistic.add(Float.parseFloat(Find_Words_In_Topic.Return_Statistic()));
            tableModel.addRow(new String[]{array_of_topics_russian[i], Find_Words_In_Topic.Return_Statistic()});
            dictionary_for_statistic.put(array_of_topics[i], array_for_find_topic_words);
        }
    }

    @Override
    public void initListeners() {
        button_1.addActionListener(_ -> {
            Link_to_file = textfield1.getText();
            Link_to_save_files = textfield2.getText();
            try {
                new BufferedReader(new FileReader(Link_to_file));
                Find_Words(Link_to_file);

                try(FileWriter writer = new FileWriter(Link_to_save_files + '/' + "Statistic.txt", false)){

                    for (String elem : array_of_topics){
                        Map<String,Integer> Dict_for_topic_words = dictionary_for_statistic.get(elem);
                        writer.append(elem);
                        writer.append('\n');
                        writer.append('\n');
                        for (String key : Dict_for_topic_words.keySet()){
                            writer.append(key);
                            writer.append(" ");
                            String count_words = Integer.toString(Dict_for_topic_words.get(key));
                            writer.append(count_words);
                            writer.append('\n');
                            writer.append('\n');
                        }
                    }
                    button_3.setVisible(true);
                    button_2.setVisible(true);
                    comboBox.setVisible(true);
                }
                catch(IOException ex){
                    button_3.setVisible(false);
                    button_2.setVisible(false);
                    comboBox.setVisible(false);
                    JOptionPane.showMessageDialog(null, "Неправильно указан путь к директории для сохранения!","Message", JOptionPane.ERROR_MESSAGE);
                }
            }
            catch (FileNotFoundException g){
                button_3.setVisible(false);
                button_2.setVisible(false);
                comboBox.setVisible(false);
                JOptionPane.showMessageDialog(null, "Файл не найден!","Message", JOptionPane.ERROR_MESSAGE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        button_2.addActionListener(_ -> {
            try {
                String Choose_topic = (String) comboBox.getSelectedItem();
                int index_for_topic = 0;
                for (int i=0; i < array_of_topics.length; i++){
                    if (array_of_topics[i].equals(Choose_topic)){
                        index_for_topic = i;
                        break;
                    }
                }

                if(Array_for_Statistic.get(index_for_topic) != 0.0) {
                    JFrame frame = new JFrame("Гистограмма для темы: " + array_of_topics_russian[index_for_topic]);
                    HistogramVisualization histogram = new HistogramVisualization(dictionary_for_statistic.get(Choose_topic), frame.getHeight());
                    frame.setLayout(new BorderLayout());

                    // Создаем кнопку для сохранения графика
                    JButton saveButton = new JButton("Сохранить график в JPG");
                    saveButton.addActionListener(_ -> histogram.saveToJPG(Link_to_save_files + "/" + "histogram.jpg"));

                    // Добавляем компонент на фрейм
                    frame.add(histogram, BorderLayout.CENTER);
                    frame.add(saveButton, BorderLayout.SOUTH); // Кнопка снизу

                    frame.setSize(500, 400); // Увеличиваем размер окна
                    frame.setVisible(true);
                }
                else{
                    JOptionPane.showMessageDialog(null, "В тексте не найдены слова, относящиеся к данной тематике!","Message", JOptionPane.ERROR_MESSAGE);
                }
            }
            catch (Exception g){
                JOptionPane.showMessageDialog(null, "Возникла непредвиденная ошибка!","Message", JOptionPane.ERROR_MESSAGE);
            }
        });
        button_3.addActionListener(_ -> {
            JFrame frame_2 = new JFrame("Таблица результатов");
            frame_2.setSize(250, 135);
            frame_2.add(table);
            frame_2.setVisible(true);
        });
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.setVisible(true);
    }
}