package edu.berkeley.eecs.cs164.pa1;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class RegexParserTest {

    @Test(expected = RegexParseException.class)
    public void testUnbalancedParentheses() throws Exception {
        Assert.assertNotNull(RegexParser.parse("a*(b|cd?)+"));
        Assert.assertNull(RegexParser.parse("a(b"));
    }

    @Test(expected = RegexParseException.class)
    public void testUnbalancedParenthesesEscape() throws Exception {
        // These should be OK
        Assert.assertNotNull(RegexParser.parse("a(b)\\)"));
        Assert.assertNotNull(RegexParser.parse("a(b\\))"));
        // This should throw.
        Assert.assertNull(RegexParser.parse("a(b\\)"));
    }

    @Test(expected = RegexParseException.class)
    public void testOneOrMore() throws Exception {
        // These should be OK
        Assert.assertNotNull(RegexParser.parse("\\+"));
        // This should throw.
        Assert.assertNull(RegexParser.parse("+"));
    }

    @Test(expected = RegexParseException.class)
    public void testZeroOrMore() throws Exception {
        // These should be OK
        Assert.assertNotNull(RegexParser.parse("\\*"));
        // This should throw.
        Assert.assertNull(RegexParser.parse("*"));
    }

    @Test(expected = RegexParseException.class)
    public void testOptional() throws Exception {
        // These should be OK
        Assert.assertNotNull(RegexParser.parse("\\?"));
        // This should throw.
        Assert.assertNull(RegexParser.parse("?"));
    }

    @Test(expected = RegexParseException.class)
    public void testOr() throws Exception {
        // These should be OK
        Assert.assertNotNull(RegexParser.parse("\\|"));
        // This should throw.
        Assert.assertNull(RegexParser.parse("|"));
    }

    @Test(expected = RegexParseException.class)
    public void testEscape() throws Exception {
        // These should be OK
        Assert.assertNotNull(RegexParser.parse("\\\\"));
        Assert.assertNotNull(RegexParser.parse("\\n"));
        Assert.assertNotNull(RegexParser.parse("\\t"));
        Assert.assertNotNull(RegexParser.parse("\\("));
        Assert.assertNotNull(RegexParser.parse("\\)"));
        // This should throw.
        Assert.assertNull(RegexParser.parse("\\"));
    }

}
