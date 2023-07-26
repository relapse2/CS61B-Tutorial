package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.GitletRepository.blobs;
import static gitlet.Utils.join;


public class Blob implements Serializable {
    private String Id;

    private byte[] bytes;

    private File fileName;          //真正文件

    private String filePath;            //源文件位置

    private File blobSaveFileName;      //现objects中记录的blob文件
    public Blob(byte[] bytes, File fileName, String filePath){
        this.bytes = bytes;
        this.fileName = fileName;
        this.filePath = filePath;
        this.Id = generateId();
        this.blobSaveFileName = generateBlobSaveFileName();
    }
    public String getFileName(){
        return fileName.getName();
    }
    private String generateId() {
        return Utils.sha1(filePath,bytes);
    }
    public String getId(){
        return this.Id;
    }
    public String getfilePath(){
        return this.filePath;
    }
    private File generateBlobSaveFileName() {
        return join(blobs, Id);
    }
    public File getBlobSaveFileNameFile(){
        return this.blobSaveFileName;
    }
    public byte[] getBytes(){
        return this.bytes;
    }

}
