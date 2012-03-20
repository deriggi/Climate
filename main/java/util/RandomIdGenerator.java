/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Johnny
 */
public class RandomIdGenerator {
    
    private final static String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v"};
    private final static String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
    
    public static void main(String[] args){
        System.out.println(RandomIdGenerator.getAnId(7));
        System.out.println(RandomIdGenerator.getAnId(7));
        System.out.println(RandomIdGenerator.getAnId(7));
        System.out.println(RandomIdGenerator.getAnId(7));
        System.out.println(RandomIdGenerator.getAnId(7));
        System.out.println(RandomIdGenerator.getAnId(7));
        System.out.println(RandomIdGenerator.getAnId(7));
    }
    public static String getAnId(int length){
        int i = 0;
        StringBuilder sb = new StringBuilder();
        ArrayList<String> letterList = new ArrayList <String>();
        
        while(i++ < length){
            letterList.add(letters[(int)(Math.random()*(letters.length))]);
            letterList.add(numbers[(int)(Math.random()*(numbers.length))]);
        }
        
        Collections.shuffle(letterList);
        for( int j = 0; j < letterList.size();j++){
            sb.append(letterList.get(j));
        }
        
        return sb.toString();
    }
    
}
