package reactive;

import io.reactivex.rxjava3.disposables.Disposable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

import static reactive.WordOccurences.getWordOccurrences;

public class WorkerTask extends SwingWorker {

    private String url;
    private String word;
    private int depth;
    private JTextArea outputArea;
    private Map<String, Integer> lastReport;
    Disposable disposable;

    public WorkerTask(String url, String word, int depth, JTextArea textArea) {
        this.url = url;
        this.word = word;
        this.outputArea = textArea;
        this.depth = depth;
    }
    @Override
    protected Object doInBackground() throws Exception {
        outputArea.selectAll();
        outputArea.replaceSelection("");
        outputArea.append("Start processing\n");
        lastReport = new HashMap<>();

        disposable = getWordOccurrences(url, word, depth)
                .subscribe(report -> {
                    report.forEach((page, occurrences) -> {
                        if (!lastReport.containsKey(page)){
                            lastReport.put(page, occurrences);
                            outputArea.append("Page: " + page + ", Occurrences: " + occurrences + "\n");
                        }
                    });
                }, error -> {
                    outputArea.append("Error occurred: " + error.getMessage());
                }, () -> {
                    outputArea.append("Search completed.");
                });
        return lastReport;
    }

    public void cancelTask(){
        outputArea.append("Interrupt");
        disposable.dispose();
    }


}
