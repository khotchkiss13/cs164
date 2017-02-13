package edu.berkeley.eecs.cs164.pa1;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class RegexParserTest {

    private void test (String pattern, boolean error) {
        System.out.println("Testing pattern: " + pattern);
        if (error) {
            try {
                Assert.assertNull(RegexParser.parse(pattern));
                System.out.println("FAILED TO CORRECTLY PARSE!");
            } catch (RegexParseException e) {
                throw new RegexParseException(e.toString());
            }
        } else {
            try {
                Assert.assertNotNull(RegexParser.parse(pattern));
            } catch (RegexParseException e) {
                System.out.println("FAILED TO CORRECTLY PARSE!");
                throw new RegexParseException(e.toString());
            }
        }
    }

    @Test(expected = RegexParseException.class)
    public void testUnbalancedParentheses() throws Exception {
        System.out.println("Testing unbalanced parenthesis");
        test("a*(b|cd?)+", false);
        test("a(b", true);
    }

    @Test(expected = RegexParseException.class)
    public void testUnbalancedParenthesesEscape() throws Exception {
        System.out.println("Testing unbalanced escaped parenthesis");
        test("a(b)\\)", false);
        test("a(b\\))", false);
        test("a(b\\)", true);
    }

    @Test(expected = RegexParseException.class)
    public void testOneOrMore() throws Exception {
        System.out.println("Testing +");
        test("\\+", false);
        test("+", true);
    }

    @Test(expected = RegexParseException.class)
    public void testZeroOrMore() throws Exception {
        System.out.println("Testing *");
        test("\\*", false);
        test("*", true);
    }

    @Test(expected = RegexParseException.class)
    public void testOptional() throws Exception {
        System.out.println("Testing Optional");
        test("\\?", false);
        test("?", true);
    }

    @Test
    public void testOr() throws Exception {
        System.out.println("Testing Alternation");
        test("\\|", false);
        test("|", false);
    }

    @Test(expected = RegexParseException.class)
    public void testEscape() throws Exception {
        System.out.println("Testing Escape Characters");
        test("\\\\", false);
        test("\\n", false);
        test("\\t", false);
        test("\\)", false);
        test("\\", true);
    }

    @Test(expected = RegexParseException.class)
    public void testGrouping() throws Exception {
        System.out.println("Testing Grouping");
        test("\\??", false);
        test("\\n*", false);
        test("\\*+", false);
        test("(\\+)?", false);
        test("(\\\\)*", false);
        test("(\\))+", false);
        test("\\((+)", true);
    }

    @Test
    public void longPatterns() throws Exception {
        System.out.println("Testing particularly long patterns");
        test("((()@+(\\n()?@+)*)|(b+)@(1h6j9g7z)|((abc123+)+))+", false);
        test("abchidnd((ha)+)*regex(rules|sucks)!?", false);
        test("whatamIdoing?((idk)?)|(Iremember+)", false);
        test("(Iamgoingtoescap+e((\\n)|(\\t)|(\\\\)|(\\+)|(\\|)|(\\))|(\\()|(\\*))+)*", false);
    }

    @Test
    public void epsilon() throws Exception {
        System.out.println("Testing Term -> ε");
        test("|", false);
        test("a|", false);
        test("a|()", false);
        test("|a", false);
        test("()|a", false);
        test("()?|a", false);
        test("()*|a", false);
        test("()+|a", false);
        test("(()+|a)+|a", false);
    }
}
