package edu.berkeley.eecs.cs164.pa1;

import junit.framework.Assert;
import org.junit.Test;

public class NFASimulatorTest {
    private static void testCase(String regex, String text) {
        testCase(regex, text, true);
    }

    private static void testCase(String regex, String text, boolean isMatch) {
        Automaton nfa = RegexParser.parse(regex);
        // if (regex.length() + text.length() < 500) {
        String not = isMatch ? "" : "not ";
        System.out.printf("'%s' should %smatch regex '%s'%n", text, not, regex);
        // }

        Assert.assertEquals(isMatch, new NFASimulator(nfa).matches(text));
    }

    @Test
    public void testSingleAtom() throws Exception {
        System.out.println("testSingleAtom 3");
        testCase("a", "a");
        testCase("a", "b", false);
        testCase("b", "b");
    }

    @Test
    public void testZeroOrMore() throws Exception {
        System.out.println("testZeroOrMore 4");
        testCase("a*", "");
        testCase("a*", "a");
        testCase("a*", "aaaaaa");
        testCase("a*", "aaaabaaa", false);
    }

    @Test
    public void testOneOrMore() throws Exception {
        System.out.println("testOneOrMore 4");
        testCase("a+", "", false);
        testCase("a+", "a");
        testCase("a+", "aaaaaaaaa");
        testCase("a+", "aaaabaaa", false);
    }

    @Test
    public void testZeroOrOne() throws Exception {
        System.out.println("testZeroOrOne 4");
        testCase("a?", "");
        testCase("a?", "a");
        testCase("a?", "aa", false);
        testCase("a?", "ba", false);
    }

    @Test
    public void testEscapeAtom() throws Exception {
        System.out.println("testEscapeAtom 10");
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
        System.out.println("testConcatenation 5");
        testCase("abc", "abc");
        testCase("abc", "acb", false);
        testCase("a\\nb", "a\nb");
        testCase("abcdef", "abcde", false);
        testCase("abcdef", "abcdefg", false);
    }

    @Test
    public void testNestedExpressions() throws Exception {
        System.out.println("testNestedExpressions 12");
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
            System.out.println("testWorstCasePerformance 3");
            testCase(regex, input);
            testCase(regex, input + input);
            testCase(regex, input + input + "a", false);
        }
    }

}
