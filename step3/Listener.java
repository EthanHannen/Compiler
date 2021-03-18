import java.lang.*;
import java.util.*;

public class Listener extends LittleBaseListener
{
    public SymbolTable table;  // Symbol Table reference

    public Listener(SymbolTable table)
    {
        this.table = table;
    }

    private void addVar (String... s)
    {
        table.checkDuplicate(s[0]); // Check for declaration errors
        ArrayList<String> var = new ArrayList<>();
        var.add(s[0]); // Name
        var.add(s[1]); // Type or STRING
        if (s.length > 2) var.add(s[2]); // Value of STRING
        
        ArrayList<List<String>> block = SymbolTable.getScope(table.scope);
        block = block != null ? block : new ArrayList<>(); // Get block if exists
        block.add(var);
        SymbolTable.addToScope(table.scope, block);
    }

    private void nextTable(String s)
    {
        table.next = new SymbolTable(s);
        table = table.next;
    }

    private boolean equals(String t1, String t2)
    {
        return t1.compareTo(t2) == 0;
    }

    @Override
    public void enterVar_decl(LittleParser.Var_declContext ctx)
    {
        String vars = ctx.getText().split("INT|FLOAT")[1];
        String type = ctx.getText().split(vars)[0];
        vars = vars.split(";")[0];
        String[] names = vars.split(",");

        for (String name : names)
            addVar(name, type);
    }

    @Override
    public void enterString_decl(LittleParser.String_declContext ctx)
    {
        String text = ctx.getText();
        String [] strings = text.split(":=");
        String value = strings[1].split(";")[0];
        String id = strings[0].split("STRING")[1];
        addVar(id, "STRING", value);
    }
    
    @Override
    public void enterParam_decl_list(LittleParser.Param_decl_listContext ctx)
    {
        String t = ctx.getText();
        if(!equals(t, ""))
        {
            // Break param list into individual parts
            String [] vars = t.split(",");
            for (String v : vars)
            {
                String name = v.split("INT|FLOAT")[1];
                String type = v.split(name)[0];
                addVar(name, type);
            }
        }
    }

    @Override
    public void enterFunc_decl(LittleParser.Func_declContext ctx)
    {
        String t = ctx.getText();
        if(!equals(t, "END"))
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
        String t = ctx.getText().substring(0,2);
        if(equals(t, "IF"))
            nextTable("BLOCK");
    }

    @Override
    public void enterElse_part(LittleParser.Else_partContext ctx)
    {
        String t = ctx.getText();
        if(!equals(t, "") && !equals(t, "ENDIF"))
           nextTable("BLOCK");
    }

    @Override
    public void enterWhile_stmt(LittleParser.While_stmtContext ctx)
    {
        nextTable("BLOCK");
    }
}
