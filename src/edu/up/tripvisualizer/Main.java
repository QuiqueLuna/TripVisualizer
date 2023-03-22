package edu.up.tripvisualizer;

import java.io.*;
import java.util.List;

/**
 * Pipeline of TripVidualizer
 * Author: Enrique Luna
 * The program reads all the multimedia files inside Trip folder,
 * saves the info inside a list of multimedia objects for sorting and easy access,
 * generate videos from images and does re-encode to the videos,
 * generate the images and videos of the initial(first and last positions) and final(all  positions) maps,
 * generates text file of all videos in order to concatenate
 * executes command to merge final video
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        //Directories that will be cleaned to guarantee the complete process.
        File maps = new File("./Maps");
        File videos = new File("./videosMerge");
        File finalVideo = new File("./finalVideo");

        //Clean the folders for previous multimedia
        Clean.cleanFolder(videos);
        Clean.cleanFolder(maps);
        Clean.cleanFolder(finalVideo);

        //Extract info of all multimedia files inside Trip folder
        String filesInfo = commandExecuter.executeCommand("exiftool ../Trip *.*","./exiftool");

        //Format to the information obtained
        String[] files = filesInfo.split("========");
        files[files.length-1]="";

        //Instantiate fileManager and creates a List of multimediaFiles Objects in order to access info in an easy way(help for sorting)
        filesManagement fileManager = new filesManagement();
        List<multimediaFile> multimediaFiles;
        multimediaFiles = fileManager.extractInfo(files);

        //Info of the files
        //fileManager.printFilesInfo(multimediaFiles);

        //Sort List
        multimediaFiles = fileManager.sortByDate(multimediaFiles);

        //Sorted info of the files by date
        //fileManager.printFilesInfo(multimediaFiles);

        //Generate videos from images and does re encoding of videos
        fileManager.generateVideosForMerge(multimediaFiles);

        //Instantiate mapGenerator and generates first and last map, using sorted List
        mapGenerator mapGenerator = new mapGenerator();
        mapGenerator.generateFirstMap(multimediaFiles.get(0).getPosition(),multimediaFiles.get(multimediaFiles.size()-1).getPosition());
        mapGenerator.generateManyPointsMap(multimediaFiles);

        //Generate text file of ordered videos list for concatenate using concat demuxer
        fileManager.generateVideosListTxtFile(multimediaFiles);

        //Generates the final Video inside finalVideo folder
        commandExecuter.executeCommandForVideo("ffmpeg -f concat -safe 0 -i ../../videosMerge/videosList.txt -c copy ../../finalVideo/output.mkv","./ffmpeg/bin");
        System.out.println("Final Video Generated In finalVideo Folder");
        System.out.println("Save it before another run, because each time the main runs it deletes the videosMerge,Maps and finalVideo Folders");
    }
}
