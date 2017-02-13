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
    public void testAlternation() throws Exception {
        testCase("a|b", "b");
        testCase("a|b", "b");
        testCase("aa|b", "aa");
        testCase("aa|b", "b");
        testCase("aa|b", "aab", false);
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
        testCase("a(bc)*d", "ad");
        testCase("a(bc)*d", "abcd");
        testCase("a(bc)*d", "abcbcbcd");
        testCase("a(bc)*d", "abcbcbd", false);
        testCase("a(bc)+d", "ad", false);
        testCase("a(bc)+d", "abcd");
        testCase("a(bc)+d", "abcbcbcd");
        testCase("a(bc)+d", "abcbcbd", false);
        testCase("a(bc)?d", "ad");
        testCase("a(bc)?d", "abcd");
        testCase("a(bc)?d", "abcbcd", false);
    }

    @Test
    public void testEmptyExpressions() throws Exception {
        testCase("|", "");
        testCase("a|", "a");
        testCase("a|", "");
        testCase("a|", "b", false);
        testCase("|a", "a");
        testCase("|a", "");
        testCase("|a", "b", false);
        testCase("a|()", "a");
        testCase("a|()", "");
        testCase("a|()", "b", false);
        testCase("a|()?", "a");
        testCase("a|()?", "");
        testCase("a|()?", "b", false);
        testCase("a|()+", "a");
        testCase("a|()+", "");
        testCase("a|()+", "b", false);
        testCase("a|()*", "a");
        testCase("a|()*", "");
        testCase("a|()*", "b", false);
        testCase("(()+|a)+|a", "a");
        testCase("(()+|a)+|a", "aa");
        testCase("(()+|a)+|a", "aaaaaa");
        testCase("(()+|a)+|a", "");
        testCase("(()+|a)+|a", "b", false);
    }

    @Test
    public void testLongPatterns() throws Exception {
        testCase("((@+(\\n@+)*)|(b+)@(1h6j9g7z)|((abc123+)+))+", "@\n@");
        testCase("((@+(\\n()?@+)*)|(b+)@(1h6j9g7z)|((abc123+)+))+", "b@1h6j9g7zabc1233abc1233333");
        testCase("((@+(\\n()?@+)*)|(b+)@(1h6j9g7z)|((abc123+)+))+", "bbbbbbbb@1h6j9g7zb@1h6j9g7z");
        testCase("((@+(\\n()?@+)*)|(b+)@(1h6j9g7z)|((abc123+)+))+", "@@@@@@@@@@\n@@@@bb@1h6j9g7zabc1233abc1233333");
        testCase("((@+(\\n()?@+)*)|(b+)@(1h6j9g7z)|((abc123+)+))+", "", false);
        testCase("((@+(\\n()?@+)*)|(b+)@(1h6j9g7z)|((abc123+)+))+", "\n@@@@", false);
        testCase("((@+(\\n()?@+)*)|(b+)@(1h6j9g7z)|((abc123+)+))+", "'b@1h6j9g7zabc12abc1233333", false);
        testCase("((@+(\\n()?@+)*)|(b+)@(1h6j9g7z)|((abc123+)+))+", "abc1233333");

        testCase("abchidnd((ha)+)*regex((rules)|(sucks))!?", "abchidndharegexrules!");
        testCase("abchidnd((ha)+)*regex((rules)|(sucks))!?", "abchidndhahahahahaharegexrules");
        testCase("abchidnd((ha)+)*regex((rules)|(sucks))!?", "abchidndregexrules");
        testCase("abchidnd((ha)+)*regex((rules)|(sucks))!?", "abchidndhahahaharegexrules!");
        testCase("abchidnd((ha)+)*regex((rules)|(sucks))!?", "abchidndhahahahahregexrules!", false);
        testCase("abchidnd((ha)+)*regex((rules)|(sucks))!?", "abchidndhahahaharegexrules!!", false);
        testCase("abchidnd((ha)+)*regex((rules)|(sucks))!?", "abchidndharegex!", false);
        testCase("abchidnd((ha)+)*regex((rules)|(sucks))!?", "", false);

        testCase("whatamIdoing?(((idk)?)|(Iremember+))", "whatamIdoin");
        testCase("whatamIdoing?(((idk)?)|(Iremember+))", "whatamIdoinidk");
        testCase("whatamIdoing?(((idk)?)|(Iremember+))", "whatamIdoingIrememberrrrr");
        testCase("whatamIdoing?(((idk)?)|(Iremember+))", "", false);
        testCase("whatamIdoing?(((idk)?)|(Iremember+))", "whatamIdoingg", false);
        testCase("whatamIdoing?(((idk)?)|(Iremember+))", "whatamIdoing?", false);
        testCase("whatamIdoing?(((idk)?)|(Iremember+))", "whatamIdoingidkIremember", false);
        testCase("whatamIdoing?(((idk)?)|(Iremember+))", "whatamIdoinid", false);

        testCase("(Iamgoingtoescap+e((\\n)|(\\t)|(\\\\)|(\\+)|(\\|)|(\\))|(\\()|(\\*))+)*", "");
        testCase("(Iamgoingtoescap+e((\\n)|(\\t)|(\\\\)|(\\+)|(\\|)|(\\))|(\\()|(\\*))+)*", "Iamgoingtoescape\n\t\\)");
        testCase("(Iamgoingtoescap+e((\\n)|(\\t)|(\\\\)|(\\+)|(\\|)|(\\))|(\\()|(\\*))+)*", "Iamgoingtoescape\n\t\\)Iamgoingtoescappppe\\(\\+\\)");
        testCase("(Iamgoingtoescap+e((\\n)|(\\t)|(\\\\)|(\\+)|(\\|)|(\\))|(\\()|(\\*))+)*", "Iamgoingtoescappppe*");
        testCase("(Iamgoingtoescap+e((\\n)|(\\t)|(\\\\)|(\\+)|(\\|)|(\\))|(\\()|(\\*))+)*", "Iamgoingtoescappppe+\n\t");
        testCase("(Iamgoingtoescap+e((\\n)|(\\t)|(\\\\)|(\\+)|(\\|)|(\\))|(\\()|(\\*))+)*", "Iamgoingtoescappppe", false);
        testCase("(Iamgoingtoescap+e((\\n)|(\\t)|(\\\\)|(\\+)|(\\|)|(\\))|(\\()|(\\*))+)*", "Iamgoingtoescae\\n", false);
        testCase("(Iamgoingtoescap+e((\\n)|(\\t)|(\\\\)|(\\+)|(\\|)|(\\))|(\\()|(\\*))+)*", "Iamgoingtoescape", false);
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
