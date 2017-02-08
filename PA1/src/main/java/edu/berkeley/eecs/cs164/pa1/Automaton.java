package edu.berkeley.eecs.cs164.pa1;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class represents a complete NFA, with a single starting
 * state and a single ending state. It roughly corresponds to the
 * Frag struct from Russ Cox's page.
 */
public class Automaton {
    private final AutomatonState start;
    private final AutomatonState out;

    /**
     * Create a new Automaton from a given start and output state
     *
     * @param start Start state for the automaton
     * @param out   Output / final state for the automaton
     */
    public Automaton(AutomatonState start, AutomatonState out) {
        this.start = start;
        this.out = out;
    }

    public AutomatonState getOut() {
        return out;
    }

    public AutomatonState getStart() {
        return start;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("digraph G {\n");
        builder.append(String.format("\trankdir=LR;%n"));
        builder.append(String.format("\tinput [shape=none, label=\"start\"];%n"));
        builder.append(String.format("\tinput -> s%d;%n", getStart().getMyId()));
        builder.append(String.format("\ts%d [shape=doublecircle];%n", getOut().getMyId()));
        printTraversal(getStart(), new HashSet<AutomatonState>(), builder);
        builder.append("}\n");
        return builder.toString();
    }

    private void printTraversal(AutomatonState current, HashSet<AutomatonState> visited, StringBuilder builder) {
        if (!visited.contains(current)) {
            visited.add(current);
            for (Map.Entry<Character, Set<AutomatonState>> entry : current.getAllTransitions()) {
                for (AutomatonState target : entry.getValue()) {
                    String label = String.format("\"%s\"", entry.getKey() == null ? "&epsilon;" : entry.getKey());
                    printTraversal(target, visited, builder);
                    builder.append(String.format("\ts%d -> s%d [label=%s];%n", current.getMyId(), target.getMyId(), label));
                }
            }
        }
    }
}
