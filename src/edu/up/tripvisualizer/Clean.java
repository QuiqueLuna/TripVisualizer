package edu.up.tripvisualizer;

import java.io.File;

/**
 * Helps clean folders between runs
 */
public class Clean {
    /**
     * Deletes all files from the given folder
     * @param dir
     */
    public static void cleanFolder(File dir){
        for (File file: dir.listFiles()) {
            if (!file.isDirectory())
                file.delete();
        }
    }
}
