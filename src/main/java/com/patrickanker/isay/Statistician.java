/*
 * Statistician.java
 *
 * Project: iSay
 *
 * Copyright (C) Patrick Anker 2011 - 2012. All rights reserved.
 * 
 * iSay by Patrick Anker is licensed under a Creative Commons 
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 */

package com.patrickanker.isay;

import java.util.HashMap;


public class Statistician {

    private static Statistician singleton;
    
    private final HashMap<String, Integer> integerStorage = new HashMap<String, Integer>();
    private final HashMap<String, Double> doubleStorage = new HashMap<String, Double>();
    private final HashMap<String, Long> longStorage = new HashMap<String, Long>();
    
    public static Statistician getStats()
    {
        if (singleton == null)
            singleton = new Statistician();
        
        return singleton;
    }
    
    public void updateInt(String key, int i)
    {
        integerStorage.put(key, i);
    }
    
    public int fetchInt(String key)
    {
        if (integerStorage.containsKey(key))
            return integerStorage.get(key);
        
        return -1;
    }
    
    public void updateDouble(String key, double d)
    {
        doubleStorage.put(key, d);
    }
    
    public double fetchDouble(String key)
    {
        if (doubleStorage.containsKey(key))
            return doubleStorage.get(key);
        
        return -1;
    }
    
    public void updateLong(String key, long l)
    {
        longStorage.put(key, l);
    }
    
    public long fetchLong(String key)
    {
        if (longStorage.containsKey(key))
            return longStorage.get(key);
        
        return -1;
    }
    
    public int sumInts(String... keys)
    {
        int i = 0;
        
        for (String key : keys) {
            i += integerStorage.get(key);
        }
        
        return i;
    }
    
    public int productInts(String... keys)
    {
        int i = 1;
        
        for (String key : keys) {
            i *= integerStorage.get(key);
        }
        
        return i;
    }
    
    public double sumDoubles(String... keys)
    {
        double i = 0;
        
        for (String key : keys) {
            i += doubleStorage.get(key);
        }
        
        return i;
    }
    
    public double productDoubles(String... keys)
    {
        double i = 1;
        
        for (String key : keys) {
            i *= doubleStorage.get(key);
        }
        
        return i;
    }
    
    public long sumLongs(String... keys)
    {
        long i = 0;
        
        for (String key : keys) {
            i += longStorage.get(key);
        }
        
        return i;
    }
    
    public long productLongs(String... keys)
    {
        long i = 1;
        
        for (String key : keys) {
            i *= longStorage.get(key);
        }
        
        return i;
    }
}
