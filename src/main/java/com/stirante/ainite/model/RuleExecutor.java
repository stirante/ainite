package com.stirante.ainite.model;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by stirante
 */
public class RuleExecutor {

    private static ScriptEngine engine;
    private final RuleSet rules;
    private HashMap<String, Float> numbers = new HashMap<>();
    private HashMap<String, Boolean> bools = new HashMap<>();

    public RuleExecutor(RuleSet rules) {
        if (engine == null) {
            ScriptEngineManager factory = new ScriptEngineManager();
            engine = factory.getEngineByName("JavaScript");
        }
        this.rules = rules;
    }

    public RuleSet getRuleSet() {
        return rules;
    }

    public void setInput(String name, String value) {
        if (!rules.getInputs().containsKey(name)) throw new IllegalArgumentException("Input name '" + name + "' not found!");
        if (rules.getInputs().get(name).equalsIgnoreCase("number")) {
            numbers.put(name, Float.parseFloat(value));
        } else if (rules.getInputs().get(name).equalsIgnoreCase("boolean")) {
            bools.put(name, Boolean.parseBoolean(value));
        }
        System.out.println("Input: " + name + " = " + value);
    }

    public void clear() {
        numbers.clear();
        bools.clear();
    }

    public ArrayList<String> execute() {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> missing = new ArrayList<>();
        //check for missing input variables and set them to default value
        for (String s : rules.getInputs().keySet()) {
            if (rules.getInputs().get(s).equalsIgnoreCase("number") && !numbers.containsKey(s)) {
                missing.add(s);
                numbers.put(s, 0F);
            } else if (rules.getInputs().get(s).equalsIgnoreCase("boolean") && !bools.containsKey(s)) {
                missing.add(s);
                bools.put(s, false);
            }
        }
        //warn about it
        if (!missing.isEmpty()) {
            System.out.println("Missing input variables: " + missing);
        }
        //execute calculates
        for (Map.Entry<String, String> e : rules.getCalculates().entrySet()) {
            String js = e.getValue();
            for (Map.Entry<String, Float> e1 : numbers.entrySet()) {
                js = js.replaceAll("'" + e1.getKey() + "'", String.valueOf(e1.getValue()));
            }
            for (Map.Entry<String, Boolean> e1 : bools.entrySet()) {
                js = js.replaceAll("'" + e1.getKey() + "'", String.valueOf(e1.getValue()));
            }
            Object obj = null;
            try {
                obj = engine.eval(js);
            } catch (ScriptException e1) {
                e1.printStackTrace();
            }
            if (obj instanceof Boolean) {
                bools.put(e.getKey(), (Boolean) obj);
            } else if (obj != null) {
                numbers.put(e.getKey(), Float.parseFloat(obj.toString()));
            }
            System.out.println("Calculate: " + e.getKey() + " = " + obj);
        }
        //execute models
        for (Map.Entry<String, String> e : rules.getModels().entrySet()) {
            String js = e.getValue();
            for (Map.Entry<String, Float> e1 : numbers.entrySet()) {
                js = js.replaceAll("'" + e1.getKey() + "'", String.valueOf(e1.getValue()));
            }
            for (Map.Entry<String, Boolean> e1 : bools.entrySet()) {
                js = js.replaceAll("'" + e1.getKey() + "'", String.valueOf(e1.getValue()));
            }
            Boolean obj = null;
            try {
                obj = (Boolean) engine.eval(js);
            } catch (ScriptException e1) {
                System.out.println("Error while executing script: " + e1.getMessage());
                System.out.println("> " + e.getValue());
            } catch (ClassCastException e1) {
                System.out.println("Error while executing script: Returned value is not a boolean!");
                System.out.println("> " + e.getValue());
            }
            if (obj != null) {
                bools.put(e.getKey(), obj);
            }
            System.out.println("Model: " + e.getKey() + " = " + obj);
        }
        //execute rules
        for (RuleSet.Rule e : rules.getRules()) {
            if (result.contains(e.getName())) continue;
            ArrayList<String> rs = e.getModels();
            boolean good = true;
            for (String r : rs) {
                if (!bools.containsKey(r) || !bools.get(r)) {
                    good = false;
                    break;
                }
            }
            if (good) result.add(e.getName());
        }
        clear();
        return result;
    }

}
