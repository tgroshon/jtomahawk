package jtomahawk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jtomahawk.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));

        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            // 1-char tokens
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '*':
                addToken(STAR);
                break;

            // 1-2 char tokens
            case '!':
                addToken(advanceOnMatch('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(advanceOnMatch('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(advanceOnMatch('=') ? LESSER_EQUAL : LESSER);
                break;
            case '>':
                addToken(advanceOnMatch('=') ? GREATER_EQUAL : GREATER);
                break;

            // special cases
            case '/':
                // double slash means a comment
                if (advanceOnMatch('/')) {
                    consumeRestOfLine();
                } else {
                    addToken(SLASH);
                }
                break;

            // error catcher
            default:
                App.error(line, "Unexpected character: " + c);
                break;
        }
    }

    /**
     * Consume chars to the end of the line
     */
    private void consumeRestOfLine() {
        while (peek() != '\n' && !isAtEnd()) {
            advance();
        }
    }

    /**
     * Lookahead to next char, but DO NOT consume it (i.e no advancing current
     * pointer)
     * 
     * @return
     */
    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }

        return source.charAt(current);
    }

    /**
     * Read character at current point, then advance current pointer
     * 
     * @return
     */
    private char advance() {
        return source.charAt(current++);
    }

    /**
     * If the current char matches the expected, advance current pointer
     * 
     * @param expected
     * @return
     */
    private boolean advanceOnMatch(char expected) {
        if (isAtEnd()) {
            return false;
        }

        if (source.charAt(current) != expected) {
            return false;
        }

        current++; // yes match, so advance counter
        return true;
    }

    /**
     * Add a token with no corresponding literal
     * 
     * @param type
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * Add a token with a corresponding literal (e.g. variable name or primitive
     * value)
     * 
     * @param type
     * @param literal
     */
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
