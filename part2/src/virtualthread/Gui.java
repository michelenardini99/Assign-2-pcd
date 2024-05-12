package virtualthread;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Gui extends JFrame {

    private JTextField urlField;
    private JTextField wordField;
    private JTextField depthField;
    private JTextArea outputArea;
    private JButton startButton;
    private JButton stopButton;
    private Future<Map<String, Integer>> task;

    public Gui() {
        setTitle("Word Occurrences virtual thread GUI");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Creazione dei componenti
        urlField = new JTextField();
        urlField.setPreferredSize(new Dimension(300, 20));
        wordField = new JTextField();
        wordField.setPreferredSize(new Dimension(300, 20));
        depthField = new JTextField();
        depthField.setPreferredSize(new Dimension(300, 20));
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        startButton = new JButton("Start");
        stopButton = new JButton("Stop");

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outputArea.selectAll();
                outputArea.replaceSelection("");
                String url = urlField.getText();
                String word = wordField.getText();
                int depth = Integer.parseInt(depthField.getText());

                try {
                    task = WordOccurences.getWordOccurrences(url, word, depth, new WordOccurences.ReportCallback() {
                        @Override
                        public void onUpdate(String url, int occ) {
                            outputArea.append("Page: " + url + ", Occurrences: " + occ + "\n");
                        }

                        @Override
                        public void onComplete() {
                            outputArea.append("Complete elaboration");
                        }
                    });
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                task.cancel(true);
                outputArea.append("Process interrupted");
            }
        });

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("URL:"));
        inputPanel.add(urlField);
        inputPanel.add(new JLabel("Word:"));
        inputPanel.add(wordField);
        inputPanel.add(new JLabel("Depth:"));
        inputPanel.add(depthField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Gui();
            }
        });
    }


}
