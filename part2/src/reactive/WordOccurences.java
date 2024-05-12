package reactive;



import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.schedulers.Schedulers;
import utils.Report;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static utils.WordOccurencesUtils.*;


public class WordOccurences {

    private static int nTask;
    private static long startTime;
    private static int initialTask;

    public static Observable<Report> getWordOccurrences(String url, String word, int depth) {
        AtomicInteger counter = new AtomicInteger(1); // Count la richiesta iniziale
        return Observable.create((ObservableEmitter<Report> emitter) -> {
            try {
                fetchWordOccurrences(url, word, depth, new Report(), emitter, counter);
                emitter.onComplete();
            } catch (IOException e) {
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io());
    }

    private static void fetchWordOccurrences(String url, String word, int depth, Report report, ObservableEmitter<Report> emitter, AtomicInteger counter) throws IOException, InterruptedException {
        String content = getContent(url);

        int occurrences = countOccurrences(content, word);
        report.put(url, occurrences);
        emitter.onNext(report);

        if (depth > 0) {
            String[] links = extractLinks(content);
            for (String link : links) {
                if(isValidURL(link)) {
                    counter.incrementAndGet();
                    fetchWordOccurrences(link, word, depth - 1, report, emitter, counter);
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
            emitter.onComplete();
        }
    }

    public static void setNumTask(int n){
        nTask = n;
        initialTask = n;
    }

    public static void setStartTime(){
        startTime = System.currentTimeMillis();
    }


}
