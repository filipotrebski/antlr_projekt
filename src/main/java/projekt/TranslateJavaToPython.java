package projekt;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

public class TranslateJavaToPython {
    public static void main(String[] args) throws IOException {
        CharStream inputStream = CharStreams.fromStream(TranslateJavaToPython.class.getResourceAsStream("/java.txt"));
        J2pLexer lexer = new projekt.J2pLexer(inputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        projekt.J2pParser parser = new projekt.J2pParser(tokens);
        ParseTree kompilacja = parser.compilationUnit();
        TranslateListener listener = new TranslateListener();

        ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
        parseTreeWalker.walk(listener, kompilacja);

        System.out.println(listener.python());

    }
}
