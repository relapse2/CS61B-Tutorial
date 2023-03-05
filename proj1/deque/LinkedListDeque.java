package deque;

import java.util.Iterator;

public class LinkedListDeque <T> implements Deque<T>,Iterable<T> {
    private class Node {
        public T item;
        public Node prev;
        public Node next;
        public Node (T i, Node n, Node m){
            item = i;
            next = n;
            prev = m;
        }
    }
    private Node sentinel;
    private int size;
    public LinkedListDeque(){
        sentinel = new Node  (null, sentinel, sentinel);
        size = 0;
    }
    public LinkedListDeque(T x){
        sentinel = new Node (null,null,null);
        sentinel.next = new Node (x,null,sentinel);
        Node last = sentinel.next;
        sentinel.prev = last;
        size = 1;
    }
    @Override
    public void addFirst(T item){
        if(isEmpty()){
            sentinel.next = new Node(item,sentinel,sentinel);
            sentinel.prev = sentinel.next;
        } else{
            Node second = sentinel.next;
            sentinel.next = new Node(item,second,sentinel);
            second.prev = sentinel.next;
        }
        size += 1;
    }
    @Override
    public void addLast(T item){
        if(isEmpty()) {
            sentinel.next = new Node(item, sentinel, sentinel);
            sentinel.prev = sentinel.next;
        }else {
            Node x = sentinel.prev;
            sentinel.prev = new Node(item, sentinel, x);
            Node last = sentinel.prev;
            x.next = last;
        }
        size += 1;
    }
    @Override
    public boolean isEmpty(){
        if(size != 0){
            return false;
        }else{
            return true;
        }
    }
    @Override
    public int size(){
        return size;
    }
    @Override
    public void printDeque(){
        Node x = sentinel.next;
        if(isEmpty()){
            System.out.println("");
        }else{
            while(x.next!=sentinel){
                System.out.print(x.item);
                x=x.next;
            }
            System.out.println(x.item);
        }
    }
    @Override
    public T removeFirst(){
        if(isEmpty()){
            return null;
        }
        T x = sentinel.next.item;
        if(size > 1) {
            sentinel.next = sentinel.next.next;
            sentinel.next.prev = sentinel;
        }else if(size == 1){
            sentinel.next = sentinel;
            sentinel.prev = sentinel;
        }
        size -= 1;
        return x;
    }
    @Override
    public T removeLast(){
        if(isEmpty()){
            return null;
        }
        Node x = sentinel.prev;
        T w =x.item;
        x.prev.next = sentinel;
        sentinel.prev = x.prev;
        size -= 1;
        return w;
    }
    @Override
    public T get(int index){
        Node x=sentinel;
        if(index > size){
            return null;
        }
        for(int i=1;i<=index;i++){
            x=x.next;
        }
        return x.item;
    }
    private T getRecursiveHelper(int index, Node p) {
        if (p == sentinel) {
            return null;
        }
        if (index == 0) {
            return p.item;
        }
        return getRecursiveHelper(index - 1, p.next);
    }
    public T getRecursive(int index) {
        return getRecursiveHelper(index, sentinel.next);
    }
    public boolean equals(Object o){
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        boolean b = (o instanceof Deque);
        if(b == false){
            return false;
        }
        Deque<T> other = (Deque<T>) o;
        if (size() != other.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            T item1 = get(i);
            T item2 = other.get(i);
            if (!item1.equals(item2)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private Node iterNode;

        LinkedListDequeIterator() {
            if(isEmpty()){
                iterNode = sentinel;
            }else{
                iterNode = sentinel.next;
            }
        }

        public boolean hasNext() {
            return iterNode != sentinel;
        }

        public T next() {
            T returnItem = iterNode.item;
            iterNode = iterNode.next;
            return returnItem;
        }
    }

}