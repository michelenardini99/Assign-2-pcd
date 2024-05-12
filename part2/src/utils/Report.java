package utils;

import java.util.*;

public class Report extends LinkedHashMap<String, Integer> {

    public Report() {
        super();
    }

    public Map.Entry<String, Integer> getLastEntry() {
        Optional<Map.Entry<String, Integer>> optionalLastEntry = entrySet().stream().reduce((first, second) -> second);
        return optionalLastEntry.orElse(null);
    }

    public String getStringLastEntry(){
        Map.Entry<String, Integer> lastEntry = getLastEntry();
        return "Link: " + lastEntry.getKey() + ", Occurrences: " + lastEntry.getValue() +"\n";
    }

}
