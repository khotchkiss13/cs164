package edu.berkeley.eecs.cs164.pa1;

/**
 * A custom exception type to throw when an error in a regular expression is detected
 */
public class RegexParseException extends RuntimeException {
    public RegexParseException(String message) {
        super(message);
    }
}
