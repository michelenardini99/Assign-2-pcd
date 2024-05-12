package event;

import io.vertx.core.Vertx;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args){
        Vertx vertx = Vertx.vertx();

        String url = "https://example.com/";
        String word = "example";
        int depth = 1;

        Map<String, Integer> lastReport = new HashMap<>();

        WordOccurences.getWordOccurrences(url, word, depth, vertx, report -> {
            report.forEach( (link, occurences) -> {
                if(!lastReport.containsKey(link)){
                    lastReport.put(link, occurences);
                    System.out.println("Link: " + link + " - Occurences: " + occurences);
                }

            });
        });





    }
}
