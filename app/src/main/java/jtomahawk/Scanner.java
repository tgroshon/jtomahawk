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

    /**
     * Advance through source string; char-by-char, token-by-token; to build up the
     * tokens list until EOF reached.
     * 
     * @return
     */
    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));

        return tokens;
    }

    /**
     * Advance through source string by one full token, incrementing current pointer
     * as values are consumed, and adding to the token list.
     */
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

            // distinguish between division and comment
            case '/':
                // double slash means a comment
                if (advanceOnMatch('/')) {
                    consumeRestOfLine();
                } else {
                    addToken(SLASH);
                }
                break;

            // whitespace, ignore
            case ' ':
            case '\r':
            case '\t':
                break;

            // newline, advance line counter
            case '\n':
                line++;
                break;

            case '"':
                consumeStringLiteral();
                break;

            // error catcher
            default:
                App.error(line, "Unexpected character: " + c);
                break;
        }
    }

    private void consumeStringLiteral() {
        while (peek() != '"' && !isAtEnd()) {
            // support multi-line strings :)
            if (peek() == '\n') {
                line++;
            }
            advance();
        }

        if (isAtEnd()) {
            App.error(line, "Unterminated string.");
            return;
        }

        advance(); // consume the closing " char.

        // Trim the surrounding quotes
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    /**
     * Is current pointer at the end of the source?
     * 
     * @return
     */
    private boolean isAtEnd() {
        return current >= source.length();
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
     * Add a token with it's corresponding literal (e.g. variable name or primitive
     * value) based on position of the start pointer (where last token left off;
     * ignoring whitespace) and
     * current pointer (where current token ends).
     * 
     * @param type
     * @param literal
     */
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
