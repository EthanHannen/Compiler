import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;

public class SymbolTable
{
    public static LinkedHashMap<String, ArrayList<List<String>>> map;
    public static SymbolTable table;
    public static int blockNum = 0;
    public static String error;
    public String scope;

    public SymbolTable()
    {
        map = new LinkedHashMap<>();
        this.scope = "GLOBAL";
    }

    public SymbolTable(String scope)
    {
        if(scope.equals("BLOCK"))
            this.scope = "BLOCK " + (++blockNum);
        else
            this.scope = scope;
    }
    
    public SymbolTable next()
    {
        return this.table;
    }

    public void checkError(String id)
    {
        ArrayList<List<String>> vars = map.get(scope);

        if(vars != null)
            for(List<String> v : vars)
                if (v.get(0).equals(id) && error == null)
                {
                    error = "DECLARATION ERROR " + id;
                    return;
                }
    }

    public void printTable()
    {
        ArrayList<List<String>> vars = map.get(scope);
        System.out.println("Symbol table " + scope);

        if(vars != null)
        {
            for(List<String> v : vars)
            {
                System.out.print(
                    "name " + 
                    v.get(0) + 
                    " type " + 
                    v.get(1) + 
                    (v.size() == 3 ? (" value " + v.get(2)) : "" )+ 
                    "\n");
            }
        }
    }
}