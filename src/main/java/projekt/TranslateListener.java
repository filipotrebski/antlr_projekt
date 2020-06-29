package projekt;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TranslateListener extends J2pBaseListener {
    String pkg = "";
    String clazz = "";
    List<String> imports = new ArrayList<>();
    List<Method> methods = new ArrayList<>();
    List<Expression> currentMethod = new ArrayList<>();
    private boolean inMethod = false;

    @Override
    public void exitPkg(projekt.J2pParser.PkgContext ctx) {
        super.exitPkg(ctx);
        pkg = ctx.qualifiedName().getText();
    }

    @Override
    public void exitImports(projekt.J2pParser.ImportsContext ctx) {
        super.exitImports(ctx);
        ctx.qualifiedName().getText();
        imports.add(ctx.qualifiedName().getText());
    }


    @Override
    public void enterMethod(projekt.J2pParser.MethodContext ctx) {
        super.enterMethod(ctx);
        currentMethod = new ArrayList<>();
        inMethod = true;

    }

    @Override
    public void exitMethod(projekt.J2pParser.MethodContext ctx) {
        super.exitMethod(ctx);
        String name = ctx.methodName().getText();
        String returnType = ctx.returnType().getText();

        methods.add(new Method(name, returnType, new ArrayList<>(currentMethod)));
        inMethod = false;
    }

    @Override
    public void exitMethodCall(projekt.J2pParser.MethodCallContext ctx) {
        super.exitMethodCall(ctx);
        projekt.J2pParser.QualifiedNameContext name = ctx.qualifiedName();
        projekt.J2pParser.ArgumentsListContext argumentsListContext = ctx.argumentsList();
        List<Argument> arguments = argumentsListContext.argument().stream().map(argCtx -> {
            TerminalNode stringLiteral = argCtx.literal().StringLiteral();
            return new StringLiteral(stringLiteral.getText());
        }).collect(Collectors.toList());
        currentMethod.add(new MethodCall(name.getText(), arguments));
    }


    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        super.exitEveryRule(ctx);
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        super.enterEveryRule(ctx);
    }

    @Override
    public void exitClass_def(projekt.J2pParser.Class_defContext ctx) {
        super.exitClass_def(ctx);
        clazz = ctx.Identifier().getText();
    }

    public String python() {
        String NL = "\n";
        StringBuilder sb = new StringBuilder();
        sb.append("#!/usr/bin/python3\n\n");

        sb.append("#package ").append(pkg).append(NL);
        imports.forEach(i -> sb.append("#import ").append(i).append(NL));
        sb.append("class ").append(clazz).append(": ").append(NL);
        int intend = 1;
        methods.forEach(m -> {
            intend(sb, intend);
            sb
                    .append("def ")
                    .append(m.name)
                    .append("(self):\n");
            for (Expression call : m.calls) {
                intend(sb, intend + 1);
                if (call instanceof MethodCall) {
                    MethodCall methodCall = (MethodCall) call;
                    if (methodCall.objectAndMethod.equals("System.out.println")) {
                        sb.append("print");
                        sb.append("(");
                        List<Argument> arguments = methodCall.arguments;
                        for (Argument argument : arguments) {
                            if (argument instanceof StringLiteral) {
                                StringLiteral stringLiteral = (StringLiteral) argument;
                                sb.append(stringLiteral.value);
                            }
                        }
                        sb.append(")");
                    }
                }
                sb.append("\n");
            }
            sb.append("\n\n");

        });

        //
        sb.append("\nif __name__ == '__main__':\n" +
                "    m = MyClass()\n" +
                "    m.main()\n");

        return sb.toString();
    }

    private void intend(StringBuilder sb, int intend) {
        for (int i = 0; i < intend; i++) {
            sb.append("  ");
        }
    }

    public static class Method {
        final String name;
        final String returnType;
        final List<Expression> calls;

        public Method(String name, String returnType, List<Expression> calls) {
            this.name = name;
            this.returnType = returnType;
            this.calls = calls;
        }
    }

    public interface Expression {
    }

    public static class VariableDeclaration implements Expression {
        final String type;
        final String name;
        final String value;

        public VariableDeclaration(String type, String name, String value) {
            this.type = type;
            this.name = name;
            this.value = value;
        }
    }

    public static class MethodCall implements Expression {
        final String objectAndMethod;
        final List<Argument> arguments;

        public MethodCall(String objectAndMethod, List<Argument> arguments) {
            this.objectAndMethod = objectAndMethod;
            this.arguments = arguments;
        }
    }

    public static abstract class Argument {
    }

    public static class StringLiteral extends Argument {
        final String value;

        public StringLiteral(String value) {
            this.value = value;
        }
    }
}
