import java.lang.*;
import java.util.*;

public class Listener extends LittleBaseListener
{
    Stack<String> current = new Stack<>();
    public SymbolTable table;
    public int blockCount = 1;

    public Listener(SymbolTable table)
    {
        this.table = table;
    }

    private void addItem (String... s)
    {
        table.checkDuplicate(s[0]);
        ArrayList<String> line = new ArrayList<>();
        line.add(s[0]);
        line.add(s[1]);
        if (s.length > 2) line.add(s[2]);
        
        ArrayList<List<String>> block = SymbolTable.getScope(table.scope);
        block = block != null ? block : new ArrayList<>();
        block.add(line);
        SymbolTable.addToScope(table.scope, block);
    }
    
    private void nextTable(String s)
    {
        table.next = new SymbolTable(s);
        table = table.next;
    }
    
    private void pushBlock()
    {
        current.push(String.format("\nBlock %d", blockCount++));
        nextTable("BLOCK");
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
        String t = ctx.getText();
        if(!equals(t, ""))
        {
            String [] vars = t.split(",");
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
            pushBlock();
    }

    @Override
    public void enterElse_part(LittleParser.Else_partContext ctx)
    {
        String t = ctx.getText();
        if(!equals(t, "") && !equals(t, "ENDIF"))
           pushBlock();
    }

    @Override
    public void enterWhile_stmt(LittleParser.While_stmtContext ctx)
    {
        pushBlock();
    }
}
