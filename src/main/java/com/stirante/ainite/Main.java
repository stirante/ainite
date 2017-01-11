package com.stirante.ainite;

import com.stirante.ainite.model.RuleExecutor;
import com.stirante.ainite.model.RuleSet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by stirante
 */
public class Main {


    public static void main(String[] args) {
        test();
    }

    private static void test() {
        InputStream is = Main.class.getResourceAsStream("/rules.txt");
        String script = convertStreamToString(is);
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        RuleSet rules = new RuleSet(script);
        RuleExecutor executor = new RuleExecutor(rules);
        executor.setInput("ocena z a", 5);
        executor.setInput("ocena z b", 4);
        executor.setInput("ocena z c", 5);
        executor.setInput("palisz papierosy", true);
        System.out.println("Result: " + executor.execute());
    }

    private static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
