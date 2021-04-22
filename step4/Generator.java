/***********************************************
* Project Step 4 | Generator.java
* Team 8
* Ethan Hannen, Michael Skipper, Renato Xhaferi
************************************************/

import java.util.ArrayList;
import java.util.List;

enum Type
{
	VAR_DECLARATION,
	STR_DECLARATION,
	ASSIGN_STATEMENT,
	READ_STATEMENT,
	WRITE_STATEMENT
}

class Input
{
	Type type;
	String value;
	
	Input(Type t, String v)
	{
		type = t;
		value = v;
	}
}

public class Generator
{
	private static List<Input> Lines;

	public static void init()
	{
		Lines = new ArrayList<Input>();
	}
	
	// Add Methods

	public static void addVarDeclaration(String decl, String type)
	{
		// Add to list of declared variables along with type
		decl = decl.replace(";", "");
		decl = decl.replaceAll("\\s", "");
		Evaluator.vars.put(decl, new String[]{"", "", type});
		Lines.add(new Input(Type.VAR_DECLARATION, decl));
	}
	public static void addStrDeclaration(String name, String value)
	{
		value = value.replace(";", "");
		Evaluator.vars.put(name, new String[]{value, "", "STRING"});
		Lines.add(new Input(Type.STR_DECLARATION, name + " " + value));
	}
	public static void addAssignStatement(String s)
	{
		s = s.replace(";", "");
		s = s.replaceAll("\\s", "");
		Lines.add(new Input(Type.ASSIGN_STATEMENT, s));
	}
	public static void addReadStatement(String s)
	{
		s = s.replace(";", "");
		s = s.replaceAll("\\s", "");
		Lines.add(new Input(Type.READ_STATEMENT, s));
	}
	public static void addWriteStatement(String s)
	{
		s = s.replace(";", "");
		s = s.replaceAll("\\s", "");
		Lines.add(new Input(Type.WRITE_STATEMENT, s));
	}
	
	// Get Methods

	private static String getVarOutput(String s)
	{
		return "\nvar " + s;
	}
	private static String getStrOutput(String s)
	{
		return "\nstr " + s;
	}

	private static String getAssignOutput(String s)
	{
		return Evaluator.generateOutput(s);
    }

	private static String getReadOutput(String s)
	{
		String out = "";
		
		s = s.replaceAll("[()]", ""); // Get rid of parenthesis
		s = s.replace("READ", ""); // Remove READ
		
		String[] vars = s.split(",");
		String[] values;

		for (String var : vars)
		{
			values = Evaluator.vars.get(var);
			
			if (values != null)
				out += "\nsys " + (values[2].equals("INT") ? "readi " : "readr ") + var;
		}
		
		return out;
	}
	private static String getWriteOutput(String s)
	{
		String out = "";
	
		s = s.replaceAll("[()]", ""); // Get rid of parenthesis
		s = s.replace("WRITE", ""); // Remove WRITE
	
		String[] vars = s.split(",");
		String[] values;
		
		for (String var : vars)
		{
			values = Evaluator.vars.get(var);
			if (values != null)
			{
				if (values[2].equals("INT"))
					out += "\nsys writei " + var;
				else if (values[2].equals("FLOAT"))
					out += "\nsys writer " + var;
				else if (values[2].equals("STRING"))
					out += "\nsys writes " + var;
			}
		}
		return out;
	}
	
	// Produce the full tiny code asm
	public static String produce()
	{
		String output = "";

		for (Input line : Lines)
		{
			switch (line.type)
			{
				case VAR_DECLARATION:  output += getVarOutput(line.value);    break;
				case STR_DECLARATION:  output += getStrOutput(line.value);    break;
				case ASSIGN_STATEMENT: output += getAssignOutput(line.value); break;
				case READ_STATEMENT:   output += getReadOutput(line.value);   break;
				case WRITE_STATEMENT:  output += getWriteOutput(line.value);  break;
				default: break;
			}
		}

		return output + "\nsys halt";
	}
}