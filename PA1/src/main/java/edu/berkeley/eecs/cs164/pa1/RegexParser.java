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
     static Character[] quant = new Character[] { '*', '+', '?' };
     static Character[] ops = new Character[] { '|', '(', ')', '\\' };
     static Character[] spec = new Character[] { 'n', 't' };
     static Set<Character> quantifiers = new HashSet<Character>(Arrays.asList(quant));
     static Set<Character> operators = new HashSet<Character>(Arrays.asList(ops));
     static Set<Character> special = new HashSet<Character>(Arrays.asList(spec));


    private RegexParser() {
    }

    private static void initialize(String pattern) {
        pos = 0;
        input = pattern.toCharArray();
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
        AutomatonState start = new AutomatonState();
        advance();
        AutomatonState last = expr(start);
        if (pos != input.length) {
            throw new RegexParseException("Parsing failed to process entire input string");
        }
        Automaton result = new Automaton(start, last);
        System.out.println(result.toString());
        return result;
    }

    private static char getToken() {
        return (pos < input.length) ? input[pos++] : 0;
    }

    private static void advance() {
        token = getToken();
    }

    private static void match(char t) {
        if (token == t) {
            advance();
        } else {
            throw new RegexParseException("Unexpected token: " + token + ". Expecting: " + t + ".");
        }
    }

    private static AutomatonState expr(AutomatonState current) {
        // AutomatonState next = new AutomatonState();
        // current.addEpsilonTransition(next);
        AutomatonState exit = term(current);
        AutomatonState last = exit;
        while (token == '|' && token != 0) {
            last = new AutomatonState();
            exit.addEpsilonTransition(last);
            advance();
            // next = new AutomatonState();
            // current.addEpsilonTransition(next);
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
            match(')');
        } else if (token == '\\') {
            advance();
            last = special(current);
        } else {
            last = character(current);
        }
        return last;
    }

    private static AutomatonState special(AutomatonState current) {
        if (special.contains(token)) {
            AutomatonState next = new AutomatonState();
            current.addTransition(token, next);
            advance();
            return next;
        } else {
            throw new RegexParseException("Incorrect Special Character");
        }
    }

    private static AutomatonState character(AutomatonState current) {
        if (!operators.contains(token)) {
            AutomatonState next = new AutomatonState();
            current.addTransition(token, next);
            advance();
            return next;
        } else {
            throw new RegexParseException("Invalid Character");
        }
    }
}
