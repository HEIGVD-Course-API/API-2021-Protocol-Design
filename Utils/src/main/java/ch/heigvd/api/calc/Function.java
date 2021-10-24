package ch.heigvd.api.calc;

import java.util.Arrays;

public class Function {
    static final String[] know_func = {"cos", "sin", "tan", "arcsin", "arccos", "arctan", "sqrt"};

    static boolean is_valid(String func) {
        func = func.toLowerCase();
        return Arrays.asList(know_func).contains(func);
    }

    static String list() {
        return String.join(" ", know_func);
    }

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

