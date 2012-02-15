/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author wb385924
 */
public class StudyCodeMatcher {

    public static void main(String[] args){
        new StudyCodeMatcher().testProcessCodes();

       StudyCodeMatcher.isStudyCode("shouldnotbe");
       StudyCodeMatcher.isStudyCode("b1GC18I5");

    }

    private static Logger log = Logger.getLogger(StudyCodeMatcher.class.getName());

    private static String codePattern = "GC\\d{1,2}I[123456]";
    private static Pattern pattern  = null;

    public static boolean isStudyCode(String code){
        if(pattern == null){
            pattern = Pattern.compile(codePattern);
        }

        boolean matches =  pattern.matcher(code).find();

        if(matches){log.log(Level.FINE, "{0} matches code pattern", code);}
        else {log.log(Level.FINE, "{0} does NOT match ocode pattern", code);}

        
        return matches;
    }


    
    private Pattern testCreateCodePattern(){
        Pattern p = Pattern.compile(codePattern);
        return p;
    }

    private void testProcessCodes() {
        Pattern  pattern = testCreateCodePattern();
//        Matcher matcher = pattern.matcher(codePattern);
        String list = readFile("C:\\Users\\wb385924\\Documents\\codes.csv");
//        System.out.println(list);
        String[] codes = list.split(",");
        System.out.println("code list size is: " + codes.length);
        int matchCount = 0;
        for (String s : codes) {
            if(!pattern.matcher(s).find()){
                System.out.println("could not match " + s);
            }else{
                matchCount++;
            }
        }
        System.out.println("count " + matchCount + " num codes" + codes.length);
    }

    private String readFile(String path) {

        StringBuilder sb = new StringBuilder();
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(new FileInputStream(path));

            BufferedReader br = new BufferedReader(isr);
            String line = null;
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (count++ > 0) {
                    sb.append(",");
                }
                sb.append(line);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                isr.close();
            } catch (IOException e) {
                
                e.printStackTrace();
            }
        }

        return sb.toString();

    }
}
