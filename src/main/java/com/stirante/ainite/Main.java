package com.stirante.ainite;

import com.stirante.ainite.model.RuleExecutor;
import com.stirante.ainite.model.RuleSet;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by stirante
 */
public class Main {


    public static void main(String[] args) {
        console();
    }

    private static void console() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                System.out.print("Script file path or 'exit': ");
                String s = reader.readLine();
                if (s.equalsIgnoreCase("exit")) return;
                File f = new File(s);
                if (!f.exists()) {
                    System.out.println("File doesn not exist!");
                    continue;
                }
                FileInputStream is = new FileInputStream(f);
                RuleSet rules = new RuleSet(convertStreamToString(is));
                is.close();
                RuleExecutor executor = new RuleExecutor(rules);
                for (String s1 : rules.getInputs().keySet()) {
                    System.out.print(s1 + ": ");
                    String s2 = reader.readLine();
                    executor.setInput(s1, s2);
                }
                ArrayList<String> result = executor.execute();
                System.out.println("Result: " + result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        executor.setInput("ocena z a", "2");
        executor.setInput("ocena z b", "5");
        executor.setInput("ocena z c", "2");
        executor.setInput("palisz papierosy", "true");
        System.out.println("Result: " + executor.execute());
    }

    private static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
