/* *****************************************************************************
 *  Name: Nguyen Le Nam Khanh
 *  Date: Jan 23, 2026
 *  Description: Baseball Elimination
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ResizingArrayBag;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;

public class BaseballElimination {

    private static final int UNKNOWN = -1;
    private static final int SURVIVE = 0;
    private static final int ELIMINATED = 1;

    private final int[] wins;
    private final int[] losses;
    private final int[] remainings;
    private final int[][] games;
    private final HashMap<String, Integer> teamToId;
    private final String[] idToTeam;
    private final int[] eliminationStatus;
    private final ArrayList<ResizingArrayBag<String>> certificates;

    // create a baseball division from given filename in format specified
    public BaseballElimination(String filename) {
        checkNonNull(filename);

        In in = new In(filename);
        final int N = in.readInt();
        if (N < 1) {
            throw new IllegalArgumentException("number of teams must be >= 1");
        }

        this.wins = new int[N];
        this.losses = new int[N];
        this.remainings = new int[N];
        this.games = new int[N][N];
        this.teamToId = new HashMap<>();
        this.idToTeam = new String[N];
        this.eliminationStatus = new int[N];
        this.certificates = new ArrayList<>();

        for (int id = 0; id < N; ++id) {
            eliminationStatus[id] = UNKNOWN;
            certificates.add(null);
        }

        for (int id = 0; id < N; ++id) {
            String team = in.readString();
            teamToId.put(team, id);
            idToTeam[id] = team;

            wins[id] = in.readInt();
            losses[id] = in.readInt();
            remainings[id] = in.readInt();

            for (int k = 0; k < N; ++k) {
                games[id][k] = in.readInt();
            }
        }
    }

    // number of teams
    public int numberOfTeams() {
        return this.wins.length;
    }

    // all teams
    public Iterable<String> teams() {
        return teamToId.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        validateTeam(team);

        int id = teamToId.get(team);
        return wins[id];
    }

    // number of losses for given team
    public int losses(String team) {
        validateTeam(team);

        int id = teamToId.get(team);
        return losses[id];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        validateTeam(team);

        int id = teamToId.get(team);
        return remainings[id];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        validateTeam(team1);
        validateTeam(team2);

        int id1 = teamToId.get(team1);
        int id2 = teamToId.get(team2);
        return games[id1][id2];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        validateTeam(team);
        checkElimination(team);

        int id = teamToId.get(team);
        return eliminationStatus[id] == ELIMINATED;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        validateTeam(team);
        checkElimination(team);

        int id = teamToId.get(team);
        return certificates.get(id);
    }

    private void checkNonNull(Object arg) {
        if (arg == null) {
            throw new IllegalArgumentException("argument is null");
        }
    }

    private void validateTeam(String team) {
        checkNonNull(team);

        if (!teamToId.containsKey(team)) {
            throw new IllegalArgumentException("unidentified team");
        }
    }

    private void checkElimination(String team) {
        final int target = teamToId.get(team);

        if (eliminationStatus[target] != UNKNOWN) {
            return;
        }

        ResizingArrayBag<String> certs = new ResizingArrayBag<>();
        final int N = numberOfTeams();
        final int targetMaxWin = wins[target] + remainings[target];

        for (int id = 0; id < N; ++id) {
            if (id != target && targetMaxWin < wins[id]) {
                certs.add(idToTeam[id]);
            }
        }

        if (!certs.isEmpty()) {
            eliminationStatus[target] = ELIMINATED;
            certificates.set(target, certs);
            return;
        }

        final int V = N * N + N + 2;
        final int s = V - 2;
        final int t = V - 1;
        final int teamVertexOffset = N * N;

        FlowNetwork G = new FlowNetwork(V);
        int totalRemains = 0;

        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < i; ++j) {
                if (i == target || j == target) {
                    continue;
                }

                int gameVertex = i * N + j;

                G.addEdge(new FlowEdge(s, gameVertex, games[i][j]));
                totalRemains += games[i][j];

                G.addEdge(new FlowEdge(gameVertex, i + teamVertexOffset,
                                       Double.POSITIVE_INFINITY));
                G.addEdge(new FlowEdge(gameVertex, j + teamVertexOffset,
                                       Double.POSITIVE_INFINITY));
            }
        }

        for (int id = 0; id < N; ++id) {
            if (id != target) {
                G.addEdge(new FlowEdge(id + teamVertexOffset, t, targetMaxWin - wins[id]));
            }
        }

        FordFulkerson ff = new FordFulkerson(G, s, t);

        if (ff.value() < totalRemains) {
            eliminationStatus[target] = ELIMINATED;

            for (int id = 0; id < N; ++id) {
                if (id != target && ff.inCut(id + teamVertexOffset)) {
                    certs.add(idToTeam[id]);
                }
            }

            certificates.set(target, certs);
        }
        else {
            eliminationStatus[target] = SURVIVE;
        }
    }

    // do unit testing
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.printf("%s is eliminated by the subset R = { ", team);
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.printf("%s ", t);
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
