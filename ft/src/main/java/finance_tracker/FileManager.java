package finance_tracker;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;



/**
 * Class Name: FileManager
 * Credit: Pablo Bandera Lopez
 * Created: 03/31/2025
 * Modified: 04/01/2025
 * 
 * Description: Class deals with File I/O. Has three sets of methds:
 * FILE PATH & DIRECTORY: Used to construct strings pointing to the files needed to read and write and create and delete files. Uses Calendar to determine the current Month and Year.
 * PRINTING METHODS: Use PrintWriter to print out to a file based on the path given.
 * READING METHODS: Use Scanner to read information from the input document.
 * 
 * Attributes:
 * - month: String
 * - year: String
 * - C: Calendar
 * 
 * Methods:
 * + <<constructor>>FileManager()
 * + getCurrentMonth(): String
 * + getCurrentYear(): String
 * + getFilePath(String, String): String
 * + getDirectoryPath(String): String
 * + filePathExists(String, String): boolean
 * + directoryExists(String): boolean
 * + makeDir(String): void
 * + makeFile(String): void
 * + rmvFile(String): void
 * + updateFile(Entries): void
 * + readEntries(String): Entries
 * + entryStringToEntry(String): Entry
 * + getNumberOfEntries(String): int
 * 
 */
public class FileManager {
    
    /*Attributes*/
    private String month, year;
    final private static Calendar C = Calendar.getInstance();

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public FileManager()//Constructor
    {   
        
        String m = this.getCurrentMonth();
        String y = this.getCurrentYear();
        if(m != null && !m.isEmpty()){this.month = m;}
        if(y != null && !y.isEmpty()) {this.year  = y;}
        
    }


    /* FILE PATH & DIRECTORY METHODS */
    public String getCurrentMonth()
    {
        int monthInt = C.get(Calendar.MONTH);
        DateFormatSymbols symbols = new DateFormatSymbols();
        String [] months = symbols.getMonths();
        return months[monthInt];
    }

    public String getCurrentYear()
    {
        int yearInt = C.get(Calendar.YEAR);
        return String.valueOf(yearInt);
    }

    public String getFilePath(String m, String y)
    {
        String path = "./Files/"+y+"/"+m+".txt";
        return path;
    }

    public String getDirectoryPath(String y)
    {
        String path = "./Files/"+y;
        return path;
    }
    
    public boolean filePathExists(String m, String y) {return Files.exists(Paths.get(getFilePath(m, y)));}
    public boolean directoryExists(String y) {return Files.exists(Paths.get(getDirectoryPath(y)));}

    public void makeDir(String path)
    {
        File f = new File(path);
        f.mkdirs();
    }

