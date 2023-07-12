package byow.Core;

public class union {        //本身为weighted quick union,直接拉过来就好了，很实用
    int[] parent;

    /* 利用disjoint set来实现union从而区分. */
    public union(int n) {
        parent = new int[n];
        // root设为-1
        for (int i = 0; i < n; i++) {
            parent[i] = -1;
        }
    }

    /* Throws an exception if v1 is not a valid vertex. */
    private void validate(int v1) {
        if (v1 < 0 || v1 >= parent.length) {
            throw new IllegalArgumentException("beepers");
        }
    }

    /* Returns the size of the set v1 belongs to. */
    public int sizeOf(int v1) {
        int root = find(v1);
        return -1 * parent[root];
    }

    /* Returns the parent of v1. If v1 is the root of a tree, returns the
       negative size of the tree for which v1 is the root. */
    public int parent(int v1) {
        validate(v1);
        return parent[v1];
    }

    /* Returns true if nodes v1 and v2 are connected. */
    public boolean isConnected(int v1, int v2) {
        return find(v1) == find(v2);
    }

    /*  */
    public void connect(int v1, int v2) {
        validate(v1);
        validate(v2);
        int p1 = find(v1);
        int p2 = find(v2);
        if (p1 == p2) {
            return;
        }
        int v = sizeOf(p1) > sizeOf(p2) ? p2 : p1;
        int other = sizeOf(p1) > sizeOf(p2) ? p1 : p2;
        parent[other] -= sizeOf(v);
        parent[v] = other;
    }

    /* Returns the root of the set v1 belongs to. Path-compression is employed
       allowing for fast search-time. */
    public int find(int v1) {
        validate(v1);
        int temp = v1;
        while (parent[temp] >= 0) {
            temp = parent[temp];
        }
        return temp;
    }

}