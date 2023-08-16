package jtomahawk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        // TODO: perform logic of scanning source for tokens and populating token list

        return tokens;
    }
}
