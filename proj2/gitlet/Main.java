package gitlet;

import java.io.File;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        final File CWD = new File(System.getProperty("user.dir"));
        final File GITLET_FOLDER = Utils.join(".gitlet");
        try {
            if(args.length == 0){
                throw new GitletException("Please enter a command.");
            }
            String firstArg = args[0];
            if(!GITLET_FOLDER.exists() && !firstArg.equals("init")){
                throw new GitletException("Not in an initialized Gitlet directory.");
            }
            switch (firstArg) {
                case "init":
                    if(args.length != 1){
                        throw new GitletException("Incorrect operands.");
                    }
                    GitletRepository.init();
                    break;
                case "add":
                    // TODO: handle the `add [filename]` command
                    if(args.length != 2){
                        throw new GitletException("Incorrect operands.");
                    }
                    GitletRepository.add(args[1]);
                    break;
                case "commit":
                    if(args.length != 2){
                        throw new GitletException("Incorrect operands.");
                    }
                    GitletRepository.commit(args[1]);
                    break;
                case "rm":
                    //
                    if(args.length != 2){
                        throw new GitletException("Incorrect operands.");
                    }
                    GitletRepository.rm(args[1]);
                    break;
                case "log":
                    //
                    if(args.length != 1){
                        throw new GitletException("Incorrect operands.");
                    }
                    GitletRepository.log();
                    break;
                case "global-log":
                    //
                    if(args.length != 1){
                        throw new GitletException("Incorrect operands.");
                    }
                    GitletRepository.global_log();
                    break;
                case "find":
                    //
                    if(args.length != 2){
                        throw new GitletException("Incorrect operands.");
                    }
                    GitletRepository.find(args[1]);
                    break;
                case "status":
                    //
                    if(args.length != 1){
                        throw new GitletException("Incorrect operands.");
                    }
                    GitletRepository.status();
                    break;
                case "checkout":


                    switch (args.length) {
                        case 2:
                            /* * checkout [branch name] */
                            GitletRepository.checkMistake(args[1]);
                            GitletRepository.checkoutBranch(args[1]);
                            break;
                        case 3:
                            if (!args[1].equals("--")) {
                                throw new GitletException("Incorrect operands.");
                            }
                            /* * checkout -- [file name] */
                            GitletRepository.checkout(args[2]);
                            break;

                        case 4:
                            if (!args[2].equals("--")) {
                                throw new GitletException("Incorrect operands.");
                            }
                            /* * checkout [commit id] -- [file name] */
                            GitletRepository.checkout(args[1], args[3]);
                            break;
                        default:
                            throw new GitletException("Incorrect operands.");
                    }
                    break;
                case "branch":
                    //
                    if(args.length != 2){
                        throw new GitletException("Incorrect operands.");
                    }
                    GitletRepository.branch(args[1]);
                    break;
                case "rm-branch":
                    //
                    if(args.length != 2){
                        throw new GitletException("Incorrect operands.");
                    }
                    GitletRepository.rm_branch(args[1]);
                    break;
                case "reset":
                    //
                    if(args.length != 2){
                        throw new GitletException("Incorrect operands.");
                    }
                    GitletRepository.reset(args[1]);
                    break;
                case "merge":
                    //
                    if(args.length != 2){
                        throw new GitletException("Incorrect operands.");
                    }
                    GitletRepository.merge(args[1]);
                    break;
                default:
                    throw new GitletException("No command with that name exists.");

            }
        }catch (GitletException excp){
            System.out.println(excp.getMessage());
            System.exit(0);
        }
    }

}
