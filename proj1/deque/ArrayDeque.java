package deque;

public class ArrayDeque<T> {
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
    public void addFirst(T item){
        items[nextfirst] = item;
        nextfirst = minushelper(nextfirst);
        size += 1;
        if(judgeresize()){
            resize();
        }
    }
    public void addLast(T item){
        items[nextlast] = item;
        nextlast = plushelper(nextlast);
        size += 1;
        if(judgeresize()){
            resize();
        }
    }
    public boolean isEmpty(){
        if(size == 0){
            return true;
        }else{
            return false;
        }
    }
    public int size(){
        return size;
    }
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
    public void check(){
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
    public T removeFirst(){
        if(isEmpty()){
            return null;
        }
        T x = items[plushelper((nextfirst))];
        nextfirst = plushelper(nextfirst);
        size -= 1;
        check();
        return x;
    }
    public T removeLast(){
        if(isEmpty()){
            return null;
        }
        T x = items[minushelper((nextlast))];
        nextlast = minushelper(nextlast);
        size -= 1;
        check();
        return x;
    }
    public T get(int index){
        return items[index];
    }

}
