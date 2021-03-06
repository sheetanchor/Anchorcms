package com.anchorcms.common.utils;

import java.util.*;
import java.util.Map.Entry;

public class MapUtil {
	
	/** 
     * 使用 Map按key进行排序 
     * @param map 
     * @return 
     */  
    public static Map<String, String> sortMapByKey(Map<String, String> map) {
        return sortMap(1, map) ; 
    }
    
	/** 
     * 使用 Map按value进行排序 
     * @param map 
     * @return 
     */  
    public static Map<String, String> sortMapByValue(Map<String, String> map) {
        return sortMap(2, map) ;
    } 
    
    private static Map<String, String> sortMap(int sortBy, Map<String, String> map) {
        if (map == null || map.isEmpty()) {  
            return null;  
        }  
        Map<String, String> sortedMap = new LinkedHashMap<String, String>();
        List<Entry<String, String>> entryList = new ArrayList<Entry<String, String>>(map.entrySet());
        if(sortBy==1){
        	 Collections.sort(entryList, new MapKeyComparator());
        }else{
        	Collections.sort(entryList, new MapValueComparator());
        }
        Iterator<Entry<String, String>> iter = entryList.iterator();
        Entry<String, String> tmpEntry = null;
        while (iter.hasNext()) {  
            tmpEntry = iter.next();  
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());  
        }  
        return sortedMap;  
    } 
}

class MapValueComparator implements Comparator<Entry<String, String>> {
    public int compare(Entry<String, String> me1, Entry<String, String> me2) {
        return me1.getValue().compareTo(me2.getValue());  
    }  
}

class MapKeyComparator implements Comparator<Entry<String, String>> {
    public int compare(Entry<String, String> me1, Entry<String, String> me2) {
        return me1.getKey().compareTo(me2.getKey());  
    }  
} 
