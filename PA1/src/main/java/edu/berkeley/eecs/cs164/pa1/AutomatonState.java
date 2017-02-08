package edu.berkeley.eecs.cs164.pa1;

import java.util.*;

/**
 * This class represents a single state in an NFA, and keeps track of its outgoing
 * transitions. Empty transitions can be added or requested using null as a character.
 */
public class AutomatonState {
    private static int maxId = 0;
    private final Map<Character, Set<AutomatonState>> transitions = new HashMap<Character, Set<AutomatonState>>();
    private final int myId;

    public AutomatonState() {
        myId = ++maxId;
    }

    /**
     * Adds a transition with an empty label
     *
     * @param state the state to transition to
     */
    public void addEpsilonTransition(AutomatonState state) {
        Set<AutomatonState> automatonStates = transitions.get(null);
        if (automatonStates == null) {
            automatonStates = new HashSet<AutomatonState>();
        }
        automatonStates.add(state);
        transitions.put(null, automatonStates);
    }

    /**
     * Adds a transition with a given label
     *
     * @param ch    the character to transition on
     * @param state the state to transition to
     */
    public void addTransition(char ch, AutomatonState state) {
        Set<AutomatonState> automatonStates = transitions.get(ch);
        if (automatonStates == null) {
            automatonStates = new HashSet<AutomatonState>();
        }
        automatonStates.add(state);
        transitions.put(ch, automatonStates);
    }

    /**
     * Gets the set of states reachable from this state via the given character
     *
     * @param ch the character to follow
     * @return set of states
     */
    public Set<AutomatonState> getTransitions(char ch) {
        Set<AutomatonState> states = transitions.get(ch);
        if (states == null) {
            states = Collections.emptySet();
        }
        return states;
    }

    /**
     * Gets the set of states reachable from this state via the empty string
     *
     * @return set of states
     */
    public Set<AutomatonState> getEpsilonTransitions() {
        Set<AutomatonState> states = transitions.get(null);
        if (states == null) {
            states = Collections.emptySet();
        }
        return states;
    }

    public Set<Map.Entry<Character, Set<AutomatonState>>> getAllTransitions() {
        return transitions.entrySet();
    }

    @Override
    public int hashCode() {
        int result = transitions.keySet().hashCode();
        result = 31 * result + getMyId();
        return result;
    }

    /**
     * Gets a unique id for the state
     *
     * @return the state's id
     */
    public int getMyId() {
        return myId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AutomatonState that = (AutomatonState) o;

        return transitions.equals(that.transitions);
    }

    @Override
    public String toString() {
        return "s" + myId;
    }
}
