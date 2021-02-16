import org.antlr.v4.runtime.*;

public class Driver
{
    public static void main(String[] args) throws Exception 
    {
        CharStream iStream = CharStreams.fromStream(System.in); // Read input stream
        LittleLexer lexer = new LittleLexer(iStream); // Lexer
        CommonTokenStream  tokenStream = new CommonTokenStream(lexer);
        LittleParser littleParser = new LittleParser(tokenStream);
        littleParser.removeErrorListeners();
        littleParser.program();
        if(littleParser.getNumberOfSyntaxErrors() == 0) {
            System.out.println("Accepted");
        } else {
            System.out.println("Not Accepted");
        }
    }
}
