package deque;

public class LinkedListDeque <type> {
    private class Node {
        public type item;
        public Node prev;
        public Node next;
        public Node (type i, Node n, Node m){
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
    public LinkedListDeque(type x){
        sentinel = new Node (null,null,null);
        sentinel.next = new Node (x,null,sentinel);
        Node last = sentinel.next;
        sentinel.prev = last;
        size = 1;
    }
    public void addFirst(type item){
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
    public void addLast(type item){
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
    public boolean isEmpty(){
        if(size != 0){
            return false;
        }else{
            return true;
        }
    }
    public int size(){
        return size;
    }
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
    public type removeFirst(){
        if(isEmpty()){
            return null;
        }
        type x = sentinel.next.item;
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
    public type removeLast(){
        if(isEmpty()){
            return null;
        }
        Node x = sentinel.prev;
        type w =x.item;
        x.prev.next = sentinel;
        sentinel.prev = x.prev;
        size -= 1;
        return w;
    }
    public type get(int index){
        Node x=sentinel;
        if(index > size){
            return null;
        }
        for(int i=1;i<=index;i++){
            x=x.next;
        }
        return x.item;
    }
    private type getRecursiveHelper(int index, Node p) {
        if (p == sentinel) {
            return null;
        }
        if (index == 0) {
            return p.item;
        }
        return getRecursiveHelper(index - 1, p.next);
    }
    public type getRecursive(int index) {
        return getRecursiveHelper(index, sentinel.next);
    }
    public boolean equals(Object o){
        boolean b = (o instanceof LinkedListDeque);
        if(b == true){
            return false;
        }
        int index = 0;
        boolean content = true;
        Node x = sentinel;
        LinkedListDeque o2 = (LinkedListDeque<type>) o;
        while(x.next!=sentinel && size == o2.size()){
            x=x.next;
            index += 1;
            if(x.item != o2.get(index)){
                content = false;
                break;
            }
        }
        return content;
    }
}