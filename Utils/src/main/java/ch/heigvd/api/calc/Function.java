package ch.heigvd.api.calc;

import java.util.Arrays;

/**
 * Known known functions and tools to translate them from string
 */
public class Function {
    static final String[] know_func = {"cos", "sin", "tan", "arcsin", "arccos", "arctan", "sqrt"};

    /**
     * Check if string is a valid and known function
     * @param func string to test
     * @return true if known
     */
    static boolean is_valid(String func) {
        func = func.toLowerCase();
        return Arrays.asList(know_func).contains(func);
    }

    /**
     * Show all known functions as a string
     * @return string of known functions
     */
    static String list() {
        return String.join(" ", know_func);
    }

    /**
     * Translate string to enum
     * @param func func to translate
     * @return Enum converted from string if valid
     */
    static FUNC to_enum(String func) {
        if (!is_valid(func)) return null;

        func = func.toLowerCase();

        switch (func) {
            case "cos":
                return FUNC.COS;
            case "sin":
                return FUNC.SIN;
            case "tan":
                return FUNC.TAN;
            case "arcsin":
                return FUNC.ARCSIN;
            case "arccos":
                return FUNC.ARCCOS;
            case "arctan":
                return FUNC.ARCTAN;
            case "sqrt":
                return FUNC.SQRT;
        }

        return null;
    }
}

