package edu.up.tripvisualizer;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Provides tools to help execute different type of commands, one to save the info received in console,
 * and other one to make a sync process generating multiple videos.
 */
public class commandExecuter {
    /**
     * Executes a command in an especific path and returns the what the console prints in String form
     * @param commandToExecute
     * @param path
     * @return
     */
    public static String executeCommand(String commandToExecute, String path){
        ProcessBuilder command = new ProcessBuilder();
        command.command("cmd.exe", "/c", commandToExecute);
        command.directory(new File(path));
        StringBuilder data = new StringBuilder();
        try {
            Process process = command.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                data.append(line);
                data.append("\n");
            }
            int exitCode = process.waitFor();
            //System.out.println("\nExited with error code : " + exitCode);
            return data.toString();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return  data.toString();
    }

    /**
     * Executes a command in an especific path one by one, this ensures that the final video could be created with no problem at all
     * since all videos will be ready to merge, this method also helps with deadlock that some commands could generate.
     * @param commandToExecute
     * @param path
     * @throws IOException
     * @throws InterruptedException
     */
    public static void executeCommandForVideo(String commandToExecute, String path) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("cmd.exe","/c",commandToExecute);
        pb.directory(new File(path));
        pb.redirectErrorStream(true);
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null)
            System.out.print("");
        process.waitFor();
    }

    /*
    public static void executeCommandForVideo(String commandToExecute, String path) throws IOException{
        ProcessBuilder command = new ProcessBuilder();
        command.command("cmd.exe", "/c", commandToExecute);
        command.directory(new File(path));
        Process process = command.start();
    }
     */
}
