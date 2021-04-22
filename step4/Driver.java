/***********************************************
* Project Step 4 | Driver.java
* Team 8
* Ethan Hannen, Michael Skipper, Renato Xhaferi
************************************************/

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import java.io.IOException;

public class Driver
{
    @SuppressWarnings( "deprecation" )
    public static void main(String[] args) throws IOException
    {
        SymbolTable table = new SymbolTable("GLOBAL");
		Generator.init();
		
        LittleParser.ProgramContext context = 
            new LittleParser(
                new CommonTokenStream(
                    new LittleLexer(
                        new ANTLRInputStream(System.in)
                    )
                )
            ).program();

        (new ParseTreeWalker()).walk(new Listener(table), context);

        if (table.error != null)
        {
            System.out.println("DECLARATION ERROR " + table.error);
        }
		else
		{
			System.out.println(Generator.produce());
		}
    }
}
