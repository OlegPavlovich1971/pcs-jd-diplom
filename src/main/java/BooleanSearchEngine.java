import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class BooleanSearchEngine implements SearchEngine {
//    Полем BooleanSearchEngine задаем мапу (ключ-искомое слово,
//    значение - список результатов поиска на каждой странице pdf-документа
//    в заданной в конструкторе директории):
    private final Map<String, List<PageEntry>> word_SearchResult = new HashMap<>();

    public BooleanSearchEngine(String DirName) throws IOException {
        // прочтите тут все pdf и сохраните нужные данные,
        // тк во время поиска сервер не должен уже читать файлы

//        В список listOfPdf извлекаем файлы из предложенной папки DirName:
        List<File> listOfPdf = Files.walk(Paths.get(DirName))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());
//        Проходим циклом по всем файлам списка listOfPdf. Принимаем, что все они pdf.
        for (File file : listOfPdf) {
//            Получаем имя файла для внесения в pageEntry:
            String pdfName = file
                    .getName();
//            Получаем объект pdf-файла для дальнейшей обработки:
            var doc = new PdfDocument(new PdfReader(file));
//            Проходим циклом по всем страницам полученного объекта doc:
            for (int j = 0; j < doc.getNumberOfPages(); j++) {
//                Получаем номер страницы для внесения в pageEntry:
                int page = j + 1;
//                Получаем текст со страницы:
                var text = PdfTextExtractor.getTextFromPage(doc.getPage(j + 1));
//                Получаем массив слов со страницы независимо от разделителя:
                var words = text.split("\\P{IsAlphabetic}+");
//                Создаем промежуточную мапу freqs:
                Map<String, Integer> freqs = new HashMap<>();
//                Проходим циклом по всем непустым словам страницы, и заполняем freqs (ключ-слово,
//                значение - количество упоминаний на странице):
                for (var word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    freqs.put(word.toLowerCase(), freqs.getOrDefault(word.toLowerCase(), 0) + 1);
                }
//                Проходим циклом по мапе freqs: ключ word используем как ключ и для word_SearchResult,
//                а значением заполняем поле count локальной переменной pageEntry:
                for (Map.Entry<String, Integer> entry : freqs.entrySet()) {
                    String word = entry.getKey();
//                    Создаем объект pageEntry:
                    PageEntry pageEntry = new PageEntry(pdfName, page, entry.getValue());
//                    По ключу word извлекаем значение из мапы word_SearchResult и дополняем его новым
//                    объектом pageEntry, после чего перезаписываем в word_SearchResult:
                    List<PageEntry> pageEntryList = new ArrayList<>();
                    if (word_SearchResult.get(word) != null) {
                        pageEntryList = word_SearchResult.get(word);
                        pageEntryList.add(pageEntry);
                    } else pageEntryList.add(pageEntry);
                    pageEntryList.sort(Collections.reverseOrder());
                    word_SearchResult.put(word, pageEntryList);
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        return word_SearchResult.get(word);
    }

    @Override
    public String toString() {
        return "BooleanSearchEngine{" +
                "word_SearchResult=" + word_SearchResult +
                '}';
    }
}
