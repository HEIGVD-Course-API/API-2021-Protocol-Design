package ch.heigvd.api.calc;

import java.util.Arrays;

public class Operator {
    static final String[] know_op = {"+", "-", "*", "/", "%"};

    static boolean is_valid(String op) {
        op = op.toLowerCase();
        return Arrays.asList(know_op).contains(op);
    }

    static String list() {
        return String.join(" ", know_op);
    }

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
        }

        return null;
    }
}

