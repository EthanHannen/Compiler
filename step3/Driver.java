import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import java.io.IOException;

public class Driver
{
    public static void main(String[] args) throws IOException
    {
        LittleParser parser = 
            new LittleParser(
                new CommonTokenStream(
                    new LittleLexer(
                        new ANTLRInputStream(System.in)
                    )
                )
            );

        LittleParser.ProgramContext context = parser.program();
        ParseTreeWalker walker = new ParseTreeWalker();
        SymbolTable symbols = new SymbolTable();
        Listener listener = new Listener(symbols);
        walker.walk(listener, context);

        if(symbols.error != null)
        {
            System.out.println(symbols.error);
        }
        else
        {
            SymbolTable table = symbols;

            while(table != null)
            {
                table.printTable();
                table = table.next();
                System.out.println("\n");
            }
        }
    }
}