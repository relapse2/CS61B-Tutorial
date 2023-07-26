package gitlet;



import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private ArrayList<String> parents;
    private Date date;
    private TreeMap<String, String> pathToBlobID = new TreeMap<>();         //利用map来记录blob文件Id到文件原路径的映射,与名字不同，但懒得改了
    private String Id;
    private File commitSaveFileName;        //commit存放文件
    private String timeStamp;
    public Commit(String message, ArrayList<String> parents, Date date, TreeMap<String, String> blobId){
        this.message = message;
        this.parents = parents;
        this.date = date;
        this.pathToBlobID = blobId;
        this.Id = generateId();
        this.timeStamp = dateToTimeStamp(date);
    }
    private static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }
    private String generateId() {
        return Utils.sha1(dateToTimeStamp(date), message, parents.toString(), pathToBlobID.toString());
    }
    public String getId(){
        return this.Id;
    }
    public TreeMap<String, String> getPathToBlobID(){
        return pathToBlobID;
    }
    public void setCommitSaveFileName(File filename){
        this.commitSaveFileName = filename;             //设定commit文件名字，不过我的就是Id，没什么区别
    }
    public File getCommitSaveFileNameFile(){
        return this.commitSaveFileName;
    }


    public ArrayList<String> getParentsCommitID() {
        return parents;
    }

    public String getMessage() {
        return message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}
