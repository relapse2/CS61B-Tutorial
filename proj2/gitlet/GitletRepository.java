package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

public class GitletRepository {
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File GITLET_FOLDER = join(".gitlet");
    static final File objects = join(GITLET_FOLDER, "objects");
    static final File commits = join(GITLET_FOLDER, "objects","commits");
    static final File blobs = join(GITLET_FOLDER, "objects","blobs");
    static final File refs = join(GITLET_FOLDER,"refs");
    static final File heads = join(GITLET_FOLDER,"refs","heads");
    static final File HEAD = join(GITLET_FOLDER,"HEAD");
    static final File addstage = join(GITLET_FOLDER,"addstage");
    static final File removestage = join(GITLET_FOLDER,"removestage");
    static final File master = join(GITLET_FOLDER,"refs","heads","master");

    /** init command */
    public static void init() {
        try {
            if (!GITLET_FOLDER.exists()) {
                GITLET_FOLDER.mkdir();
            }else{
                throw new GitletException("A Gitlet version-control system already exists in the current directory.");
            }
            //建立.gitlet基本框架
            objects.mkdir();
            refs.mkdir();
            heads.mkdir();
            commits.mkdir();
            blobs.mkdir();
            HEAD.createNewFile();
            master.createNewFile();

            //求时间
            Date currentdate = new Date(0);
            Commit initCommit = new Commit("initial commit",new ArrayList<>(),currentdate,new TreeMap<>());
            File initcommit = join(GITLET_FOLDER,"objects","commits",initCommit.getId());
            Utils.writeObject(initcommit,initCommit);               //写入序列化commit文件
            Utils.writeContents(master,initCommit.getId());         //写入master最新的commitId来找到commit文件
            Utils.writeContents(HEAD,master.getAbsolutePath());     //HEAD中存放现在指向分支位置

        } catch (GitletException excp) {
            System.out.println(excp.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private static File getFileFromCWD(String file) {
        return Paths.get(file).isAbsolute()
                ? new File(file)
                : join(CWD, file);
    }
    public static void add(String filename){
        try {
            File file = getFileFromCWD(filename);
            if (!file.exists()) {
                throw new GitletException("File does not exist.");
            }
            byte[] bytes = Utils.readContents(file);
            Blob blob = new Blob(bytes, file, file.getPath());
            File blobsavefile = blob.getBlobSaveFileNameFile();
            Utils.writeObject(blobsavefile,blob);
            Stage addstage1;
            Stage removestage1;
            if(!addstage.exists()){
                addstage1 = new Stage();
            }else{
                addstage1 = readObject(addstage, Stage.class);    //建立一个Stage类来装饰map来维护stage
            }
            if(!removestage.exists()){
                removestage1 = new Stage();
            }else{
                removestage1 = readObject(removestage, Stage.class);    //建立一个Stage类来装饰map来维护stage
            }
            if(removestage1.containsKey(blob.getId())){
                removestage1.remove(blob.getId());              //add的文件removestage里有的话，删掉
            }
            if(addstage1.containsValue(file.getAbsolutePath())){
                addstage1.remove(getKey(addstage1.getStage(),file.getAbsolutePath()));      //overwrite同路径文件
            }
            addstage1.put(blob.getId(),file.getAbsolutePath());
            Commit currCommit = readCurrCommmit();              //如果commit里有和要add的文件一样的blob，将其从暂存区里删除
            if(currCommit.getPathToBlobID().containsKey(blob.getId())){
                addstage1.remove(blob.getId());
            }
            Utils.writeObject(addstage, addstage1);      //序列化写进其中
            Utils.writeObject(removestage,removestage1);
        }catch (GitletException excp) {
            System.out.println(excp.getMessage());
        }
    }
    public static void commit(String message){
        try{
            String currentBranchPath = Utils.readContentsAsString(HEAD);
            File currentBranch = new File(currentBranchPath);
            String parentCommitId = Utils.readContentsAsString(currentBranch);
            Commit parentCommit = readCommitByID(parentCommitId);
            String parentId = parentCommit.getId();
            ArrayList<String> parents = new ArrayList<>();
            parents.add(parentId);
            TreeMap<String, String> pathToBlobId = parentCommit.getPathToBlobID();       //parentCommit中的
               //addstage中的内容treemap的decorator,此处需要确定是老的还是新的时候
            Stage addstage1;
            Stage removestage1;
            if(!addstage.exists()){
                addstage1 = new Stage();
            }else{
                addstage1 = readObject(addstage, Stage.class);    //建立一个Stage类来装饰map来维护stage
            }
            if(!removestage.exists()){
                removestage1 = new Stage();
            }else{
                removestage1 = readObject(removestage, Stage.class);    //建立一个Stage类来装饰map来维护stage
            }

            if(message.equals("")){
                throw new GitletException("Please enter a commit message.");
            }
            if(addstage1.isEmpty() && removestage1.isEmpty()){   //错误打印
                throw new GitletException("No changes added to the commit.");
            }
            for(String k:addstage1.keySet()){
                if(pathToBlobId.containsValue(addstage1.get(k))){           //此处容易忽略要overwrite
                    pathToBlobId.remove(getKey(pathToBlobId,addstage1.get(k)));
                    pathToBlobId.put(k,addstage1.get(k));
                }else{
                    pathToBlobId.put(k,addstage1.get(k));
                }
            }
            for(String k:removestage1.keySet()){
                pathToBlobId.remove(k);
            }
            Commit newCommit = new Commit(message,parents,new Date(),pathToBlobId);     //新commit
            Utils.writeObject(addstage,new Stage());               //清空缓存区
            Utils.writeObject(removestage,new Stage());
            File newcommit = join(commits,newCommit.getId());
            Utils.writeObject(newcommit,newCommit);               //写入序列化commit文件
            Utils.writeContents(currentBranch,newCommit.getId());         //写入当前分支最新的commitId来找到commit文件


        }catch (GitletException excp) {
            System.out.println(excp.getMessage());
        }
    }
    public static void rm(String filename){
        File file = getFileFromCWD(filename);
        try{
            Commit currentCommit =readCurrCommmit();
            TreeMap<String, String> pathToBlobId = currentCommit.getPathToBlobID();
            Stage addstage1;
            Stage removestage1;
            if(!addstage.exists()){
                addstage1 = new Stage();
            }else{
                addstage1 = readObject(addstage, Stage.class);    //建立一个Stage类来装饰map来维护stage
            }
            if(!removestage.exists()){
                removestage1 = new Stage();
            }else{
                removestage1 = readObject(removestage, Stage.class);    //建立一个Stage类来装饰map来维护stage
            }
            Blob blob;
            if(file.exists()) {
                byte[] bytes = Utils.readContents(file);
                blob = new Blob(bytes, file, file.getPath());
            }else{
                blob = getBlob(getKey(pathToBlobId,file.getAbsolutePath()));
            }

            String Id = blob.getId();
            if(addstage1.containsKey(Id) && !pathToBlobId.containsKey(Id)){
                addstage1.remove(Id);
            } else if(pathToBlobId.containsKey(Id) ){
                removestage1.put(Id,file.getAbsolutePath());
                Utils.restrictedDelete(file);           //包含两种情况，文件存在返回true，并删除，不存在返回false
            }else{
                throw new GitletException("No reason to remove the file.");
            }
            Utils.writeObject(addstage,addstage1);
            Utils.writeObject(removestage,removestage1);
        }catch (GitletException excp) {
            System.out.println(excp.getMessage());
        }
    }
    //log
    private static Commit readCurrCommmit() {
        String currCommmitID = readCurrCommmitID();
        File CURR_COMMIT_FILE = join(commits, currCommmitID);
        return readObject(CURR_COMMIT_FILE, Commit.class);
    }
    private static String readCurrCommmitID() {
        File HEADS_FILE = join(heads, readCurrBranch());
        return Utils.readContentsAsString(HEADS_FILE);
    }
    private static String readCurrBranch() {
        String branchPath = Utils.readContentsAsString(HEAD);
        File currBranch = new File(branchPath);
        return currBranch.getName();
    }


    public static void log() {
        Commit currCommit = readCurrCommmit();
        while (!currCommit.getParentsCommitID().isEmpty()) {
            if (isMergeCommit(currCommit)) {
                printMergeCommit(currCommit);
            } else {
                printCommit(currCommit);
            }
            ArrayList<String> parentsCommitID = currCommit.getParentsCommitID();
            currCommit = readCommitByID(parentsCommitID.get(0));
        }
        printCommit(currCommit);
    }

    private static boolean isMergeCommit(Commit currCommmit) {
        return currCommmit.getParentsCommitID().size() == 2;
    }

    private static void printCommit(Commit currCommmit) {
        System.out.println("===");
        printCommitID(currCommmit);
        printCommitDate(currCommmit);
        printCommitMessage(currCommmit);
    }

    private static void printMergeCommit(Commit currCommmit) {
        System.out.println("===");
        printCommitID(currCommmit);
        printMergeMark(currCommmit);
        printCommitDate(currCommmit);
        printCommitMessage(currCommmit);
    }

    private static void printCommitID(Commit currCommmit) {
        System.out.println("commit " + currCommmit.getId());
    }

    private static void printMergeMark(Commit currCommmit) {
        List<String> parentsCommitID = currCommmit.getParentsCommitID();
        String parent1 = parentsCommitID.get(0);
        String parent2 = parentsCommitID.get(1);
        System.out.println("Merge: " + parent1.substring(0, 7) + " " + parent2.substring(0, 7));
    }

    private static void printCommitDate(Commit currCommmit) {
        System.out.println("Date: " + currCommmit.getTimeStamp());
    }

    private static void printCommitMessage(Commit currCommmit) {
        System.out.println(currCommmit.getMessage() + "\n");
    }

    private static Commit readCommitByID(String commitID) {
        if (commitID.length() == 40) {
            File CURR_COMMIT_FILE = join(commits, commitID);
            if (!CURR_COMMIT_FILE.exists()) {
                return null;
            }
            return readObject(CURR_COMMIT_FILE, Commit.class);
        } else {
            List<String> objectID = plainFilenamesIn(commits);
            for (String o : objectID) {
                if (commitID.equals(o.substring(0, commitID.length()))) {
                    return readObject(join(commits, o), Commit.class);
                }
            }
            return null;
        }
    }
    //global-log
    public static void global_log() {
        List<String> commitList = Utils.plainFilenamesIn(commits);
        Commit commit;
        for (String id : commitList) {
            try {
                commit = readCommitByID(id);
                if (isMergeCommit(commit)) {
                    printMergeCommit(commit);
                } else {
                    printCommit(commit);
                }
            } catch (Exception ignore) {
            }
        }
    }
    //find
    public static void find(String findMessage) {
        try {
            List<String> commitList = Utils.plainFilenamesIn(commits);
            List<String> idList = new ArrayList<String>();
            Commit commit;
            for (String id : commitList) {
                try {
                    commit = readCommitByID(id);
                    if (findMessage.equals(commit.getMessage())) {
                        idList.add(id);
                    }
                } catch (Exception ignore) {
                }
            }
            if (idList.isEmpty()) {
                throw new GitletException("Found no commit with that message.");
            } else {
                for (String id : idList) {
                    System.out.println(id);
                }
            }
        }catch (GitletException excp) {
            System.out.println(excp.getMessage());
        }
    }
    //status
    public static void status() {
        printBranches();
        printStagedFile();
        printRemovedFiles();
        printModifiedNotStagedFile();
        printUntrackedFiles();
    }

    private static void printBranches() {
        List<String> branchList = Utils.plainFilenamesIn(heads);
        String currBranch = readCurrBranch();
        System.out.println("=== Branches ===");
        System.out.println("*" + currBranch);
        if (branchList.size() > 1) {
            for (String branch : branchList) {
                if (!branch.equals(currBranch)) {
                    System.out.println(branch);
                }
            }
        }
        System.out.println();
    }

    private static void printStagedFile() {
        System.out.println("=== Staged Files ===");
        Stage addstage1;
        if(!addstage.exists()){
            addstage1 = new Stage();
        }else{
            addstage1 = readObject(addstage, Stage.class);    //建立一个Stage类来装饰map来维护stage
        }
        for (String id: addstage1.keySet()) {
            File blob = Utils.join(blobs,id);
            Blob b = readObject(blob,Blob.class);
            System.out.println(b.getFileName());
        }
        System.out.println();
    }

    private static void printRemovedFiles() {
        System.out.println("=== Removed Files ===");
        Stage removestage1;
        if(!removestage.exists()){
            removestage1 = new Stage();
        }else{
            removestage1 = readObject(removestage, Stage.class);    //建立一个Stage类来装饰map来维护stage
        }
        for (String id:removestage1.keySet()) {
            File blob = Utils.join(blobs,id);
            Blob b = readObject(blob,Blob.class);
            System.out.println(b.getFileName());
        }
        System.out.println();
    }

    private static void printModifiedNotStagedFile() {
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
    }

    private static void printUntrackedFiles() {
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }
    //checkout 共有三种情况
    /* 情况1*/
    public static void checkMistake(String branchName) {
        String currbranch = readCurrBranch();
        File currBranch = join(heads,currbranch);
        File newBranch = join(heads,branchName);
        try {
            if(!newBranch.exists()){
                throw new GitletException("No such branch exists.");
            }
            if (branchName.equals(currBranch.getName())) {
                throw new GitletException("No need to checkout the current branch.");
            }
        }catch (GitletException excp){
            System.out.println(excp.getMessage());
            System.exit(0);
        }
    }
    public static void checkoutBranch(String branchName){
        checkMistake(branchName);
        Commit oldCommit = readCurrCommmit();
        File currBranch = Utils.join(heads,branchName);
        Utils.writeContents(HEAD,currBranch.getAbsolutePath());     //更改当前指向branch
        Commit newCommit = readCurrCommmit();
        changeCommitTo(oldCommit,newCommit);
    }
    private static void changeCommitTo(Commit oldCommit,Commit newCommit){          //由commit更改CWD的文件
        TreeMap<String, String> newBlobs = newCommit.getPathToBlobID();
        TreeMap<String, String> oldBlobs = oldCommit.getPathToBlobID();
        //获取路径
        List<String> newPathList = new ArrayList<>();
        List<String> oldPathList = new ArrayList<>();
        List<String> bothPathList = new ArrayList<>();
        for(String newId: newBlobs.keySet()){
            Blob newBlob = getBlob(newId);
            newPathList.add(newBlob.getfilePath());
        }
        for(String oldId: oldBlobs.keySet()){
            Blob oldBlob = getBlob(oldId);
            oldPathList.add(oldBlob.getfilePath());
        }
        for(String path: newPathList){
            if(oldPathList.contains(path)){
                bothPathList.add(path);
            }
        }
        try {
            for (String path : newPathList) {
                if (bothPathList.contains(path)) {
                    oldPathList.remove(path);
                    String newId = getKey(newBlobs, path);
                    Blob newBlob = getBlob(newId);
                    String oldId = getKey(oldBlobs, path);
                    Blob oldBlob = getBlob(oldId);
                    if (newId == oldId) {
                        continue;
                    } else {
                        File file = new File(path);
                        Utils.writeContents(file, new String(newBlob.getBytes(), StandardCharsets.UTF_8));
                    }
                } else {
                    String newId = getKey(newBlobs, path);
                    Blob newBlob = getBlob(newId);
                    File file = new File(path);
                    if (file.exists()) {
                        throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first.");
                    }
                    Utils.writeContents(file, new String(newBlob.getBytes(), StandardCharsets.UTF_8));
                }
            }
            for (String path : oldPathList) {
                File file = new File(path);
                Utils.restrictedDelete(file);
            }
            Utils.writeObject(addstage,new Stage());               //清空缓存区
            Utils.writeObject(removestage,new Stage());
        }catch (GitletException excp){
            System.out.println(excp.getMessage());
            System.exit(0);
        }
    }
    private static String getKey(TreeMap<String, String> map, String value){
        for(String key: map.keySet()){
            if(map.get(key).equals(value)){
                return key;
            }
        }
        return null;
    }
    private static Blob getBlob(String Id){
        File file = Utils.join(blobs,Id);
        return Utils.readObject(file,Blob.class);
    }
    /*第二种情况*/
    public static void checkout(String filename){
        File file = getFileFromCWD(filename);
        Commit currCommit = readCurrCommmit();
        TreeMap<String, String> Blobs = currCommit.getPathToBlobID();
        try{
            if(!Blobs.containsValue(file.getAbsolutePath())){
                throw new GitletException("File does not exist in that commit.");
            }
        }catch (GitletException excp){
            System.out.println(excp.getMessage());
            System.exit(0);
        }
        String Id = getKey(Blobs,file.getAbsolutePath());
        Blob blob = getBlob(Id);
        Utils.writeContents(file, new String(blob.getBytes(), StandardCharsets.UTF_8));

    }
    /*第3种情况*/
    public static void checkout(String commitId,String filename){
        Commit commit = readCommitByID(commitId);
        try{
            if(commit == null){
                throw new GitletException("No commit with that id exists.");
            }
        }catch (GitletException excp){
            System.out.println(excp.getMessage());
            System.exit(0);
        }
        File file = getFileFromCWD(filename);
        TreeMap<String, String> Blobs = commit.getPathToBlobID();
        try{
            if(!Blobs.containsValue(file.getAbsolutePath())){
                throw new GitletException("File does not exist in that commit.");
            }
        }catch (GitletException excp){
            System.out.println(excp.getMessage());
            System.exit(0);
        }
        String Id = getKey(Blobs,file.getAbsolutePath());
        Blob blob = getBlob(Id);
        Utils.writeContents(file, new String(blob.getBytes(), StandardCharsets.UTF_8));
    }

    public static void branch(String branchName){
        List<String> allBranches = Utils.plainFilenamesIn(heads);
        try {
            if (allBranches.contains(branchName)) {
                throw new GitletException("A branch with that name already exists.");
            }
        }catch (GitletException excp){
            System.out.println(excp.getMessage());
            System.exit(0);
        }
        File file = Utils.join(heads,branchName);
        String Id = readCurrCommmitID();
        Utils.writeContents(file,Id);
    }


    public static void rm_branch(String branchName) {
        List<String> allBranches = Utils.plainFilenamesIn(heads);
        try {
            if (!allBranches.contains(branchName)) {
                throw new GitletException("A branch with that name does not exist.");
            }
            if (branchName.equals(readCurrBranch())){
                throw new GitletException("Cannot remove the current branch.");
            }
        }catch (GitletException excp){
            System.out.println(excp.getMessage());
            System.exit(0);
        }
        File file = Utils.join(heads,branchName);
        file.delete();          //restrictedDelete只能删除CWD里的file
    }


    public static void reset(String commitId) {
        List<String> allCommits = Utils.plainFilenamesIn(commits);
        try {
            if (!allCommits.contains(commitId)) {
                throw new GitletException("No commit with that id exists.");
            }
        }catch (GitletException excp){
            System.out.println(excp.getMessage());
            System.exit(0);
        }
        Commit currCommit = readCurrCommmit();
        Commit newCommit = readCommitByID(commitId);
        String currBranch = readCurrBranch();
        File branchFile = Utils.join(heads, currBranch);
        Utils.writeContents(branchFile, commitId);
        changeCommitTo(currCommit,newCommit);
    }
    /*merge function*/

    public static void merge(String branchName) {
        String currBranch = readCurrBranch();
        Commit currCommit = readCurrCommmit();
        try {
            Stage addStage = Utils.readObject(addstage,Stage.class);
            Stage removeStage = Utils.readObject(removestage, Stage.class);
            if (!(addStage.isEmpty() && removeStage.isEmpty())) {
                throw new GitletException("You have uncommitted changes.");
            }
            if(currBranch.equals(branchName)){
                throw new GitletException("Cannot merge a branch with itself.");
            }
            List<String> allBranches = Utils.plainFilenamesIn(heads);
            if (!allBranches.contains(branchName)) {
                throw new GitletException("A branch with that name does not exist.");
            }
        }catch (GitletException excp){
            System.out.println(excp.getMessage());
            System.exit(0);
        }
        Commit mergeCommit = readCommitByBranchName(branchName);
        Commit splitPoint = findSplitPoint(currCommit,mergeCommit);
        //两种简易情况
        if (splitPoint.getId().equals(mergeCommit.getId())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        if (splitPoint.getId().equals(currCommit.getId())) {
            System.out.println("Current branch fast-forwarded.");
            checkoutBranch(branchName);
            System.exit(0);
        }
        //以下为较为棘手的合并情况
        String message = "Merged " + branchName + " into " + currBranch + ".";
        Commit mergedCommit = mergeFilesToNewCommit(splitPoint, currCommit, mergeCommit,message);
        //保存commit
        File file = join(commits,mergedCommit.getId());
        Utils.writeObject(file,mergedCommit);               //写入序列化commit文件
        File branchFile = join(heads,currBranch);
        Utils.writeContents(branchFile,mergedCommit.getId());//保存commit
        changeCommitTo(currCommit,mergedCommit);    //代码复用checkoutBranch的子函数

    }

    private static Commit mergeFilesToNewCommit(Commit splitPoint, Commit currCommit, Commit mergeCommit,String message) {
        String currCommitID = currCommit.getId();
        String mergeCommitID = mergeCommit.getId();
        ArrayList<String> parents = new ArrayList<>(List.of(currCommitID, mergeCommitID));
        TreeMap<String,String> allFiles = new TreeMap<>();
        allFiles.putAll(splitPoint.getPathToBlobID());
        allFiles.putAll(currCommit.getPathToBlobID());
        allFiles.putAll(mergeCommit.getPathToBlobID());
        TreeMap<String,String> splitMap = splitPoint.getPathToBlobID();
        TreeMap<String,String> currMap = currCommit.getPathToBlobID();
        TreeMap<String,String> mergeMap = mergeCommit.getPathToBlobID();
        TreeMap<String,String> mergedMap = new TreeMap<>();
        boolean ifConflict = false;
        for(String Id: allFiles.keySet()){
            String path = allFiles.get(Id);
            String splitId = getKey(splitMap,path);
            String currId = getKey(currMap,path);
            String mergeId = getKey(mergeMap,path);
            //部分条件冗余，但怕出错,这里equal和==的使用要当心，条件顺序要当心
            //merge和merged不一样阿艹，de了好久bug
            if (mergeId != null && mergeId.equals(splitId) && currId == null){
                continue;           //case 7
            }else if(currId != null && currId.equals(splitId)  && mergeId == null){
                continue;           //case 6
            }else if(splitId==null && mergeId == null && currId != null){
                mergedMap.put(currId,path);         //case 4
            }else if(splitId == null && currId == null && mergeId != null){
                mergedMap.put(mergeId,path);        //case 5
            }else if(currId != null && mergeId!=null && currId.equals(splitId) && !mergeId.equals(currId)){
                mergedMap.put(mergeId,path);        //case 1,此处仍然有可能mergeId不存在
            }else if(currId != null && mergeId!=null && mergeId.equals(splitId) && !currId.equals(mergeId)){
                mergedMap.put(currId,path);         //case 2，此处仍然有可能currId不存在
            }else if(currId != null && mergeId!=null && !mergeId.equals(splitId) && mergeId.equals(currId)){
                mergedMap.put(currId,path);         //case3无conflict
            }else if(currId == null && mergeId == null && splitId != null){
                continue;                           //两个都是null有可能
            }else{
                ifConflict = true;
                String currBranchContents;
                String givenBranchContents;
                if(currId == null){                     //一个不存在一个修改过也算conflict，把这种情况包括进去
                    currBranchContents = "";
                }else {
                    Blob currBlob = getBlob(currId);
                    currBranchContents = new String(currBlob.getBytes(), StandardCharsets.UTF_8);
                }
                if(mergeId == null){
                    givenBranchContents = "";
                }else {
                    Blob mergeBlob = getBlob(mergeId);
                    givenBranchContents = new String(mergeBlob.getBytes(), StandardCharsets.UTF_8);
                }
                String conflictContents = "<<<<<<< HEAD\n" + currBranchContents + "=======\n" + givenBranchContents + ">>>>>>>\n";
                File conflictFile = new File(path);
                Blob blob = new Blob(conflictContents.getBytes(),conflictFile,path);        //忘记saveblob了
                File file = join(blobs,blob.getId());
                Utils.writeObject(file,blob);
                mergedMap.put(blob.getId(),path);
            }

        }
        if(ifConflict){
            System.out.println("Encountered a merge conflict.");
        }
        Commit newCommit = new Commit(message,parents,new Date(),mergedMap);
        return newCommit;
    }

    private static Commit readCommitByBranchName(String branchName) {
        File branchFileName = join(heads, branchName);
        String newCommitID = Utils.readContentsAsString(branchFileName);
        return readCommitByID(newCommitID);
    }
    private static Commit findSplitPoint(Commit commit1, Commit commit2) {
        Map<String, Integer> commit1IDToLength = caculateCommitMap(commit1, 0);
        Map<String, Integer> commit2IDToLength = caculateCommitMap(commit2, 0);
        return caculateSplitPoint(commit1IDToLength, commit2IDToLength);
    }

    private static Map<String, Integer> caculateCommitMap(Commit commit, int length) {
        Map<String, Integer> map = new HashMap<>();
        if (commit.getParentsCommitID().isEmpty()) {
            map.put(commit.getId(), length);
            return map;
        }
        map.put(commit.getId(), length);
        length++;
        for (String id : commit.getParentsCommitID()) {     //保证数据完整，为一条路即可，有替代重复问题不大，因为找的时候是按一条路来的，重叠的length用哪条路都没关系，数据有同一性
            Commit parent = readCommitByID(id);
            map.putAll(caculateCommitMap(parent, length));
        }
        return map;
    }

    private static Commit caculateSplitPoint(Map<String, Integer> map1, Map<String, Integer> map2) {
        int minLength = Integer.MAX_VALUE;
        String minID = "";
        for (String id : map1.keySet()) {
            if (map2.containsKey(id) && map1.get(id) < minLength) {
                minID = id;
                minLength = map1.get(id);
            }
        }
        return readCommitByID(minID);
    }

}
