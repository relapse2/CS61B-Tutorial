package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>,V> implements Map61B <K,V> {
    private int size;
    private BSTNode root;

    private class BSTNode {
        private K key;
        private V value;
        private BSTNode left, right;

        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
        }
    };

    public BSTMap(){
        size = 0;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        if (key == null) {
            return false;
        }
        return contain(root, key) != null;
    }
    private BSTNode contain(BSTNode Node,K key){
        if(Node == null){
            return null;
        }
        int cmp = key.compareTo(Node.key);
        if (cmp < 0) {
            return contain(Node.left, key);
        } else if (cmp > 0) {
            return contain(Node.right, key);
        } else {
            return Node;
        }
    }

    @Override
    public V get(K key) {
        if(containsKey(key) == false){
            return null;
        }else {
            return contain(root,key).value;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("calls put() with a null key");
        }
        root = put(root,key,value);
    }

    private BSTNode put(BSTNode Node, K key, V value){
        if(Node == null){
            size += 1;
            return new BSTNode(key, value);
        }
        int cmp = key.compareTo(Node.key);
        if(cmp > 0){
            Node.right = put(Node.right, key, value);
        }else if(cmp < 0){
            Node.left = put(Node.left, key, value);
        }else{
            Node.value = value;
        }
        return Node;
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
    public void printInOrder() {

    }
}
