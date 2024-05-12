package reactive;

import utils.WordOccurencesUtils;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static reactive.WordOccurences.getWordOccurrences;

public class Main {

    public static void main(String[] args) {
        int numReq = 500;
        WordOccurences.setNumTask(numReq);
        WordOccurences.setStartTime();
        for(int i = 0; i < numReq; i++){
            getWordOccurrences("https://www.example.com", "example", 1)
                    .subscribe(report -> {
                        System.out.println(report.getStringLastEntry());
                    }, error -> {
                        System.err.println("Error occurred: " + error.getMessage());
                    }, () -> {
                        System.out.println("Search completed.");
                    });
        }

       while (true){}
    }
    //500 request
    //avarage cpu-usage: 19%
    //usage thread: 500 RxCachedThreadScheduler
    //5143 millisecondi
    //97.2 richieste per second
}
