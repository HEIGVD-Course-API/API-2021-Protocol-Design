package ch.heigvd.api.calc;

/**
 * Known error codes that could happen
 */
public enum ErrorCode {
    DIVIDE_BY_ZERO,     // Dividing by zero
    OVERFLOW,           // Overflow of value
    UNKNOWN_COMMAND,    // Received unknow command (malformated or syntax error)
    INVALID_INPUT,      // Invalid input on number
    UNKNOWN_OPERATOR,   // Unknown operator, not in OP
    UNKNOWN_FUNC        // Unknown func, not in FUNC
}

