import org.antlr.v4.runtime.*;

public class Driver
{
    public static void main(String[] args) throws Exception 
    {
    	CharStream iStream = CharStreams.fromStream(System.in); // Read input stream
        LittleLexer lexer = new LittleLexer(iStream); // Lexer
        Vocabulary vocab = lexer.getVocabulary(); // Defined grammar
        Token token = lexer.nextToken(); // Grab the first token

        while (token.getType() != token.EOF)
        {
            System.out.println("Token Type: " + vocab.getSymbolicName(token.getType()));
            System.out.println("Value: " + token.getText());
            token = lexer.nextToken();
        }
        System.out.println(); // The .out files have an extra line at the bottom...
    }
}
