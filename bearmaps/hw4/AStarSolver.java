package bearmaps.hw4;

import bearmaps.proj2ab.ArrayHeapMinPQ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class AStarSolver<Vertex> implements ShortestPathsSolver<Vertex> {
    private SolverOutcome outcome;
    private LinkedList<Vertex> solution;
    private double solutionWeight;
    private int numStatesExplored;
    private double explorationTime;
    private HashMap<Vertex, Double> distTo;
    private HashMap<Vertex, Vertex> edgeTo;
    private ArrayHeapMinPQ<Vertex> pq;

    /* AStarSolver: Constructor which finds the solution, computing everything necessary for all
       other methods to return their results in constant time. Note that timeout passed in is in
       seconds. */
    public AStarSolver(AStarGraph<Vertex> input, Vertex start, Vertex end, double timeout) {
        double startTime = System.currentTimeMillis();
        double currentTime = startTime;

        solution = new LinkedList<>();
        solutionWeight = 0;
        numStatesExplored = 0;

        distTo = new HashMap<>();
        edgeTo = new HashMap<>();
        pq = new ArrayHeapMinPQ<>();

        distTo.put(start, 0.0);
        pq.add(start, 0);

        while (pq.size() > 0 && !pq.getSmallest().equals(end) && (currentTime - startTime) < timeout) {
            Vertex current = pq.removeSmallest();
            List<WeightedEdge<Vertex>> edgeNeighbors = input.neighbors(current);
            for (WeightedEdge<Vertex> e : edgeNeighbors) {
                double edgeEndToGoalDist = input.estimatedDistanceToGoal(e.to(), end);
                relax(e, edgeEndToGoalDist);
            }
            numStatesExplored += 1;
            currentTime = System.currentTimeMillis();
        }

        if ((currentTime - startTime) >= timeout) {
            outcome = SolverOutcome.TIMEOUT;
        } else if (pq.size() == 0) {
            outcome = SolverOutcome.UNSOLVABLE;
        } else {
            outcome = SolverOutcome.SOLVED;
            solutionWeight = distTo.get(end);
            solution.addFirst(end);
            Vertex current = end;
            while (!current.equals(start)) {
                current = edgeTo.get(current);
                solution.addFirst(current);
            }
        }

        currentTime = System.currentTimeMillis();
        explorationTime = (currentTime - startTime) / 1000.0;
    }

    private void relax(WeightedEdge<Vertex> e, double bToGoalDist) {
        Vertex a = e.from();
        Vertex b = e.to();
        double w = e.weight();
        if (!distTo.containsKey(b)) {
            double distToB = distTo.get(a) + w;
            double bPriority = distToB + bToGoalDist;

            distTo.put(b, distToB);
            edgeTo.put(b, a);
            pq.add(b, bPriority);
        } else if (distTo.get(a) + w < distTo.get(b)) {
            double distToB = distTo.get(a) + w;
            double bPriority = distToB + bToGoalDist;

            distTo.put(b, distToB);
            edgeTo.put(b, a);
            pq.changePriority(b, bPriority);
        }
    }

    /* Returns one of SolverOutcome.SOLVED, SolverOutcome.TIMEOUT, or SolverOutcome.UNSOLVABLE.
       Should be SOLVED if the AStarSolver was able to complete all work in the time given.
       UNSOLVABLE if the priority queue became empty. TIMEOUT if the solver ran out of time. */
    @Override
    public SolverOutcome outcome() {
        return outcome;
    }

    /* Returns a list of vertices corresponding to a solution.
       Should be empty if result was TIMEOUT or UNSOLVABLE. */
    @Override
    public List<Vertex> solution() {
        return solution;
    }

    /* Returns the total weight of the given solution, taking into account edge weights.
       Should be 0 if result was TIMEOUT or UNSOLVABLE. */
    @Override
    public double solutionWeight() {
        return solutionWeight;
    }

    /* Returns the total number of priority queue dequeue operations. */
    @Override
    public int numStatesExplored() {
        return numStatesExplored;
    }

    /* Returns the total time spent in seconds by the constructor. */
    @Override
    public double explorationTime() {
        return explorationTime;
    }
}