    public void makeFile(String path)
    {
        File f = new File(path);
        try{f.createNewFile();}
        catch(IOException e){JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);}
    }

    public void rmvFile(String path)
    {
        File f = new File(path);
        try{f.delete();}catch(Exception e){}
    }



    /* PRINTING METHODS */
    public void updateFile(Entries entries)//Updates the file with the current entries
    {
    
        try (PrintWriter pw = new PrintWriter(new File(getFilePath(month, year)))) {
            for(Entry e : entries.getEntries())
            {
                pw.println(e.toString());
            }
        } catch (FileNotFoundException e) 
        {
            JOptionPane.showMessageDialog(null, e.toString(), "Error: File Not Found", JOptionPane.ERROR_MESSAGE);
        }
        
    }


    /* READING METHODS */
    public Entries readEntries(String path)//Loads the file into the entries
    {
        try (Scanner s = new Scanner(new File(path));){
            String entryString;
            Entry e;
            Entries entries = new Entries();
            
            while(s.hasNextLine())
            {
                entryString = s.nextLine();
                try{
                    e = entryStringToEntry(entryString);
                    entries.addEntryNoUpdate(e);
                    
                }catch(FileFormatException ex){}
            }
            s.close();
            return entries;
        } catch (FileNotFoundException ex) 
        {
            JOptionPane.showMessageDialog(null, ex.toString(), "Error: File Not Found", JOptionPane.ERROR_MESSAGE);
        }
        return null;
        
    }

    public Entry entryStringToEntry(String s) throws FileFormatException
    {

        String [] tokens = s.split(",");
        if(tokens.length < 10){throw new FileFormatException("Error Reading File");}
        try
        {
            Entry e  = new Entry(Integer.parseInt(tokens[0]));
            e.setAmount(Double.parseDouble(tokens[1]));
            e.setType(Integer.parseInt(tokens[2]));
            e.setCategory(Integer.parseInt(tokens[3]));
            int i = Integer.parseInt(tokens[4]);
            if(i > 0){e.setSubcategory(i);}
            i = Integer.parseInt(tokens[5]);
            if(i > 0){e.setSubcategory2(i);}
            i = Integer.parseInt(tokens[6]);
            if(i > 0){e.setSubcategory3(i);}
            i = Integer.parseInt(tokens[7]);
            if(i > 0){e.setSubcategory4(i);}
            e.setAccount(Integer.parseInt(tokens[8]));
            e.setComment(tokens[9]);
            return e;

        }catch(InvalidEntryException e){}
        return null;
    }

    public int getNumberOfEntries(String path)//Returns the number of entries in the file
    {  
        try (Scanner s = new Scanner(new File(path));){
            int counter = 0;
            while(s.hasNextLine()){
                counter ++;
                s.nextLine();
            }
            s.close();
            return counter;
        }catch (FileNotFoundException ex){}
        return 0;
    }

    public void printTables(JScrollPane sPane, JTable table, JScrollPane sPane2, JTable table2)
    {
        // Create BufferedImage objects for sPane and sPane2
        BufferedImage image1 = new BufferedImage(sPane.getWidth(), sPane.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d1 = image1.createGraphics();
       
        if(sPane.isVisible()){ sPane.paint(g2d1);}else{sPane.setVisible(true); table.setVisible(true); sPane.paint(g2d1); sPane.setVisible(false); table.setVisible(false);}
        g2d1.dispose();

        BufferedImage image2 = new BufferedImage(sPane2.getWidth(), sPane2.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d2 = image2.createGraphics();
        
        if(sPane2.isVisible()){sPane2.paint(g2d2);}else{sPane2.setVisible(true); table2.setVisible(true);  sPane2.paint(g2d2); sPane2.setVisible(false); table2.setVisible(false);}
        g2d2.dispose();

        // Create the directory if it doesn't exist
        File directory = new File("./Files/Exports/src");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save the images as files
        try {
            ImageIO.write(image1, "png", new File(directory, "entries.png"));
            ImageIO.write(image2, "png", new File(directory, "totals.png"));
        } catch (IOException e) {
            
        }

        try (PDDocument pdDoc = new PDDocument();) {
            PDPage page = new PDPage();
            pdDoc.addPage(page);
            PDImageXObject imageP1 = PDImageXObject.createFromFile("./Files/Exports/src/entries.png", pdDoc);
            PDImageXObject imageP2 = PDImageXObject.createFromFile("./Files/Exports/src/totals.png", pdDoc);
            
            try (PDPageContentStream cs = new PDPageContentStream(pdDoc, page)) {
                cs.beginText();
                cs.setFont(PDType1Font.COURIER_BOLD, 20);
                cs.setNonStrokingColor(Color.BLACK);
                cs.newLineAtOffset(250,700);
                cs.showText(month+" "+year);
                cs.newLine();
                cs.endText();
                cs.drawImage(imageP1,300-(imageP1.getWidth()/4), 400, (imageP1.getWidth()/2), (imageP1.getHeight()/2));
                cs.drawImage(imageP2, 300-(imageP2.getWidth()/4), 400-(imageP1.getHeight()/2), (imageP2.getWidth()/2), (imageP2.getHeight()/2));
            }
            String path = "./Files/Exports/"+month+".pdf";
            pdDoc.save(path);
        } catch (Exception e) {
        }

        
    }

   
}
