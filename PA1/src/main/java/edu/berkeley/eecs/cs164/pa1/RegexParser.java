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
     * operator     -> '|', '(', ')', '*', '+', '?', '\'
     * character    -> !operators
     *
     */


    /**
      * Global Variables
      *
      * These variables are used throughout the parser to allow access to
      * commonly accessed variables across all functions.
      *
      */
    private static int pos;
    private static char[] input;
    private static char token;
    private static Set<Character> quantifiers = new HashSet<Character>(
        Arrays.asList(new Character[] { '*', '+', '?' })
    );
    private static Set<Character> operators = new HashSet<Character>(
        Arrays.asList(new Character[] { '*', '+', '?', '|', '(', ')', '\\' })
    );
    private static Set<Character> specials = new HashSet<Character>(
        Arrays.asList(new Character[] { '*', '+', '?', '|', '(', ')', '\\', 'n', 't' })
    );

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
        pos = 0;
        input = pattern.toCharArray();
        Automaton nfa = constructNFA();
        if (pos != input.length) parseError(Error.EOS, token);
        return nfa;
    }


    /**
     * NFA Construction
     *
     * The following functions are used to parse the string into the
     * correct tokens as specified in the Equivalent Grammar from above.
     * Each function keeps track of the current state passed to it,
     * calls the appropriate parse function to get the next state, and then
     * connects the current and next states together as specified by the
     * operators in the term.
     *
     * General function pattern:
     * @param current Current Automata state that has been parsed
     * @return The end Automata state of the substring the function is
     *         responsible for
     * @throws RegexParseException upon encountering a parse error
     */
    private static Automaton constructNFA() {
        advance();
        AutomatonState start = new AutomatonState();
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
            closeCaptureGroup();
        } else if (token == '\\') {
            advance();
            last = special(current);
        } else {
            last = character(current);
        }
        return last;
    }

    private static AutomatonState special(AutomatonState current) {
        if (!specials.contains(token)) { parseError(Error.INV_SPEC, token); }
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


    /**
     * Token Manipulation
     *
     * The following functions are used to incremement the token
     *
     */

    private enum Error {
        EOS, INV_CHAR, INV_SPEC, UNEXPECTED_TOK
    }

    private static char getToken() {
        return (pos < input.length) ? input[pos++] : 0;
    }

    private static void advance() {
        token = getToken();
    }

    private static void closeCaptureGroup() {
         if (token == ')') advance(); else parseError(Error.UNEXPECTED_TOK, token);
    }

    /**
     * parseHelper
     *
     * Helper function that will throw the RegexParseException with the correct
     * message, detailing exactly what happened.
     *
     */
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
