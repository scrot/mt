package org.uva.rdewildt.mt.utils;

import org.uva.rdewildt.mt.utils.model.Percentage;

import java.util.*;

/**
 * Created by roy on 5/28/16.
 */
public class MapUtils {

    public static <T,U> Map<T, Integer> mapListLenghts(Map<T, ? extends Collection<U>> map){
        Map<T, Integer> counts = new HashMap<>();
        for(Map.Entry<T, ? extends Collection<U>> col : map.entrySet()){
            counts.put(col.getKey(), col.getValue().size());
        }
        return counts;
    }


    public static  <T> Map<T, Integer> mapTakeByOrderedValue(Map<T, Integer> map, Percentage percentage){
        int limit =  (int) (percentage.getPercentage0to1() * map.size());
        SortedMap<Integer, T> sorted = new TreeMap<>(mapSwapKeyValue(map));
        return mapSwapKeyValue(mapTake(sorted, limit));

    }

    public static <T,U> SortedMap<T,U> mapTake(SortedMap<T,U> map, int limit){
        SortedMap<T,U> head = new TreeMap<>();
        int i = 0;
        for(Map.Entry<T,U> entry : map.entrySet()){
            if(i < limit){
                head.put(entry.getKey(), entry.getValue());
            }
            else {
                break;
            }
        }
        return head;
    }

    public static  <T,U> Map<U,T> mapSwapKeyValue(Map<T,U> map){
        Map<U,T> rev = new HashMap<>();
        for(Map.Entry<T,U> entry : map.entrySet()){
            rev.put(entry.getValue(), entry.getKey());
        }
        return rev;
    }

    public static  <T,U> Integer mapTotalListLenghts(Map<T, ? extends Collection<U>> map){
        Integer size = 0;

        for(Collection<U> entry : map.values()){
            size += entry.size();
        }

        return size;
    }

    public static  <T,U> Integer calculateUniqueElements(Map<T, ? extends Collection<U>> map){
        Set<U> uniqueAuthors = new HashSet<>();
        for(Map.Entry<T, ? extends Collection> entry : map.entrySet()){
            uniqueAuthors.addAll(entry.getValue());
        }
        return uniqueAuthors.size();
    }

    public static  <T,U> SortedSet<U> getSortedSet(Map<T, ? extends Collection<U>> commits){
        TreeSet<U> flatCommits = new TreeSet<>();
        commits.forEach((s, commitlist) -> flatCommits.addAll(commitlist));
        return flatCommits;
    }
}
