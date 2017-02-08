package edu.berkeley.eecs.cs164.pa1;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

/**
 * This class parses a simple regular expression syntax into an NFA
 */
public class RegexParser {

    /**
     * Grammar: (+ amd * are right-associative)
     * expr    -> term
     *          | expr '|' term             # Alternation
     *
     * term    -> factor
     *          | term factor               # Concatenation
     *
     * factor  -> atom
     *          | atom '+'
     *          | atom '*'
     *          | atom '?'                  # Quantifiers
     *
     * atom    -> '\n'                      # Escapes
     *          | '\t'
     *          | '\|'
     *          | '\('
     *          | '\)'
     *          | '\*'
     *          | '\+'
     *          | '\?'
     *          | '\\'
     *          | '(' expr ')'              # Nested Expressions
     *          | not |, (, ), *, +, ?, \   # Single Characters
     * <p>
     * <p>
     * Equivalent grammar:
     * expr         -> term ('|' term)*
     * term         -> factor+
     * factor       -> atom ('+'|'*'|'?')?
     * atom         -> '(' expr ')' | '\' special | char
     * special      -> 'n' | 't' | operators
     * operators    -> '|', '(', ')', '*', '+', '?', '\'
     * chars        -> !operators
     *
     */

     static private int pos;
     static char[] input;
     static char token;
     static Set<Character> quantifiers;
     static Set<Character> operators;
     static Set<Character> special;

    private RegexParser() {
    }

    private static void initialize(String pattern) {
        pos = 0;
        input = pattern.toCharArray();
        Character[] quant = new Character[] { '*', '+', '?' };
        Character[] ops = new Character[] { '|', '(', ')', '\\' };
        Character[] spec = new Character[] { 'n', 't' };
        quantifiers = new HashSet<Character>(Arrays.asList(quant));
        operators = new HashSet<Character>(Arrays.asList(ops));
        special = new HashSet<Character>(Arrays.asList(spec));
        operators.addAll(quantifiers);
        special.addAll(operators);
    }

    /**
     * This is the main function of this object. It kicks off
     * whatever "compilation" process you write for converting
     * regex strings to NFAs.
     *
     * @param pattern the pattern to compile
     * @return an NFA accepting the pattern
     * @throws RegexParseException upon encountering a parse error
     */
    public static Automaton parse(String pattern) {
        initialize(pattern);
        advance();
        expr();
        if (pos != input.length) {
            throw new RegexParseException("Parsing failed to process entire input string");
        }
        return new Automaton(null, null);
    }

    private static char getToken() {
        return (pos < input.length) ? input[pos++] : 0;
    }

    private static void advance() {
        token = getToken();
        System.out.println("Token: "+token);
    }

    private static void match(char t) {
        if (token == t) {
            advance();
        } else {
            throw new RegexParseException("Unexpected token: " + token + ". Expecting: " + t + ".");
        }
    }

    private static void expr() {
        term();
        while (token == '|' && token != 0) {
            advance();
            term();
        }
    }

    private static void term() {
        factor();
        while (token != '|' && token != 0) {
            factor();
        }
    }

    private static void factor() {
        atom();
        if (quantifiers.contains(token)) {
            advance();
        }
    }

    private static void atom() {
        if (token == '(') {
            advance();
            expr();
            match(')');
        } else if (token == '\\') {
            advance();
            special();
        } else {
            character();
        }
    }

    private static void special() {
        if (special.contains(token)) {
            advance();
        } else {
            throw new RegexParseException("Incorrect Special Character");
        }
    }

    private static void character() {
        if (!operators.contains(token)) {
            advance();
        } else {
            throw new RegexParseException("Invalid Character");
        }
    }
}
