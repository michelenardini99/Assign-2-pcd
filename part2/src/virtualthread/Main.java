package virtualthread;

import utils.WordOccurencesUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) throws IOException{
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        int numReq = 500;
        WordOccurences.setNumTask(numReq);
        WordOccurences.setStartTime();
        for(int i = 0; i < numReq; i++){
            futures.add(WordOccurences.getWordOccurrences("https://www.example.com", "example", 1, new WordOccurences.ReportCallback() {
                @Override
                public void onUpdate(String url, int occ) {
                    System.out.println("Page: " + url + ", Word: " + "example" + ", Occurrences: " + occ + "\n");
                }

                @Override
                public void onComplete() {
                    System.out.println("Complete elaboration");
                }
            }));
        }
        while(true){}

        //500 req
        //3758 secondi
        //average cpu_usage 57.6%
        //Thread: ForkJoinPoolWorker 8
        //133.04 richieste per secondo
    }
}
