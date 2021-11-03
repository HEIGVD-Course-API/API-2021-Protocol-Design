package ch.heigvd.api.calc;

import java.util.Arrays;

/**
 * Utils to translate and verify operators
 */
public class Operator {
    static final String[] know_op = {"+", "-", "*", "/", "%", "="};

    /**
     * Check if string is a valid operator
     * @param op string to check
     * @return true if it's a known operator
     */
    static boolean is_valid(String op) {
        return Arrays.asList(know_op).contains(op);
    }

    /**
     * List known operators as a string
     * @return string of known operators
     */
    static String list() {
        return String.join(" ", know_op);
    }

    /**
     * Translate string op to enum
     * Check if it's a valid and known operator or return null
     * @param op String to check
     * @return Enum translated from string or null
     */
    static OP to_enum(String op) {
        if (!is_valid(op)) return null;

        op = op.toLowerCase();

        switch (op) {
            case "+":
                return OP.PLUS;
            case "-":
                return OP.MINUS;
            case "*":
                return OP.MULT;
            case "/":
                return OP.DIVIDE;
            case "%":
                return OP.MODULUS;
            case "=":
                return OP.AFFECT;
        }

        return null;
    }
}

