package projekt;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

public class TranslateJavaToPython {
    public static void main(String[] args) throws IOException {
        CharStream inputStream;
        if (args.length == 1) {
            inputStream = CharStreams.fromFileName(args[0]);
        } else {
            inputStream = CharStreams.fromStream(TranslateJavaToPython.class.getResourceAsStream("/java.txt"));
        }

        J2pLexer lexer = new J2pLexer(inputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        J2pParser parser = new J2pParser(tokens);
        ParseTree kompilacja = parser.compilationUnit();
        TranslateListener listener = new TranslateListener();

        ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
        parseTreeWalker.walk(listener, kompilacja);

        System.out.println(listener.python());

    }
}
