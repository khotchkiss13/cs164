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
     *
     * expr         -> term ('|' term)*
     * term         -> factor+
     * factor       -> atom ('+'|'*'|'?')?
     * atom         -> '(' expr ')' | '\' special | char
     * special      -> 'n' | 't' | operators
     * operators    -> '|', '(', ')', '*', '+', '?', '\'
     * chars        -> !operators
     *
     */

     private static int pos;
     private static char[] input;
     private static char token;
     private static Character[] quantify = new Character[] { '*', '+', '?' };
     private static Character[] ops = new Character[] { '|', '(', ')', '\\' };
     private static Character[] spec = new Character[] { 'n', 't' };
     private static Set<Character> quantifiers = new HashSet<Character>(Arrays.asList(quantify));
     private static Set<Character> operators = new HashSet<Character>(Arrays.asList(ops));
     private static Set<Character> special = new HashSet<Character>(Arrays.asList(spec));

     private enum Error {
        EOS, INV_CHAR, INV_SPEC, UNEXPECTED_TOK
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
    static Automaton parse(String pattern) {
        initialize(pattern);
        Automaton nfa = constructNFA();
        if (pos != input.length) parseError(Error.EOS, token);
        return nfa;
    }

    private static void initialize(String pattern) {
        pos = 0;
        input = pattern.toCharArray();
        operators.addAll(quantifiers);
        special.addAll(operators);
    }

    private static Automaton constructNFA() {
        AutomatonState start = new AutomatonState();
        advance();
        AutomatonState last = expr(start);
        return new Automaton(start, last);
    }

    private static AutomatonState expr(AutomatonState current) {
        AutomatonState exit = term(current);
        AutomatonState last = exit;
        while (token == '|' && token != 0) {
            last = new AutomatonState();
            exit.addEpsilonTransition(last);
            advance();
            exit = term(current);
            exit.addEpsilonTransition(last);
        }
        return last;
    }

    private static AutomatonState term(AutomatonState current) {
        AutomatonState last = factor(current);
        while (token != '|' && token != 0 && token != ')') {
            last = factor(last);
        }
        return last;
    }

    private static AutomatonState factor(AutomatonState current) {
        AutomatonState last = atom(current);
        if (quantifiers.contains(token)) {
            if (token == '+') {
                last.addEpsilonTransition(current);
            } else if (token == '*') {
                last.addEpsilonTransition(current);
                current.addEpsilonTransition(last);
            } else if (token == '?') {
                current.addEpsilonTransition(last);
            }
            advance();
        }
        return last;
    }

    private static AutomatonState atom(AutomatonState current) {
        AutomatonState last;
        if (token == '(') {
            advance();
            last = expr(current);
            closeParenthesis();
        } else if (token == '\\') {
            advance();
            last = special(current);
        } else {
            last = character(current);
        }
        return last;
    }

    private static AutomatonState special(AutomatonState current) {
        if (!special.contains(token)) { parseError(Error.INV_SPEC, token); }
        if (token == 'n') {
            token = '\n';
        } else if (token == 't') {
            token = '\t';
        }
        AutomatonState next = new AutomatonState();
        current.addTransition(token, next);
        advance();
        return next;
    }

    private static AutomatonState character(AutomatonState current) {
        if (operators.contains(token)) { parseError(Error.INV_CHAR, token); }
        AutomatonState next = new AutomatonState();
        current.addTransition(token, next);
        advance();
        return next;
    }

    private static char getToken() {
        return (pos < input.length) ? input[pos++] : 0;
    }

    private static void advance() {
        token = getToken();
    }

    private static void closeParenthesis() {
         if (token == ')') advance(); else parseError(Error.UNEXPECTED_TOK, token);
    }

    private static void parseError(Error errorCode, Character tok) {
        String message;
        switch (errorCode) {
            case EOS:
                message = "Could not complete Parsing: " + tok;
                break;
            case INV_CHAR:
                message = "Invalid character: " + tok;
                break;
            case INV_SPEC:
                message = "Invalid escaped character: " + tok;
                break;
            case UNEXPECTED_TOK:
                message = "Unexpected Token: " + tok + ". Expected: )";
                break;
            default:
                message = "Parsing error";
        }
        throw new RegexParseException(message);
    }
}
