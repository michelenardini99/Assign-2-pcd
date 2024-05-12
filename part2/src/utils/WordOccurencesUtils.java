package utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class WordOccurencesUtils {

    public static int countOccurrences(String text, String word) {
        int count = 0;
        int index = 0;
        if(text == null) return count;
        while ((index = text.indexOf(word, index)) != -1) {
            count++;
            index += word.length();
        }
        return count;
    }

    public static String[] extractLinks(String html) {
        Document doc = Jsoup.parse(html);
        Elements links = doc.select("a[href]");
        String[] result = new String[links.size()];
        int i = 0;
        for (Element link : links) {
            result[i++] = link.attr("abs:href");
        }
        return result;
    }

    public static boolean isValidURL(String urlString) {
        try {
            new URL(urlString);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static String getContent(String urlString) throws IOException {
        URL url = new URL(urlString);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        }
    }

    public static List<String> generateURLs() {
        // Lista di URL reali per esempio
        List<String> urls = Arrays.asList(
                "https://www.examples.com",
                "https://www.wikipedia.org",
                "https://www.google.com",
                "https://www.ansa.it",
                "https://www.adidas.it/",
                "https://www.youtube.com/"

        );
        return urls;
    }

    public static List<String> generateWords() {
        // Lista di URL reali per esempio
        List<String> words = Arrays.asList(
                "example",
                "wiki",
                "google",
                "vita",
                "uomo",
                "video"
        );
        return words;
    }

}
