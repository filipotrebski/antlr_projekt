package projekt;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TranslateListener extends J2pBaseListener {

    int intend = 0;
    StringBuilder sb = new StringBuilder("#!/usr/bin/python3\n");
    String mainCall = "\nif __name__ == '__main__':\n" +
            "    m = MyClass()\n" +
            "    m.main(sys.argv)\n";

    private Set<String> fields = new HashSet<>();


    @Override
    public void exitPkg(J2pParser.PkgContext ctx) {
        addLine("#package " + ctx.qualifiedName().getText());

    }

    @Override
    public void exitImports(J2pParser.ImportsContext ctx) {
        addLine("#import " + ctx.qualifiedName().getText());
        addLine("import sys");
    }

    @Override
    public void enterClass_def(J2pParser.Class_defContext ctx) {
        addLine("class " + ctx.Identifier() + ":");
        addEmptyLine();
        intend++;
    }

    @Override
    public void exitClass_def(J2pParser.Class_defContext ctx) {
        intend--;
    }

    @Override
    public void exitFieldDef(J2pParser.FieldDefContext ctx) {
        addLine(ctx.variable().getText() + " = " + ctx.literal().getText());
        fields.add(ctx.variable().getText());
    }

    @Override
    public void enterMethod(J2pParser.MethodContext ctx) {
        List<J2pParser.ArgumentDefContext> args = ctx.argumentsDefList().argumentDef();
        String arguments = args
                .stream()
                .map(a -> a.Identifier().getText())
                .collect(Collectors.joining(", "));
        if (arguments.length() > 0) {
            arguments = ", " + arguments;
        }
        addLine("def " + ctx.methodName().getText() + "(self" + arguments + "):");
        intend++;
    }

    @Override
    public void exitMethod(J2pParser.MethodContext ctx) {
        addEmptyLine();
        addEmptyLine();
        intend--;
    }

    @Override
    public void enterMethodCall(J2pParser.MethodCallContext ctx) {
        String method = ctx.qualifiedName().getText();
        if (method.equals("System.out.println")) {
            method = "print";
        } else {
            method = "self." + method;
        }

        List<J2pParser.ArgumentContext> arguments = ctx.argumentsList().argument();
        String args = arguments
                .stream()
                .map(a -> {
                    if (a.literal() != null) {
                        return a.literal().getText();
                    } else {
                        String variable = a.variable().getText();
                        return (fields.contains(variable) ? "self." : "") + variable;
                    }
                })
                .collect(Collectors.joining(", "));

        addLine(method + "(" + args + ")");
    }


    @Override
    public void exitVariableDef(J2pParser.VariableDefContext ctx) {
        super.exitVariableDef(ctx);
        addLine(ctx.variable().getText() + " = " + ctx.literal().getText());

    }

    private void addEmptyLine() {
        sb.append("\n");
    }

    private void addLine(String text) {
        intend(sb, intend);
        sb.append(text);
        sb.append("\n");
    }


    private void intend(StringBuilder sb, int intend) {
        for (int i = 0; i < intend; i++) {
            sb.append("  ");
        }
    }


    public String python() {
        return sb.toString() + "\n\n" + mainCall;
    }
}
