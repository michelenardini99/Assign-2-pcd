package virtualthread;

import utils.Report;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static utils.WordOccurencesUtils.*;
import static utils.WordOccurencesUtils.isValidURL;

public class WordOccurences {

    private static final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    private static int nTask;
    private static long startTime;
    private static int initialTask;

    public static Future<Map<String, Integer>> getWordOccurrences(String url, String word, int depth, ReportCallback callback) throws IOException {
        AtomicInteger counter = new AtomicInteger(1);
        return executorService.submit(() -> fetchWordOccurrences(url, word, depth, new Report(), callback, counter));
    }

    private static Report fetchWordOccurrences(String url, String word, int depth, Report report, ReportCallback callback, AtomicInteger counter) throws IOException, InterruptedException {
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
            nTask--;
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            System.out.println("Tempo completamento task: " + elapsedTime + " millisecondi");
            if(nTask == 0){
                endTime = System.currentTimeMillis();
                double elapsedTimeSeconds = (endTime - startTime) / 1000.0; // Tempo trascorso in secondi
                double throughput = initialTask / elapsedTimeSeconds;
                System.out.println("Throughput: " + throughput + " req/s");
            }
            callback.onComplete();
        }

        return report;
    }

    public interface ReportCallback{
        void onUpdate(String url, int occ);
        void onComplete();
    }

    public static void setNumTask(int n){
        nTask = n;
        initialTask = n;
    }

    public static void setStartTime(){
        startTime = System.currentTimeMillis();
    }

}
