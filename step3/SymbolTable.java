import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;

public class SymbolTable
{
    // Persistent Variables
    public static LinkedHashMap<String, ArrayList<List<String>>> map = 
        new LinkedHashMap<>();      // Contains all Symbol Tables
    public static int block = 1;    // Block counter for logical blocks
    public static String error;     // Hold declaration error
    
    // Local Variables
    public SymbolTable next;        // Next symbol table   
    public String scope;            // Scope table belongs to (global, function, etc)
    

    public SymbolTable(String scope)
    {
        if(scope.equals("BLOCK"))
            this.scope = "BLOCK " + (block++);
        else
            this.scope = scope;
    }
    
    public static void addToScope(String s, ArrayList<List<String>> a)
    {
        map.put(s, a);
    }
    
    public static ArrayList<List<String>> getScope(String s)
    {
        return SymbolTable.map.get(s);
    }

    public void checkDuplicate(String id)
    {
        ArrayList<List<String>> vars = map.get(scope);

        if(vars != null)
        {
            for(List<String> v : vars)
            {
                if (v.get(0).equals(id) && error == null)
                {
                    error = id;
                    return;
                }
            }
        }
    }

    public void printTable()
    {
        ArrayList<List<String>> vars = map.get(scope);
	if(scope.compareTo("GLOBAL") != 0)
                System.out.println("\n");

        System.out.print("Symbol table " + scope);

        if(vars != null)
        {
            for(List<String> v : vars)
            {
                System.out.print(
                    "\nname " + 
                    v.get(0) + 
                    " type " + 
                    v.get(1) + 
                    (v.size() == 3 ? (" value " + v.get(2)) : "" ));
            }
        }
    }
}
