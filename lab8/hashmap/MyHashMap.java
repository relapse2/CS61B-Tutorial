package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private Set<K> keys;
    // You should probably define some more!
    private static final int DEFAULT_INITIAL_SIZE = 16;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;
    private int bucketSize;
    private int nodesize;
    private int initialSize;
    private double loadFactor;

    /** Constructors */
    public MyHashMap() {
        this(DEFAULT_INITIAL_SIZE, DEFAULT_LOAD_FACTOR);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, DEFAULT_LOAD_FACTOR);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize = initialSize;
        this.loadFactor = maxLoad;
        this.nodesize = 0;
        this.bucketSize = initialSize;
        buckets = createTable(bucketSize);
        for (int i = 0; i < bucketSize; i++) {
            buckets[i] = createBucket();
        }
        keys = new HashSet<>();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key,value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<Node>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    private int hash(K key) {
        int h = key.hashCode();
        return Math.floorMod(h, bucketSize);
    }
    @Override
    public void clear(){
        for (int i = 0; i < bucketSize; i++) {
            buckets[i].clear();
        }
        keys.clear();
        this.nodesize = 0;
    }
    @Override
    public boolean containsKey(K key){
        if(key == null){
            return false;
        }
        int i = hash(key);
        //必须哈希码和equal都满足，因为可能都重写过，就很麻烦；；应该是通过hashcode缩小范围
        for (Node node : buckets[i]) {
            if (node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key){
        if(key == null){
            return null;
        }
        int i = hash(key);
        for (Node node : buckets[i]) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }
    @Override
    public int size(){
        return nodesize;
    }
    @Override
    public Set<K> keySet(){
        return keys;
    }
    @Override
    public void put(K key, V value){
        int i = hash(key);
        if(containsKey(key)==false) {
            Node node = createNode(key, value);
            buckets[i].add(node);
            keys.add(key);
            nodesize++;
            if (nodesize / bucketSize >= loadFactor) {
                resize(bucketSize*2);
            }
        }else{
            for (Node node : buckets[i]) {
                if (node.key.equals(key)) {
                    node.value = value;
                }
            }
        }

    }

    //完成resize时，应当注意不能直接替换，而是建立一个新的hashmap，因为要实现整体的resize，hash后的key的位置也需要改变，因为hash与buckersize有关，值会变化，则我们去建立一个新的map，整体上实现，思考整体功能的结合）
    //并且上面有一个getnode辅助函数也很有用，多个函数中的都有使用
    private void resize(int newBucketSize){
        MyHashMap<K, V> newMyHashMap = new MyHashMap<>(newBucketSize, this.loadFactor);
        for (K key : keySet()) {
            newMyHashMap.put(key, get(key));
        }
        this.bucketSize = newMyHashMap.bucketSize;
        this.buckets = newMyHashMap.buckets;
    }
    @Override
    public V remove(K key){
        throw new UnsupportedOperationException("unsupported");
    }
    @Override
    public V remove(K key, V value){
        throw new UnsupportedOperationException("unsupported");
    }
    @Override
    public Iterator<K> iterator(){
        return keys.iterator();
    }
}
