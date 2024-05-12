package event;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;

public class Main {



    public static void main(String[] args){
        Vertx vertx = Vertx.vertx();
        HttpClient client = vertx.createHttpClient();

        int numRequests = 500; // Numero di richieste da inviare
        WordOccurences.setStartTime();
        WordOccurences.setNumTask(numRequests);

        for(int i=0; i<numRequests; i++){
            WordOccurences.getWordOccurrences("https://www.example.com", "example", 1, vertx, report -> {
                System.out.println(report.getStringLastEntry());
            });
        }

        //num request: 500
        //avarage cpu-usage: 28.6%
        //thread-event-loop: 15
        //Tempo totale: 11428 millsecondi
        //43 richieste/secondo

    }
}
