package com.stirante.ainite.model;

import com.stirante.ainite.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

/**
 * Created by stirante
 */
public class RuleSet {

    private HashMap<String, String> inputs = new HashMap<>();
    private HashMap<String, String> calculates = new HashMap<>();
    private HashMap<String, String> models = new HashMap<>();
    private ArrayList<Rule> rules = new ArrayList<>();
    private HashMap<String, RuleAlias> aliases = new HashMap<>();

    public RuleSet(String script) {
        String[] lines = script.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.startsWith("#") || line.isEmpty() || line.length() == 1) continue;
            boolean found = false;
            InstructionParser[] parsers = new InstructionParser[]{new InputParser(), new ModelParser(), new RuleParser(), new CalculateParser(), new AliasParser()};
            for (InstructionParser parser : parsers) {
                if (parser.parse(line)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("Error parsing line " + i + ":");
                System.out.println("> " + line);
            }
        }
    }

    public HashMap<String, String> getCalculates() {
        return calculates;
    }

    public HashMap<String, String> getModels() {
        return models;
    }

    public ArrayList<Rule> getRules() {
        return rules;
    }

    public HashMap<String, RuleAlias> getAliases() {
        return aliases;
    }

    public HashMap<String, String> getInputs() {
        return inputs;
    }

    class RuleAlias {
        private String alias;
        private String description;

        public RuleAlias(String alias, String description) {

            this.alias = alias;
            this.description = description;
        }

        public String getAlias() {
            return alias;
        }

        public String getDescription() {
            return description;
        }
    }

    class Rule {
        private String name;
        private ArrayList<String> models;

        public Rule(String name, ArrayList<String> models) {
            this.name = name;
            this.models = models;
        }

        public String getName() {
            return name;
        }

        public ArrayList<String> getModels() {
            return models;
        }
    }

    abstract class InstructionParser {
        public abstract boolean parse(String line);
    }

    class InputParser extends InstructionParser {

        @Override
        public boolean parse(String line) {
            Matcher matcher = Constants.Patterns.INPUT.matcher(line);
            if (matcher.find()) {
                inputs.put(matcher.group(1), matcher.group(2));
                return true;
            }
            return false;
        }
    }

    class ModelParser extends InstructionParser {

        @Override
        public boolean parse(String line) {
            Matcher matcher = Constants.Patterns.MODEL.matcher(line);
            if (matcher.find()) {
                models.put(matcher.group(1), matcher.group(2));
                return true;
            }
            return false;
        }
    }

    class RuleParser extends InstructionParser {

        @Override
        public boolean parse(String line) {
            Matcher matcher = Constants.Patterns.RULE.matcher(line);
            if (matcher.find()) {
                ArrayList<String> list = new ArrayList<>();
                Matcher matcher1 = Constants.Patterns.MODEL_LIST.matcher(matcher.group(2));
                while (matcher1.find()) {
                    list.add(matcher1.group(1));
                }
                rules.add(new Rule(matcher.group(1), list));
                return true;
            }
            return false;
        }
    }

    class AliasParser extends InstructionParser {

        @Override
        public boolean parse(String line) {
            Matcher matcher = Constants.Patterns.ALIAS.matcher(line);
            if (matcher.find()) {
                aliases.put(matcher.group(1), new RuleAlias(matcher.group(2), matcher.group(3)));
                return true;
            }
            return false;
        }
    }

    class CalculateParser extends InstructionParser {

        @Override
        public boolean parse(String line) {
            Matcher matcher = Constants.Patterns.CALCULATE.matcher(line);
            if (matcher.find()) {
                calculates.put(matcher.group(1), matcher.group(2));
                return true;
            }
            return false;
        }
    }

}
