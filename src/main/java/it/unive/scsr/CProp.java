package it.unive.scsr;
import java.util.HashMap;
import java.util.Map;

public class CProp {
    private Map<String, String> constantMap; // Map to store constant-variable pairs

    public CProp() {
        constantMap = new HashMap<>();
    }

    /**
     * Assigns a constant value to a variable.
     *
     * @param id      The variable name (identifier).
     * @param value   The value (constant or expression) assigned to the variable.
     */
    public void assignConstant(String id, String value) {
        String evaluatedValue = evaluateExpression(value);
        constantMap.put(id, evaluatedValue);
    }

    /**
     * Retrieves the constant value associated with a variable.
     *
     * @param id      The variable name (identifier).
     * @return        The constant value (or expression) associated with the variable.
     */
    public String getConstant(String id) {
        return constantMap.get(id);
    }

    /**
     * Removes the constant information associated with a variable.
     *
     * @param id      The variable name (identifier) to be removed.
     */
    public void killVariable(String id) {
        constantMap.remove(id);
    }

    /**
     * Evaluates constant expressions (x+y, x-y, x*y, x/y, -x).
     *
     * @param expression   The expression to evaluate.
     * @return             The evaluated result (constant or modified expression).
     */
    private String evaluateExpression(String expression) {
        if (expression.contains("+")) {
            String[] parts = expression.split("\\+");
            String x = parts[0].trim();
            String y = parts[1].trim();
            double result = Double.parseDouble(getConstant(x)) + Double.parseDouble(getConstant(y));
            return String.valueOf(result);
        } else if (expression.contains("-")) {
            String[] parts = expression.split("-");
            String x = parts[0].trim();
            String y = parts[1].trim();
            double result = Double.parseDouble(getConstant(x)) - Double.parseDouble(getConstant(y));
            return String.valueOf(result);
        } else if (expression.contains("*")) {
            String[] parts = expression.split("\\*");
            String x = parts[0].trim();
            String y = parts[1].trim();
            double result = Double.parseDouble(getConstant(x)) * Double.parseDouble(getConstant(y));
            return String.valueOf(result);
        } else if (expression.contains("/")) {
            String[] parts = expression.split("/");
            String x = parts[0].trim();
            String y = parts[1].trim();
            double result = Double.parseDouble(getConstant(x)) / Double.parseDouble(getConstant(y));
            return String.valueOf(result);
        } else if (expression.contains("-")) {
            String[] parts = expression.split("-");
            String x = parts[0].trim();
            String y = parts[1].trim();
            double result = Double.parseDouble(getConstant(x)) - Double.parseDouble(getConstant(y));
            return String.valueOf(result);
           
        }else {
            // Expression doesn't involve arithmetic operations (e.g., just a constant or variable)
            return expression;
        }
    }
}
