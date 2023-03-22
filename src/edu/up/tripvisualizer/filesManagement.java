package edu.up.tripvisualizer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class deals with all the information usage, which allow us to extract the metadata from the files, sorting this info,
 * create videos for merge (via ffmpeg) and and generating the final text file to mix the videos (via concat demuxer)
 */
public class filesManagement {
    /**
     * Extracts all the needed info for generating multimediqFile objects traversing the String it receives
     * If a file doesn't have position a default one would be assigned
     * @param files
     * @return
     */
    public List<multimediaFile> extractInfo(String[] files) {
        System.out.println("Extracting Information of images/videos...");
        List<multimediaFile> multimediaFiles = new ArrayList<>();
        for (String file:files) {
            Boolean check = true;
            String[] data = file.split("\n");
            String name="",date="",position="",width="",height="";
            for(String info:data){
                if(info.contains("File Name")){
                    //System.out.println(info);
                    name=info.substring(34);
                }else if(info.contains("Create Date")&&check) {
                    //System.out.println(info);
                    date=info.substring(34);
                    check=false;
                }else if(info.contains("GPS Position")){
                    //System.out.println(info);
                    position=info.substring(34);
                }else if(info.contains("Image Width")){
                    //System.out.println(info);
                    width=info.substring(34);
                }else if(info.contains("Image Height")){
                    //System.out.println(info);
                    height=info.substring(34);
                }
            }
            if(name!=""){
                if(position==""){
                    //If the image doesn't have position, a default position would be assigned
                    position="0 deg 0' 0.0\" N, 0 deg 0' 0.0\" E";
                }
                multimediaFile multimediaFile = new multimediaFile(name,date,position,width,height);
                multimediaFiles.add(multimediaFile);
            }
        }
        return multimediaFiles;
    }

    /**
     * Mix the date into a Double number which help sorting the Object array using bubble sort
     * @param multimediaFiles
     * @return
     */
    public List<multimediaFile> sortByDate(List<multimediaFile> multimediaFiles){
        System.out.println("Sorting by Date...");
        for(int i=0;i<multimediaFiles.size();i++){
            for (int j = 0; j < multimediaFiles.size()-i-1; j++) {
                String date1 = multimediaFiles.get(j).getDate().substring(0,4)+multimediaFiles.get(j).getDate().substring(5,7)+multimediaFiles.get(j).getDate().substring(8,10)+multimediaFiles.get(j).getDate().substring(11,13)+multimediaFiles.get(j).getDate().substring(14,16)+multimediaFiles.get(j).getDate().substring(17);
                String date2 = multimediaFiles.get(j+1).getDate().substring(0,4)+multimediaFiles.get(j+1).getDate().substring(5,7)+multimediaFiles.get(j+1).getDate().substring(8,10)+multimediaFiles.get(j+1).getDate().substring(11,13)+multimediaFiles.get(j+1).getDate().substring(14,16)+multimediaFiles.get(j+1).getDate().substring(17);
                double firstDate = Double.parseDouble(date1);
                double lastDate = Double.parseDouble(date2);
                if(firstDate>lastDate){
                    multimediaFile aux = multimediaFiles.get(j);
                    multimediaFiles.set(j, multimediaFiles.get(j+1));
                    multimediaFiles.set(j+1,aux);
                }
            }
        }
        return  multimediaFiles;
    }

    /**
     * Generates a video for each image and a video with different encoding for each video
     * It supports a certain types of files
     * Images (jpg,jpeg,png,jfif,bmp,raw,tif)
     * Video (mp4,mov,avi,flv,mkv,wmv,avchd,webm)
     * For a different format the if needs to be modified
     * @param multimediaFiles
     * @throws IOException
     * @throws InterruptedException
     */
    public void generateVideosForMerge(List<multimediaFile> multimediaFiles) throws IOException, InterruptedException {
        System.out.println("Generating Videos...");
        for (multimediaFile file:multimediaFiles) {
            if(file.getFileName().contains(".jpg")||file.getFileName().contains(".jpeg")||file.getFileName().contains(".jfif")||file.getFileName().contains(".png")||file.getFileName().contains(".bmp")||file.getFileName().contains(".tif")||file.getFileName().contains(".raw")){
                String name = file.getFileName().split("\\.")[0];
                System.out.println("Generating "+file.getFileName()+" video...");
                //Use this command instead of the command below if problems occur in the generated video
                //commandExecuter.executeCommandForVideo("ffmpeg -loop 1 -i "+"../../Trip/"+f.getFileName()+" -c:v libx264 -t 3 -pix_fmt yuv420p -vf scale=1920:1080 -s "+f.getWidth()+"x"+f.getHeight()+" ../../videosMerge/"+name+".mkv","./ffmpeg/bin");
                commandExecuter.executeCommandForVideo("ffmpeg -loop 1 -i "+"../../Trip/"+file.getFileName()+" -c:v libx264 -t 3 -pix_fmt yuv420p -vf scale=1920:-2 "+" ../../videosMerge/"+name+".mkv","./ffmpeg/bin");
            }else if(file.getFileName().contains(".mp4")||file.getFileName().contains(".mov")||file.getFileName().contains(".avi")||file.getFileName().contains(".flv")||file.getFileName().contains(".mkv")||file.getFileName().contains(".wmv")||file.getFileName().contains(".avchd")||file.getFileName().contains(".webm")){
                String name = file.getFileName().split("\\.")[0];
                System.out.println("Re-encoding "+file.getFileName()+"...");
                commandExecuter.executeCommandForVideo("ffmpeg -i ../../Trip/"+file.getFileName()+" -vf scale=1920:1080 -map 0 -c copy -c:v libx264 ../../videosMerge/"+name+".mkv","./ffmpeg/bin");
                //commandExecuter.executeCommandForVideo("ffmpeg -i ../../Trip/"+f.getFileName()+" -vf scale=1920:1080 -c:v libx264 ../../videosMerge/"+name+".mp4","./ffmpeg/bin");
            }
        }
    }

    /**
     * Creates a text file with the final list of the videos to merge, this list is already sorted,
     * this file will help to generate the final video using concat demuxer
     * @param multimediaFiles
     */
    public void generateVideosListTxtFile(List<multimediaFile> multimediaFiles){
        System.out.println("Generating Text File...");
        try {
            File newTextFile = new File("./videosMerge/videosList.txt");
            FileWriter fw = new FileWriter(newTextFile);
            fw.write("file ../videosMerge/Map.mkv\n");
            for(multimediaFile f:multimediaFiles){
                String nameWithoutExtension = f.getFileName().split("\\.")[0];
                fw.write("file "+"../videosMerge/"+nameWithoutExtension+".mkv\n");
            }
            fw.write("file ../videosMerge/MapManyLocations.mkv");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the info from all the objects generated
     * @param multimediaFiles
     */
    public void printFilesInfo(List<multimediaFile> multimediaFiles){
        for (multimediaFile f:multimediaFiles) {
            System.out.println(f.getFileName());
            System.out.println(f.getDate());
            System.out.println(f.getPosition());
            System.out.println(f.getWidth());
            System.out.println(f.getHeight());
        }
    }

}
