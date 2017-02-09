package edu.berkeley.eecs.cs164.pa1;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * This class simulates a non-deterministic finite automaton over ASCII strings.
 */
public class NFASimulator {
    private final Automaton nfa;

    /**
     * Create a new simulator from a given NFA structure.
     *
     * @param nfa the nfa to simulate
     */
    public NFASimulator(Automaton nfa) {
        this.nfa = nfa;
    }

    /**
     * Determines whether or not the given text is accepted by the NFA.
     *
     * @param text the text to try matching
     * @return true if the text is accepted by the NFA, else false
     */
    public boolean matches(String text) {
        Set<AutomatonState> currentStates = new HashSet<AutomatonState>();
        Set<AutomatonState> epStates = new HashSet<AutomatonState>();
        currentStates.add(this.nfa.getStart());
        currentStates.addAll(closure(this.nfa.getStart()));
        char[] match = text.toCharArray();
        for (char ch : match) { currentStates = dfaEdge(currentStates, ch); }
        return currentStates.contains(this.nfa.getOut());
    }

    /**
     * Determines the set of possible NFA states the input can be in after
     * the next character.
     *
     * @param states The set of current possible states
     * @param ch The next character in the input
     * @return The next set of possible states
     */
    private Set<AutomatonState> dfaEdge(Set<AutomatonState> states, char ch) {
        Set<AutomatonState> nextStates = new HashSet<AutomatonState>();
        Set<AutomatonState> epStates = new HashSet<AutomatonState>();
        for (AutomatonState state : states) {
            nextStates.addAll(state.getTransitions(ch));
        }
        for (AutomatonState state : nextStates) {
            epStates.addAll(closure(state));
        }
        nextStates.addAll(epStates);
        return nextStates;
    }

    /**
     * Determines the set of states reachable by traveling along epsilon
     * edges from the given state
     *
     * @param state The current state
     * @return The set of states reachable through epsilon transitions
     */
    private Set<AutomatonState> closure(AutomatonState state) {
        Set<AutomatonState> epStates = new HashSet<AutomatonState>();
        Set<AutomatonState> currEpStates = new HashSet<AutomatonState>();
        Set<AutomatonState> nextEpStates = new HashSet<AutomatonState>();
        Set<AutomatonState> stateCheck;
        nextEpStates.addAll(state.getEpsilonTransitions());
        do {
            currEpStates = nextEpStates;
            nextEpStates = new HashSet<AutomatonState>();
            for (AutomatonState epState : currEpStates) {
                stateCheck = epState.getEpsilonTransitions();
                for (AutomatonState stateCh : stateCheck) {
                    if (!epStates.contains(stateCh)) {
                        nextEpStates.add(stateCh);
                        epStates.add(stateCh);
                    }
                }
            }
        } while (!nextEpStates.isEmpty());
        epStates.addAll(currEpStates);
        epStates.addAll(nextEpStates);
        return epStates;
    }

}
