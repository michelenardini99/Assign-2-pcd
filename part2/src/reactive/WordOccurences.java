package reactive;



import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static utils.WordOccurencesUtils.*;


public class WordOccurences {

    public static Observable<Map<String, Integer>> getWordOccurrences(String url, String word, int depth) {
        return Observable.create((ObservableEmitter<Map<String, Integer>> emitter) -> {
            try {
                Map<String, Integer> report = new HashMap<>();
                fetchWordOccurrences(url, word, depth, report, emitter);
                emitter.onComplete();
            } catch (IOException e) {
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io());
    }

    private static void fetchWordOccurrences(String url, String word, int depth, Map<String, Integer> report, ObservableEmitter<Map<String, Integer>> emitter) throws IOException {
        String content = getContent(url);
        int occurrences = countOccurrences(content, word);
        report.put(url, occurrences);
        emitter.onNext(new HashMap<>(report));

        if (depth > 0) {
            String[] links = extractLinks(content);
            for (String link : links) {
                if(isValidURL(link)) {
                    fetchWordOccurrences(link, word, depth - 1, report, emitter);
                }
            }
        }
    }




}
