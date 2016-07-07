package org.uva.rdewildt.mt.utils;

import org.uva.rdewildt.mt.utils.model.Percentage;

import java.util.*;

/**
 * Created by roy on 5/28/16.
 */
public class MapUtils {

    public static <T, U> Map<T, Integer> mapListLenghts(Map<T, ? extends Collection<U>> map) {
        Map<T, Integer> counts = new HashMap<>();
        for (Map.Entry<T, ? extends Collection<U>> col : map.entrySet()) {
            counts.put(col.getKey(), col.getValue().size());
        }
        return counts;
    }


    public static <T> Map<T, Integer> mapTakeByOrderedValue(Map<T, Integer> map, Percentage percentage) {
        int limit = (int) (percentage.getPercentage0to1() * map.size());
        SortedMap<Integer, T> sorted = new TreeMap<>(mapSwapKeyValue(map));
        return mapSwapKeyValue(mapTake(sorted, limit));

    }

    public static <T, U> SortedMap<T, U> mapTake(SortedMap<T, U> map, int limit) {
        SortedMap<T, U> head = new TreeMap<>();
        int i = 0;
        for (Map.Entry<T, U> entry : map.entrySet()) {
            if (i < limit) {
                head.put(entry.getKey(), entry.getValue());
            } else {
                break;
            }
        }
        return head;
    }

    public static <T, U> Map<U, T> mapSwapKeyValue(Map<T, U> map) {
        Map<U, T> rev = new HashMap<>();
        for (Map.Entry<T, U> entry : map.entrySet()) {
            rev.put(entry.getValue(), entry.getKey());
        }
        return rev;
    }

    public static <T, U> Integer mapTotalListLenghts(Map<T, ? extends Collection<U>> map) {
        Integer size = 0;

        for (Collection<U> entry : map.values()) {
            size += entry.size();
        }

        return size;
    }

    public static <T, U> Integer calculateUniqueElements(Map<T, ? extends Collection<U>> map) {
        Set<U> uniqueAuthors = new HashSet<>();
        for (Map.Entry<T, ? extends Collection> entry : map.entrySet()) {
            uniqueAuthors.addAll(entry.getValue());
        }
        return uniqueAuthors.size();
    }

    public static <T, U> SortedSet<U> getSortedSet(Map<T, ? extends Collection<U>> commits) {
        TreeSet<U> flatCommits = new TreeSet<>();
        commits.forEach((s, commitlist) -> flatCommits.addAll(commitlist));
        return flatCommits;
    }

    public static <T> void incrementMapValue(Map<T, Object> map, T key, Integer increment) {
        map.computeIfPresent(key, (k, v) -> v instanceof Integer ? (Integer) v + increment : v);
        map.putIfAbsent(key, increment);
    }


    public static <K, V> List<List<V>> transposeValues(Map<K, List<V>> map) {
        if (map.size() <= 0) {
            return new ArrayList<>();
        }
        List<List<V>> values = new ArrayList<>(map.values());
        List<List<V>> newvalues = new ArrayList<>();
        if (values.get(0) != null) {
            for (int i = 0; i < values.get(0).size(); i++) {
                List<V> row = new ArrayList<>();
                for (int j = 0; j < values.size(); j++) {
                    row.add(values.get(j).get(i));
                }
                newvalues.add(row);
            }
        }
        return newvalues;
    }

    public static <K, V> void addValueToMapList(Map<K, List<V>> map, K key, V value) {
        if (map.get(key) == null) {
            map.put(key, new ArrayList<V>() {{
                add(value);
            }});
        } else {
            List<V> newvalue = map.get(key);
            newvalue.add(value);
            map.put(key, newvalue);
        }
    }

    public static <K, V> Map<K, V> listsToKeyValueMap(List<K> l1, List<V> l2) {
        Map<K, V> map = new HashMap<>();
        Iterator<K> i1 = l1.iterator();
        Iterator<V> i2 = l2.iterator();
        while (i1.hasNext() && i2.hasNext()) {
            map.put(i1.next(), i2.next());
        }
        if (i1.hasNext() || i2.hasNext()) {
            return new HashMap<>();
        } else {
            return map;
        }
    }

    public static <K, V> void addValueToMapSet(Map<K, Set<V>> map, K key, V value) {
        if (!map.containsKey(key)) {
            map.put(key, new HashSet<V>() {{
                add(value);
            }});
        } else {
            Set<V> newvalue = map.get(key);
            newvalue.add(value);
            map.put(key, newvalue);
        }
    }

    public static <K, V> void addValueToMapSet(Map<K, Set<V>> map, K key, Set<V> value) {
        if (!map.containsKey(key)) {
            map.put(key, value);
        } else {
            Set<V> newvalue = map.get(key);
            newvalue.addAll(value);
            map.put(key, newvalue);
        }
    }
}
