package ch.heigvd.api.calc;

import java.util.HashMap;

public class Calc {
  /**
   * Contains the descriptions of all the supported operations
   */
  private static final HashMap<SUPPORTED_OPERATIONS, String> OPERATIONS_DESCRIPTION = new HashMap<>() {
    {
      put(SUPPORTED_OPERATIONS.ADD_2, "Adds two numbers together");
      put(SUPPORTED_OPERATIONS.ADD_3, "Adds three numbers together");
      put(SUPPORTED_OPERATIONS.ADD_4, "Adds four numbers together");
      put(SUPPORTED_OPERATIONS.DIV_2,
          "Divides the first number by the second. The second number cannot be 0. Returns the integer division.");
      put(SUPPORTED_OPERATIONS.FAC_1, "Computes the factorial of the number. The number must be less or equal to 10.");
      put(SUPPORTED_OPERATIONS.MUL_2, "Multiplies two numbers together");
      put(SUPPORTED_OPERATIONS.POW_2, "Raises the first number to the power of the second");
      put(SUPPORTED_OPERATIONS.SUB_2, "Subtract the second number to the first");
    }
  };

  /**
   * Represents all the possible commands for the CALC protocol
   */
  public enum MESSAGES {
    NOTIFY, HELP, COMPUTE, RESULT, ERROR, QUIT
  }

  /**
   * Represents all the supported operations by the CALC protocol ADD_2 and SUB_2
   * are required
   */
  public enum SUPPORTED_OPERATIONS {
    ADD_2, ADD_3, ADD_4, DIV_2, FAC_1, MUL_2, POW_2, SUB_2;

    @Override
    public String toString() {
      return String.join(" ", this.name().split("_"));
    }

    /**
     * Returns the description of the current operation
     * 
     * @return the description
     */
    public String description() {
      return OPERATIONS_DESCRIPTION.get(this);
    }
  }

  /**
   * Represents the error codes provided by the CALC protocol
   */
  public enum ERROR_CODES {
    NONE(0), BAD_REQUEST(400), NOT_FOUND(404), INTERNAL_SERVER_ERROR(500);

    private final int _code;

    private ERROR_CODES(final int code) {
      _code = code;
    }

    @Override
    public String toString() {
      return _code + " " + String.join(" ", this.name().split("_"));
    }
  }
}
