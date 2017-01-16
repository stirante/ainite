package com.stirante.ainite;

import java.util.regex.Pattern;

/**
 * Created by stirante
 */
public class Constants {

    /**
     * All names have to be lower or upper case english alphabet letters, numbers or spaces
     */
    public static class Patterns {
        /**
         * input("name", "type")
         * Group 1: name - input name
         * Group 2: type - number/boolean
         */
        public static final Pattern INPUT = Pattern.compile("input\\(\"([a-zA-Z 0-9]+)\", *\"([a-zA-Z 0-9]+)\"\\)");
        /**
         * calculates("name", "js")
         * Group 1: name - variable name
         * Group 2: js - javascript to execute
         */
        public static final Pattern CALCULATE = Pattern.compile("calculate\\(\"([a-zA-Z 0-9]+)\", *\"(.+)\"\\)");
        /**
         * models("name", "js")
         * Group 1: name - models name
         * Group 2: js - javascript to execute
         */
        public static final Pattern MODEL = Pattern.compile("model\\(\"([a-zA-Z 0-9]+)\", *\"(.+)\"\\)");
        /**
         * alias("name", "alias", "description")
         * Group 1: name - rules name
         * Group 2: alias - name publicly visible
         * Group 3: description - rules description
         */
        public static final Pattern ALIAS = Pattern.compile("alias\\(\"([a-zA-Z 0-9]+)\", *\"(.*)\", *\"(.*)\"\\)");
        /**
         * rules("name", ["model1", "model2"])
         * Group 1: name - rules name
         * Group 2: models - list of models
         */
        public static final Pattern RULE = Pattern.compile("rule\\(\"([a-zA-Z 0-9]+)\", *\\[(.+)]\\)");
        /**
         * Group 1: name - models name
         */
        public static final Pattern MODEL_LIST = Pattern.compile("\"([A-Za-z0-9 ]+)\"");
    }
}
