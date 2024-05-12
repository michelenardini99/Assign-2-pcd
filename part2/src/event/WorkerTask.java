package event;

import io.vertx.core.Vertx;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class WorkerTask extends SwingWorker {

    private String url;
    private String word;
    private int depth;
    private JTextArea outputArea;
    private Map<String, Integer> lastReport;
    private Vertx vertx;

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
        vertx = Vertx.vertx();
        WordOccurences.getWordOccurrences(url, word, depth, vertx, report -> {
            outputArea.append(report.getStringLastEntry());
        });
        return lastReport;
    }

    public void cancelTask(){
        this.vertx.close();
    }
}
