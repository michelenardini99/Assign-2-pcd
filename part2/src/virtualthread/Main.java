package virtualthread;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) throws IOException{

        String url2 = "https://example.com";
        String word2 = "example";
        int depth2 = 2;


        Future<Map<String, Integer>> future2 = WordOccurences.getWordOccurrences(url2, word2, depth2, new WordOccurences.ReportCallback() {
            @Override
            public void onUpdate(String url, int occ) {
                System.out.println("Page: " + url + ", Word: " + word2 + ", Occurrences: " + occ + "\n");
            }

            @Override
            public void onComplete() {
                System.out.println("Complete elaboration");
            }
        });

        try{
            Map<String, Integer> report = future2.get();
            System.out.println("Final Report:");
            report.forEach((page, occurrences) -> System.out.println("Page: " + page + ", Occurrences: " + occurrences + "\n"));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
