package ru.pixelmongo.pixelmongo.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomUtils {

    private static char[] keyChars;

    static {
        List<Character> chars = new ArrayList<Character>();
        for(int i = 'a'; i <= 'z'; i++)
            chars.add((char)i);
        for(int i = 0; i <= 9; i++)
            chars.add((char)('0'+i));
        for(int i = 'A'; i <= 'Z'; i++)
            chars.add((char)i);
        keyChars = new char[chars.size()];
        for(int i = 0; i < keyChars.length; i++)
            keyChars[i] = chars.get(i);
    }

    public static String generateRandomKey() {
        return generateRandomKey(32);
    }

    public static String generateRandomKey(int length) {
        Random r = new Random();
        StringBuilder key = new StringBuilder();
        for(int i = 0; i < length; i++)
            key.append(keyChars[r.nextInt(keyChars.length)]);
        return key.toString();
    }

    /**
     * Get random element from list
     * @param <T>
     * @param list
     * @param defaultValue - value to return if list is empty or got null value.
     * @return
     */
    public static <T> T randomElement(List<T> list, T defaultValue) {
        int size = list.size();
        if(size == 0) return defaultValue;
        T val;
        if(size == 1) {
            val = list.get(0);
        }else {
            Random r = new Random();
            val = list.get(r.nextInt(size));
        }
        return val == null ? defaultValue : val;
    }

}
