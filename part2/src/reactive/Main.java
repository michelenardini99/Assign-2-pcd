package reactive;

import java.util.concurrent.CountDownLatch;

import static reactive.WordOccurences.getWordOccurrences;

public class Main {

    public static void main(String[] args) {
        String url = "https://example.com";
        String word = "example";
        int depth = 2;
        CountDownLatch latch = new CountDownLatch(1);

        getWordOccurrences(url, word, depth)
                .subscribe(report -> {
                    System.out.println("Report:");
                    report.forEach((page, occurrences) -> System.out.println("Page: " + page + ", Occurrences: " + occurrences + "\n"));
                }, error -> {
                    System.err.println("Error occurred: " + error.getMessage());
                }, () -> {
                    System.out.println("Search completed.");
                    latch.countDown();
                });
        try {
            Thread.sleep(5000); // Aspetta 5 secondi
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            latch.await(); // Attende il completamento della task
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
