package gitlet;

import java.io.Serializable;
import java.util.TreeMap;

public class Stage implements Serializable {        //这个类相当于一个decorator
    private TreeMap<String,String> stage;
    public Stage(){
        this.stage = new TreeMap<>();
    }
    public Stage(TreeMap<String,String> stage){
        this.stage = stage;
    }
    public TreeMap<String,String> getStage(){
        return this.stage;
    }
    public String get(String key){
        return this.stage.get(key);
    }
    public boolean containsKey(String key){
        return this.stage.containsKey(key);
    }
    public boolean containsValue(String value){
        return this.stage.containsValue(value);
    }
    public void put(String key,String value){
        this.stage.put(key,value);
    }
    public void putAll(TreeMap<String,String> map){
        this.stage.putAll(map);
    }
    public void remove(String key){
        this.stage.remove(key);
    }
    public boolean isEmpty(){
        return this.stage.isEmpty();
    }
    public java.util.Set<String> keySet(){
        return this.stage.keySet();
    }
}
