package event;

import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.Report;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static utils.WordOccurencesUtils.*;

public class WordOccurences {

    private static int nTask;
    private static int initialTask;
    private static long startTime;

    public static void setNumTask(int n){
        nTask = n;
        initialTask = n;
    }

    public static void setStartTime(){
        startTime = System.currentTimeMillis();
    }

    public static void getWordOccurrences(String url, String word, int depth, Vertx vertx, Handler<Report> resultHandler) {

        AtomicInteger counter = new AtomicInteger(1); // Count la richiesta iniziale
        fetchWordOccurrences(url, word, depth, vertx, new Report(), counter, resultHandler);
    }

    private static void fetchWordOccurrences(String url, String word, int depth, Vertx vertx, Report report, AtomicInteger counter, Handler<Report> resultHandler) {
        WebClient client = WebClient.create(vertx);
        client.getAbs(url).send(ar -> {
            if (ar.succeeded()) {
                HttpResponse<Buffer> response = ar.result();
                String responseBody = response.bodyAsString();
                int occurrences = countOccurrences(responseBody, word);
                report.put(url, occurrences);
                resultHandler.handle(report);
                if (depth > 0) {
                    String[] links = extractLinks(responseBody);
                    for (String link : links) {
                        if(isValidURL(link)) {
                            counter.incrementAndGet();
                            fetchWordOccurrences(link, word, depth - 1, vertx, report, counter, resultHandler);
                        }
                    }
                }
            }
            if (counter.decrementAndGet() == 0) {
                client.close();
                nTask--;
                long endTime = System.currentTimeMillis();
                long elapsedTime = endTime - startTime;
                System.out.println("Tempo completamento task: " + elapsedTime + " millisecondi");
                if(nTask == 0){
                    endTime = System.currentTimeMillis();
                    double elapsedTimeSeconds = (endTime - startTime) / 1000.0; // Tempo trascorso in secondi
                    double throughput = initialTask / elapsedTimeSeconds;
                    System.out.println("Throughput: " + throughput + " req/s");
                    vertx.close();
                }
            }
        });
    }

}
