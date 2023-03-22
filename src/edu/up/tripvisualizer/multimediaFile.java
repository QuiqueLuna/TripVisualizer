package edu.up.tripvisualizer;

/**
 * Allows us to create multimediaFile objects which help us to have an easier way to manage the info of each file inside the Trip folder,
 * these will also allow us to have a List with all the objects helplping us with the sorting and access of info.
 */
public class multimediaFile {
    private String fileName;
    private String date;
    private String position;
    private String width;
    private String height;

    public String getWidth() { return width; }
    public void setWidth(String width) { this.width = width; }

    public String getHeight() { return height; }
    public void setHeight(String height) { this.height = height; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public multimediaFile(String fileName, String date, String position,String width, String height){
        setFileName(fileName);
        setDate(date);
        setPosition(position);
        setWidth(width);
        setHeight(height);
    }


}
