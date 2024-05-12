package virtualthread;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static utils.WordOccurencesUtils.*;
import static utils.WordOccurencesUtils.isValidURL;

public class WordOccurences {

    private static final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();


    public static Future<Map<String, Integer>> getWordOccurrences(String url, String word, int depth, ReportCallback callback) throws IOException {
        Map<String, Integer> report = new HashMap<>();
        AtomicInteger counter = new AtomicInteger(1);
        return executorService.submit(() -> fetchWordOccurrences(url, word, depth, report, callback, counter));
    }

    private static Map<String, Integer> fetchWordOccurrences(String url, String word, int depth, Map<String, Integer> report, ReportCallback callback, AtomicInteger counter) throws IOException {
        String content = getContent(url);
        int occurrences = countOccurrences(content, word);
        report.put(url, occurrences);
        callback.onUpdate(url, occurrences);
        if (depth > 0) {
            String[] links = extractLinks(content);
            for (String link : links) {
                if(isValidURL(link)) {
                    counter.incrementAndGet();
                    report.putAll(fetchWordOccurrences(link, word, depth - 1, report, callback, counter));
                }
            }
        }
        if (counter.decrementAndGet() == 0) {

            callback.onComplete();
        }
        return report;
    }

    public interface ReportCallback{
        void onUpdate(String url, int occ);
        void onComplete();
    }

}
