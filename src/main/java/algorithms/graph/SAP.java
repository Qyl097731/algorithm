package algorithms.graph;

import java.util.Arrays;

/**
 * @description 最大流邻接矩阵模板
 * @date 2023/6/7 23:04
 * @author: qyl
 */
public class SAP {
    final int MAXN = 1100;
    int[][] maze = new int[MAXN][MAXN];
    int[] gap = new int[MAXN], dis = new int[MAXN], pre = new int[MAXN], cur = new int[MAXN];

    int sap(int start, int end, int nodenum) {
        Arrays.fill (cur, 0);
        Arrays.fill (dis, 0);
        Arrays.fill (gap, 0);

        int u = pre[start] = start, maxflow = 0, aug = -1;
        gap[0] = nodenum;
        while (dis[start] < nodenum) {
            for (int v = cur[u]; v < nodenum; v++) {
                if (maze[u][v] != 0 && dis[u] == dis[v] + 1) {
                    if (aug == -1 || aug > maze[u][v]) {
                        aug = maze[u][v];
                    }
                    pre[v] = u;
                    u = cur[u] = v;
                    if (v == end) {
                        maxflow += aug;
                        for (u = pre[u]; v != start; v = u, u = pre[u]) {
                            maze[u][v] -= aug;
                            maze[v][u] += aug;
                        }
                        aug = -1;
                    }
                    break;
                }
            }
            int mindis = nodenum - 1;
            for (int v = 0; v < nodenum; v++) {
                if (maze[u][v] != 0 && mindis > dis[v]) {
                    cur[u] = v;
                    mindis = dis[v];
                }
            }
            if ((--gap[dis[u]]) == 0) {
                break;
            }
            gap[dis[u] = mindis + 1]++;
            u = pre[u];
        }
        return maxflow;
    }
}
