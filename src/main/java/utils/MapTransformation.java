package utils;

import java.util.*;

public class MapTransformation {

    public static <K, V> void addValueToMapList(Map<K, List<V>> map, K key, V value) {
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList<V>() {{ add(value); }});
        }
        else {
            List<V> newvalue = map.get(key);
            newvalue.add(value);
            map.put(key, newvalue);
        }
    }

    public static <T,U,V> Map<T,U> filterContains(Map<T,U> toFilter, Map<T,V> toCheck){
        Map<T,U> result = new HashMap<>();
        for(Map.Entry<T,U> entry : toFilter.entrySet()){
            if(toCheck.containsKey(entry.getKey())){
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public static <K, V extends Collection> Map<K, Integer> valueCounts(Map<K, V> values){
        Map<K, Integer> counts = new HashMap<>();

        for(Map.Entry<K, V> entry : values.entrySet()){
            counts.put(entry.getKey(), entry.getValue().size());
        }

        return counts;
    }

    public static <K, V extends Collection> Integer sumValueLengths(Map<K,V> map){
        Integer counter = 0;
        for(V value : map.values()){
            counter += value.size();
        }
        return counter;
    }

    public static <K,V> List<List<V>> transposeValues(Map<K, List<V>> map){
        List<List<V>> values = new ArrayList<>(map.values());
        List<List<V>> newvalues = new ArrayList<>();
        for(int i = 0; i < values.get(0).size(); i++){
            List<V> row = new ArrayList<>();
            for(int j = 0; j < values.size(); j++){
                row.add(values.get(j).get(i));
            }
            newvalues.add(row);
        }
        return newvalues;
    }
}
