package edu.berkeley.eecs.cs164.pa1;

import junit.framework.Assert;
import org.junit.Test;

public class NFASimulatorTest {
    private static void testCase(String regex, String text) {
        testCase(regex, text, true);
    }

    private static void testCase(String regex, String text, boolean isMatch) {
        Automaton nfa = RegexParser.parse(regex);

        if (regex.length() + text.length() < 100) {
            String not = isMatch ? "" : "not ";
            System.out.printf("'%s' should %smatch regex '%s'%n", text, not, regex);
        }

        Assert.assertEquals(isMatch, new NFASimulator(nfa).matches(text));
    }

    @Test
    public void testSingleAtom() throws Exception {
        testCase("a", "a");
        testCase("a", "b", false);
        testCase("b", "b");
    }

    @Test
    public void testZeroOrMore() throws Exception {
        testCase("a*", "");
        testCase("a*", "a");
        testCase("a*", "aaaaaa");
        testCase("a*", "aaaabaaa", false);
    }

    @Test
    public void testOneOrMore() throws Exception {
        testCase("a+", "", false);
        testCase("a+", "a");
        testCase("a+", "aaaaaaaaa");
        testCase("a+", "aaaabaaa", false);
    }

    @Test
    public void testZeroOrOne() throws Exception {
        testCase("a?", "");
        testCase("a?", "a");
        testCase("a?", "aa", false);
        testCase("a?", "ba", false);
    }

    @Test
    public void testEscapeAtom() throws Exception {
        testCase("\\n", "\n");
        testCase("\\n", "n", false);
        testCase("\\t", "\t");
        testCase("\\t", "t", false);
        testCase("\\(", "(");
        testCase("\\)", ")");
        testCase("\\*", "*");
        testCase("\\+", "+");
        testCase("\\?", "?");
        testCase("\\\\", "\\");
    }

    @Test
    public void testConcatenation() throws Exception {
        testCase("abc", "abc");
        testCase("abc", "acb", false);
        testCase("a\\nb", "a\nb");
        testCase("abcdef", "abcde", false);
        testCase("abcdef", "abcdefg", false);
    }

    @Test
    public void testNestedExpressions() throws Exception {
        testCase("ab(cd)e", "abcde");
        // with star
        testCase("a(bc)*d", "ad");
        testCase("a(bc)*d", "abcd");
        testCase("a(bc)*d", "abcbcbcd");
        testCase("a(bc)*d", "abcbcbd", false);
        // with plus
        testCase("a(bc)+d", "ad", false);
        testCase("a(bc)+d", "abcd");
        testCase("a(bc)+d", "abcbcbcd");
        testCase("a(bc)+d", "abcbcbd", false);
        // with optional
        testCase("a(bc)?d", "ad");
        testCase("a(bc)?d", "abcd");
        testCase("a(bc)?d", "abcbcd", false);
    }

    @Test
    public void testWorstCasePerformance() throws Exception {
        String regex = "";
        String input = "";
        for (int n = 10; n <= 100; n += 10) {
            regex = "a?a?a?a?a?a?a?a?a?a?" + regex + "aaaaaaaaaa";
            input += "aaaaaaaaaa";
            testCase(regex, input);
            testCase(regex, input + input);
            testCase(regex, input + input + "a", false);
        }
    }

}