import java.lang.*;
import java.util.*;

public class Listener extends LittleBaseListener
{
    Stack<String> stack = new Stack<>();
    public SymbolTable symbols;
    public int blockCount = 1;

    public Listener(SymbolTable symbols)
    {
        this.symbols = symbols;
    }
    
    private void addItem (String... s)
    {
        symbols.checkError(s[0]);
        ArrayList<String> line = new ArrayList<>();
        line.add(s[0]);
        line.add(s[1]);
        if (s.length > 2) line.add(s[2]);
        
        ArrayList<List<String>> block = SymbolTable.map.get(symbols.scope);
        block = block != null ? block : new ArrayList<>();
        block.add(line);
        SymbolTable.map.put(symbols.scope, block);
    }
    
    private void nextTable(String s)
    {
        symbols.table = new SymbolTable(s);
        symbols = symbols.table;
    }

    @Override
    public void enterVar_decl(LittleParser.Var_declContext ctx)
    {
        String vars = ctx.getText().split("INT|FLOAT")[1];
        String type = ctx.getText().split(vars)[0];
        vars = vars.split(";")[0];
        String[] ids = vars.split(",");

        for (String id : ids)
            addItem(id, type);
    }

    @Override
    public void enterString_decl(LittleParser.String_declContext ctx)
    {
        String text = ctx.getText();
        String [] vars = text.split(":=");
        String val = vars[1].split(";")[0];
        String id = vars[0].split("STRING")[1];
        addItem(id, "STRING", val);
    }
    
    @Override
    public void enterParam_decl_list(LittleParser.Param_decl_listContext ctx)
    {
        String text = ctx.getText();
        if(text.compareTo("") != 0)
        {
            String [] vars = text.split(",");
            for (String v : vars)
            {
                String name = v.split("INT|FLOAT")[1];
                String type = v.split(name)[0];
                addItem(name, type);
            }
        }
    }

    @Override
    public void enterFunc_decl(LittleParser.Func_declContext ctx)
    {
        if(ctx.getText().compareTo("END") != 0)
        {
            String text = ctx.getText().split("BEGIN")[0];
            text = text.split("INT|FLOAT|VOID|STRING")[1];
            text = text.split("\\(")[0];
            nextTable(text);
        }
    }

    @Override
    public void enterIf_stmt(LittleParser.If_stmtContext ctx)
    {
        String text = ctx.getText();

        if(text.substring(0,2).equals("IF"))
        {
            stack.push("\nBlock " + blockCount++);
            nextTable("BLOCK");
        }
    }

    @Override
    public void enterElse_part(LittleParser.Else_partContext ctx)
    {
        if(ctx.getText().compareTo("") != 0 && 
           ctx.getText().compareTo("ENDIF") != 0)
        {
            stack.push("\nBlock "+ blockCount++);
            nextTable("BLOCK");
        }
    }

    @Override
    public void enterWhile_stmt(LittleParser.While_stmtContext ctx)
    {
        stack.push("\nBlock " + blockCount++);
        nextTable("BLOCK");
    }
}
