package jtomahawk.ast;

import jtomahawk.Token;
import jtomahawk.TokenType;

public class AstPrinter implements Expr.Visitor<String> {

    /**
     * Test function to prove that the printer works
     * 
     * @param args
     */
    public static void main(String[] args) {
        // Analagous to "-123 * (45.67 + 8)"
        Expr exampleExpression = new Expr.Binary(
                new Expr.Unary(new Token(TokenType.MINUS, "-", null, 1), new Expr.Literal(123)),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Grouping(
                        new Expr.Binary(new Expr.Literal(45.67),
                                new Token(TokenType.PLUS, "+", null, 1),
                                new Expr.Literal(8))));

        // Should print: "(* (- 123) (group (+ 45.67 8)))"
        System.out.println(new AstPrinter().print(exampleExpression));
    }

    /**
     * Implement top-level of visitor delegation
     * 
     * @param expr
     * @return
     */
    String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) {
            return "nil";
        }

        return expr.value.toString();
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.getLexeme(), expr.left, expr.right);
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.getLexeme(), expr.right);
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }
}
