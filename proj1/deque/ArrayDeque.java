package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>,Iterable<T> {
    private T[] items;
    private int size;
    private int nextfirst;
    private int nextlast;
    public ArrayDeque(){
        items = (T[])new Object[8];
        size = 0;
        nextfirst = 4;
        nextlast = 5;
    }
    private boolean judgeresize(){
        return nextlast==nextfirst+1 || nextfirst == items.length-1 && nextlast == 0;
    }
    private void resize(){
        T[] items2 = (T[])new Object[items.length*2];
        System.arraycopy(items,0,items2,0,nextlast);
        System.arraycopy(items,nextlast,items2,items.length+nextlast,items.length-nextlast);
        items = items2;
        nextfirst = items.length/2+nextfirst;
    }
    /**One of the two basic function represents circulation.*/
    private  int plushelper(int plus){
        if(plus+1 >=items.length){
            return 0;
        }
        return plus+1;
    }
    /**One of the two basic function represents circulation.*/
    private  int  minushelper(int minus){
        if(minus-1 < 0){
            return items.length-1;
        }
        return minus-1;
    }
    @Override
    public void addFirst(T item){
        items[nextfirst] = item;
        nextfirst = minushelper(nextfirst);
        size += 1;
        if(judgeresize()){
            resize();
        }
    }
    @Override
    public void addLast(T item){
        items[nextlast] = item;
        nextlast = plushelper(nextlast);
        size += 1;
        if(judgeresize()){
            resize();
        }
    }
    @Override
    public boolean isEmpty(){
        if(size == 0){
            return true;
        }else{
            return false;
        }
    }
    @Override
    public int size(){
        return size;
    }
    @Override
    public void printDeque(){
        if(nextfirst < nextlast){
            int i = nextfirst+1;
            for(;i < nextlast-1;i++){
                System.out.print(items[i]);
            }
            System.out.println(items[i]);
        }else{
            int i = nextfirst+1;
            for(;i < size;i++){
                System.out.print(items[i]);
            }
            i = 0;
            for(;i < nextlast-1;i++){
                System.out.print(items[i]);
            }
            System.out.println(items[i]);
        }
    }
    private void check(){
        if(size <= items.length*0.25 && size >= 16) {
            T[] items2 = (T[]) new Object[size];
            if (nextfirst < nextlast) {
                System.arraycopy(items, nextfirst + 1, items2, 0, size);
            } else {
                System.arraycopy(items, nextfirst + 1, items2, 0, items.length - nextfirst-1);
                System.arraycopy(items, 0, items2, items.length- nextfirst - 1, nextlast);
            }
            items = items2;
            nextfirst = size-1;
            nextlast = 0;
        }
    }
    @Override
    public T removeFirst(){
        if(isEmpty()){
            return null;
        }
        T x = items[plushelper((nextfirst))];
        items[plushelper((nextfirst))] = null;
        nextfirst = plushelper(nextfirst);
        size -= 1;
        check();
        return x;
    }
    @Override
    public T removeLast(){
        if(isEmpty()){
            return null;
        }
        T x = items[minushelper((nextlast))];
        items[plushelper((nextlast))] = null;
        nextlast = minushelper(nextlast);
        size -= 1;
        check();
        return x;
    }
    @Override
    public T get(int index){
        if(isEmpty()){
            return null;
        }
        return items[(nextfirst + 1 + index) % items.length];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator<T> implements Iterator<T> {
        private int pos;

        ArrayDequeIterator() {
            pos = 0;
        }

        public boolean hasNext() {
            return pos < size;
        }

        public T next() {
            T returnItem = (T) get(pos);
            pos += 1;
            return returnItem;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }

        if (!(o instanceof Deque)) {
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

}
