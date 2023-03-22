package edu.up.tripvisualizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * This class allows to ganerate the map with first and last image (with help of the sorted List of objects) and the final map using
 * all the positions (with help of the sorted List of objects). The map are created using mapquest,
 * its basically a request -> save image and create video process
 */
public class mapGenerator {
    /**
     * Obtains the first and last position and turns each one into its decimal form, then request the image to mapquest and saves it
     * @param position1
     * @param position2
     */
    public void generateFirstMap(String position1, String position2){
        System.out.println("Generating First Map...");
        //Position1
        String[] coordinates = position1.split(",");
        String[] latitude = coordinates[0].split(" ");
        String[] longitude = coordinates[1].split(" ");

        String latitude1 = Double.toString(Double.parseDouble(latitude[0])+Double.parseDouble(latitude[2].substring(0,latitude[2].length()-1))/60+Double.parseDouble(latitude[3].substring(0,latitude[3].length()-1))/3600);
        if(latitude[4].contains("S")){
            latitude1="-"+latitude1;
        }

        String longitude1 = Double.toString(Double.parseDouble(longitude[1])+Double.parseDouble(longitude[3].substring(0,longitude[3].length()-1))/60+Double.parseDouble(longitude[4].substring(0,longitude[4].length()-1))/3600);
        if(longitude[5].contains("W")){
            longitude1="-"+longitude1;
        }

        //Position2
        String[] coordinatesFinal = position2.split(",");
        String[] latitudeFinal = coordinatesFinal[0].split(" ");
        String[] longitudeFinal = coordinatesFinal[1].split(" ");
        String latitude2 = Double.toString(Double.parseDouble(latitudeFinal[0])+Double.parseDouble(latitudeFinal[2].substring(0,latitudeFinal[2].length()-1))/60+Double.parseDouble(latitudeFinal[3].substring(0,latitudeFinal[3].length()-1))/3600);
        if(latitudeFinal[4].contains("S")){
            latitude2="-"+latitude2;
        }

        String longitude2 = Double.toString(Double.parseDouble(longitudeFinal[1])+Double.parseDouble(longitudeFinal[3].substring(0,longitudeFinal[3].length()-1))/60+Double.parseDouble(longitudeFinal[4].substring(0,longitudeFinal[4].length()-1))/3600);
        if(longitudeFinal[5].contains("W")){
            longitude2="-"+longitude2;
        }

        //Request Image
        String mapLink = "https://www.mapquestapi.com/staticmap/v5/map?start="+latitude1+","+longitude1+"&end="+latitude2+","+longitude2+"&routeArc=true&size=1920,1080@2x&key=8ZJCiitpZTJA94PeoOovVRI0ABak6ugU";
        try {
            URL url = new URL(mapLink);
            BufferedImage image = ImageIO.read(url);
            saveImageAndVideo(image,"Map","jpg");
        }catch (Exception e){
            System.out.println(e);
        }
    }

    /**
     * Obtains all the positions and turns each one to its decimal form, then requests the image to mapquest and saves it
     * @param positions
     */
    public void generateManyPointsMap(List<multimediaFile> positions){
        System.out.println("Generating Second Map..");
        StringBuilder locations = new StringBuilder();
        for(multimediaFile position:positions){
            String[] coordinates = position.getPosition().split(",");
            String[] latitude = coordinates[0].split(" ");
            String[] longitude = coordinates[1].split(" ");

            String latitudePoints = Double.toString(Double.parseDouble(latitude[0])+Double.parseDouble(latitude[2].substring(0,latitude[2].length()-1))/60+Double.parseDouble(latitude[3].substring(0,latitude[3].length()-1))/3600);
            if(latitude[4].contains("S")){
                latitudePoints="-"+latitudePoints;
            }

            String longitudePoints = Double.toString(Double.parseDouble(longitude[1])+Double.parseDouble(longitude[3].substring(0,longitude[3].length()-1))/60+Double.parseDouble(longitude[4].substring(0,longitude[4].length()-1))/3600);
            if(longitude[5].contains("W")){
                longitudePoints="-"+longitudePoints;
            }
            locations.append(latitudePoints);
            locations.append(",");
            locations.append(longitudePoints);
            locations.append("||");
        }
        String totalLocations = locations.toString().substring(0,locations.length()-3);
        String mapLink = "https://www.mapquestapi.com/staticmap/v5/map?locations="+totalLocations+"&size=1920,1080@2x&key=8ZJCiitpZTJA94PeoOovVRI0ABak6ugU";
        try {
            URL url = new URL(mapLink);
            BufferedImage image = ImageIO.read(url);
            saveImageAndVideo(image,"MapManyLocations","jpg");
        }catch (Exception e){
            System.out.println(e);
        }
    }

    /**
     * Receives the image generated via mapquest and turns it into a video
     * @param image
     * @param filename
     * @param extension
     */
    public void saveImageAndVideo(BufferedImage image, String filename, String extension){
        File outputImage = new File("./Maps/"+filename+"."+extension);
        try{
            ImageIO.write(image, extension, outputImage);
            commandExecuter.executeCommandForVideo("ffmpeg -loop 1 -i "+"../../Maps/"+filename+"."+extension+" -c:v libx264 -t 3 -pix_fmt yuv420p -vf scale=1920:1080 ../../videosMerge/"+filename+".mkv","./ffmpeg/bin");
        } catch (IOException | InterruptedException ioe){
            ioe.printStackTrace();
        }
    }
}
