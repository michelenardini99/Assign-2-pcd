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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static utils.WordOccurencesUtils.*;

public class WordOccurences {

    public static void getWordOccurrences(String url, String word, int depth, Vertx vertx, Handler<Map<String, Integer>> resultHandler) {
        WebClientOptions options = new WebClientOptions().setConnectTimeout(5000);
        WebClient client = WebClient.create(vertx, options);

        Map<String, Integer> report = new HashMap<>();
        AtomicInteger counter = new AtomicInteger(1); // Count la richiesta iniziale
        fetchWordOccurrences(url, word, depth, client, report, counter, resultHandler);
    }

    private static void fetchWordOccurrences(String url, String word, int depth, WebClient client, Map<String, Integer> report, AtomicInteger counter, Handler<Map<String, Integer>> resultHandler) {
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
                            counter.incrementAndGet(); // Incrementa il contatore per ogni link trovato
                            fetchWordOccurrences(link, word, depth - 1, client, report, counter, resultHandler);
                        }
                    }
                }
            }
            if (counter.decrementAndGet() == 0) {
                client.close();
                System.out.println("Exit");
            }
        });
    }

}
