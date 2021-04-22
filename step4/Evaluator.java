/***********************************************
* Project Step 4 | Evaluator.java
* Team 8
* Ethan Hannen, Michael Skipper, Renato Xhaferi
************************************************/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Evaluator
{
	// Map to variables name, their values [0] and register equivalence [1]
    static HashMap<String, String[]> vars = new HashMap<String, String[]>();
    static int regCounter = 0;
    static String reg = "r" + String.valueOf(regCounter);
	
	private static void addRegister()
    {
        reg = "r" + String.valueOf(++regCounter); // Increment
    }
    
    private static String assignSingle(String asmt, String expr)
    {
        String out = "";
        if (expr.matches(".*\\d.*")) 
        {   // is Literal
            vars.put(asmt, new String[]{expr, "", (expr.contains(".") ? "FLOAT" : "INT")}); // Update value in map
            return "\nmove " + expr + " " + asmt;
        }
        else
        {
            String[] values = vars.get(expr);
            
            if (values != null && values[0].matches(".*\\d.*"))
            {   // Has a value already
                vars.put(asmt, new String[]{values[0], "", (values[0].contains(".") ? "FLOAT" : "INT")}); // Update value in map
                return "\nmove " + values[0] + " " + asmt;
            }
        }

        String[] values = vars.get(asmt);
        
        vars.put(asmt, new String[]{"", reg, values[2]}); // Update register in map
        out += "\nmove " + expr + " " + reg;
        out += "\nmove " + reg + " " + asmt;
        
        addRegister();
        return out;
    }
    
    private static String genLitExpression(String i1, String i2, String opr)
    {   // Simple mathematical operations
        String out = "";

        if (i1.contains(".") || i2.contains("."))
        {
            switch (opr)
            {
                case "+": out = String.valueOf(Float.valueOf(i1) + Float.valueOf(i2)); break;
                case "-": out = String.valueOf(Float.valueOf(i1) - Float.valueOf(i2)); break;
                case "*": out = String.valueOf(Float.valueOf(i1) * Float.valueOf(i2)); break;
                case "/": out = String.valueOf(Float.valueOf(i1) / Float.valueOf(i2)); break;
            }
        }
        else
        {
            switch (opr)
            {
                case "+": out = String.valueOf(Integer.valueOf(i1) + Integer.valueOf(i2)); break;
                case "-": out = String.valueOf(Integer.valueOf(i1) - Integer.valueOf(i2)); break;
                case "*": out = String.valueOf(Integer.valueOf(i1) * Integer.valueOf(i2)); break;
                case "/": out = String.valueOf(Integer.valueOf(i1) / Integer.valueOf(i2)); break;
            }
        }
        return out;
    }
    
    // Generate a variable + variable expression if needed (instances where value is yet to be read)
    // If value exists or register is equivalent, assign those instead
    private static String genVarExpr(String v1, String v2, String opr, String asmt, boolean isFloat)
    {
        String tReg = reg; // For possibly recycling registers
        String out = "";
        
        if (!v2.matches(".*\\d.*")) // Not a literal, attempt to get value
        {
            String[] values = vars.get(v2);

            if (values != null && values[1] == "") // Not literal and V2 not in prior register, need new
                out = "\nmove " + v2 + " " + reg;
            else // Equivalency in prior register - reuse
                tReg = values[1];
            out += "\n";
        }
        else
        {
            out = "\nmove " + v2 + " " + reg + "\n"; // Move literal into register
        }

        switch (opr)
        {
            case "+": out += (isFloat ? "addr " : "addi "); break;
            case "-": out += (isFloat ? "subr " : "subi "); break;
            case "*": out += (isFloat ? "mulr " : "muli "); break;
            case "/": out += (isFloat ? "divr " : "divi "); break;
        }

        out += v1 + " " + tReg; // Do math against the register
        out += "\nmove " + tReg + " " + asmt; // Move result into memory
        
        String[] values = vars.get(asmt); // Get variable type
        vars.put(asmt, new String[]{"", tReg, values[2]});
        
        if (reg.equals(tReg)) // New register was needed, increment
            addRegister();
        return out;
    }
    
    private static String evaluateTerms(String[] t, String asmt)
    {
        String out = "";

        if (t[0].matches(".*\\d.*") && t[2].matches(".*\\d.*"))
        {
            // Both literals, do simple math and assign directly
            out += assignSingle(asmt, genLitExpression(t[0], t[2], t[1]));
        }
        else if (t[0].matches(".*\\d.*"))
        {
            // Left is a literal, attempt to get value of right variable
            String[] values = vars.get(t[2]);
            if (values != null && values[0].matches(".*\\d.*"))
                out += assignSingle(asmt, genLitExpression(t[0], values[0], t[1]));
            else
                out += genVarExpr(t[2], t[0], t[1], asmt, values[2] == "FLOAT");
        }
        else if (t[2].matches(".*\\d.*"))
        {
            // Right side is a literal, attempt to get value of left variable
            String[] values = vars.get(t[0]);
            if (values != null && values[0].matches(".*\\d.*"))
                out += assignSingle(asmt, genLitExpression(values[0], t[2], t[1]));
            else
                out += genVarExpr(t[2], t[0], t[1], asmt, values[2] == "FLOAT");
        }
        else
        {
            // Both are variables, attempt to get values and simplify/optimize
            String[] v1 = vars.get(t[0]);
            String[] v2 = vars.get(t[2]);
            if (v1 != null && v2 != null && v1[0].matches(".*\\d.*") && v2[0].matches(".*\\d.*"))
                out += assignSingle(asmt, genLitExpression(v1[0], v2[0], t[1]));
            // Send the rest backwards from proper subtraction and division
            else if (v1 != null && v1[0].matches(".*\\d.*"))
                out += genVarExpr(t[2], v1[0], t[1], asmt, v2[2] == "FLOAT");
            else if (v2 != null && v2[0].matches(".*\\d.*"))
                out += genVarExpr(v2[0], t[0], t[1], asmt, v2[2] == "FLOAT");
            else
                out += genVarExpr(t[2], t[0], t[1], asmt, v1[2] == "FLOAT");
        }
        return out;
    }
    
    public static String generateOutput(String s)
    {
		s = s.replaceAll("\\s", ""); // Remove all spaces just in case
	
        String out = "";
        
        // Split on assignment
        String asmt[] = s.split(":=");
        // Remove paranthesis (FUTURE: paranthesis would need to be accounted for)
        asmt[1] = asmt[1].replaceAll("[()]", "");
        
        // Split on +/- first
        String s1[] = asmt[1].split("(?<=[-+])|(?=[-+])");
        
        if (s1.length == 1)
        {
            // This will leave you with whole expressions for * or /
            // FUTURE: Account for multi-tiered expressions like x*n+2/z
            // Would need to iterate over s1 or handle recursively

            // Split on *// second
            String s2[] = s1[0].split("(?<=[*/])|(?=[*/])");
            if (s2.length == 1) // Simple assignment, no expression
                return assignSingle(asmt[0], asmt[1]);
            else
                out += evaluateTerms(s2, asmt[0]); // Evaluate *// expression
        }
        else
        {
            out += evaluateTerms(s1, asmt[0]); // Evaluate +/- expression
        }
        
        return out;
    }
}