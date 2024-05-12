package reactive;

import io.reactivex.rxjava3.disposables.Disposable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import utils.Report;

import static reactive.WordOccurences.getWordOccurrences;

public class WorkerTask extends SwingWorker {

    private String url;
    private String word;
    private int depth;
    private JTextArea outputArea;
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

        disposable = getWordOccurrences(url, word, depth)
                .subscribe(report -> {
                    outputArea.append(report.getStringLastEntry());
                }, error -> {
                    outputArea.append("Error occurred: " + error.getMessage());
                }, () -> {
                    outputArea.append("Search completed.");
                });
        return disposable;
    }

    public void cancelTask(){
        outputArea.append("Interrupt");
        disposable.dispose();
    }


}
