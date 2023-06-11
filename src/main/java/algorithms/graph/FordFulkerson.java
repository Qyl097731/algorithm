package algorithms.graph;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 最大流：EK 即最短增益路径
 * bfs 找寻最短路径，更新增益，直至找不到
 * @author qyl
 */
public class FordFulkerson {
    private boolean[] marked; // 标记每个点是否被访问过
    private FlowEdge[] edgeTo; // 记录每个点的上一条边
    private int value; // 最大流量

    public FordFulkerson(FlowNetwork G, int s, int t) {
        value = 0;
        while (hasAugmentingPath(G, s, t)) {
            int bottle = Integer.MAX_VALUE; // 增广路径上的最小剩余容量
            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                bottle = Math.min(bottle, edgeTo[v].residualCapacityTo(v));
            }
            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                edgeTo[v].addResidualFlowTo(v, bottle);
            }
            value += bottle;
        }
    }

    public int value() {
        return value;
    }

    private boolean hasAugmentingPath(FlowNetwork G, int s, int t) {
        edgeTo = new FlowEdge[G.V()];
        marked = new boolean[G.V()];

        // 广度优先搜索
        Queue<Integer> queue = new LinkedList<Integer> ();
        queue.add(s);
        marked[s] = true;
        while (!queue.isEmpty()) {
            int v = queue.poll();
            for (FlowEdge e : G.adj(v)) {
                int w = e.other(v);
                if (!marked[w] && e.residualCapacityTo(w) > 0) {
                    edgeTo[w] = e;
                    marked[w] = true;
                    queue.add(w);
                }
            }
        }
        return marked[t];
    }

    public static void main(String[] args) {
        // 构造一个有向图
        int V = 6;
        FlowNetwork flowNetwork = new FlowNetwork(V);
        FlowEdge edge1 = new FlowEdge(0, 1, 2);
        FlowEdge edge2 = new FlowEdge(0, 2, 3);
        FlowEdge edge3 = new FlowEdge(1, 3, 3);
        FlowEdge edge4 = new FlowEdge(1, 4, 1);
        FlowEdge edge5 = new FlowEdge(2, 4, 1);
        FlowEdge edge6 = new FlowEdge(2, 5, 2);
        FlowEdge edge7 = new FlowEdge(3, 5, 2);
        FlowEdge edge8 = new FlowEdge(4, 5, 3);
        flowNetwork.addEdge(edge1);
        flowNetwork.addEdge(edge2);
        flowNetwork.addEdge(edge3);
        flowNetwork.addEdge(edge4);
        flowNetwork.addEdge(edge5);
        flowNetwork.addEdge(edge6);
        flowNetwork.addEdge(edge7);
        flowNetwork.addEdge(edge8);

        // 运行Ford-Fulkerson算法
        FordFulkerson fordFulkerson = new FordFulkerson(flowNetwork, 0, 5);
        System.out.println("Maximum flow is " + fordFulkerson.value());
    }
}

/**
 * 流量网络图
 */
class FlowNetwork {
    private final int V; // 点的数量
    private int E; // 边的数量
    private final LinkedList<FlowEdge>[] adj; // 用于存储邻接表

    public FlowNetwork(int V) {
        this.V = V;
        this.E = 0;
        this.adj = new LinkedList[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new LinkedList<FlowEdge>();
        }
    }

    public int V() {
        return V;
    }

    public int E() {
        return E;
    }

    public void addEdge(FlowEdge e) {
        int v = e.from();
        int w = e.to();
        adj[v].add(e);
        adj[w].add(e);
        E++;
    }

    public Iterable<FlowEdge> adj(int v) {
        return adj[v];
    }
}

/**
 * 流量边
 */
class FlowEdge {
    private final int v; // 边的起点
    private final int w; // 边的终点
    private final int capacity; // 边的容量
    private int flow; // 边当前的流量

    public FlowEdge(int v, int w, int capacity) {
        this.v = v;
        this.w = w;
        this.capacity = capacity;
        this.flow = 0;
    }

    public int capacity() {
        return capacity;
    }

    public int flow() {
        return flow;
    }

    public int from() {
        return v;
    }

    public int to() {
        return w;
    }

    /**
     * 查看起点为vertex的另一端
     * @param vertex
     * @return vertex的另一端
     */
    public int other(int vertex) {
        if (vertex == v) {
            return w;
        } else {
            return v;
        }
    }

    /**
     * 如果反向边，就返回当前流量，否则返回剩余流量
     * @param vertex
     * @return 可用流量
     */
    public int residualCapacityTo(int vertex) {
        if (vertex == v) {
            return flow;
        } else {
            return capacity - flow;
        }
    }

    /**
     * 进行流量增加、减少
     * @param vertex
     * @param delta
     */
    public void addResidualFlowTo(int vertex, int delta) {
        if (vertex == v) {
            flow -= delta;
        } else {
            flow += delta;
        }
    }
}

