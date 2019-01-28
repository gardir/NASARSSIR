

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.List;

import com.sun.syndication.feed.synd.SyndEnclosureImpl;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import javax.imageio.ImageIO;


/**
 * Created by Gard on 03/08/2017.
 * @author Gard
 * @version 0.5
 */
public class NASARSSIR {

    public static void main(String[] args) {
        try {
            URL feedUrl = new URL("https://www.nasa.gov/rss/dyn/lg_image_of_the_day.rss");

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));

            List entries = feed.getEntries();
            int i = 0;
            for ( Object listItem : entries ) {
                if ( listItem instanceof SyndEntryImpl ) {
                    if ( !checkEntry( (SyndEntryImpl) listItem, args[0] ) ) {
                        System.out.println("Image already saved. Aborting...");
                        if ( i > 0 ) {
                            System.out.format("Saved %d images to '%s'\n", i, args[0]);
                        }
                        System.exit(0);
                    }
                }
                i++;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR: "+ex.getMessage());
        }
    }

    /**
     * Checks if an Syndicate Entry implementation has an enclosed list of image urls,
     * @param entry the entry to check
     * @param path path to save image if entry is valid
     * @return a boolean value representing all images saved or _one_ failed
     * @throws IOException
     */
    private static boolean checkEntry(SyndEntryImpl entry, String path) throws IOException {
        String url = ((SyndEnclosureImpl) entry.getEnclosures().get(0)).getUrl().replace("http://", "https://");
        String description = entry.getUri();
        String filename = description.substring(description.lastIndexOf("/") + 1);
        String originalFilename = url.substring(url.lastIndexOf("/") + 1);
        if ( isNasaImage(url) ) {

            String filepath = String.format("%s%s", path.charAt(path.length()-1) == '/' ? path : path + "/", originalFilename);
            File outFile = new File(filepath);
            if ( outFile.exists() ) {
                String newFilePath = String.format("%s%s%s", path.charAt(path.length()-1) == '/' ? path : path + "/",
                        filename,
                        originalFilename.substring(originalFilename.lastIndexOf(".")));
                File newOutFile = new File(newFilePath);
                if ( !outFile.renameTo(newOutFile) )
                    return false;
                System.out.format("Renamed '%s' to '%s'\n", originalFilename, filename);
                return true;
            } else if ( saveImageTo(url, path, filename) ) {
                return false;
            }
            System.out.format("Saved '%s'\n", filename);
        }
        return true;
    }

    /**
     * Checks if a given url is of an image from 'http://www.nasa.gov/sites/default/files/thumbnails/image/'
     * @param url the url to check
     * @return boolean value representing if it is an image from given source
     */
    private static boolean isNasaImage(String url) {
        return url.startsWith("https://www.nasa.gov/sites/default/files/thumbnails/image/") &&
                (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith("png") );
    }

    /**
     * Saves image at url to the absolute path with given filename.
     * @param url the url to an image
     * @param absolutePath absolute-path-to the folder to store in
     * @param filename name of the new image
     * @return a boolean value representing if the image were successfully stored
     * @throws IOException
     */
    private static boolean saveImageTo(String url, String absolutePath, String filename) throws IOException {
        // Creates path as 'absolutePath/filename' by checking if absolutePath included char '/' at the end.
        String path = String.format("%s%s", absolutePath.charAt(absolutePath.length()-1) == '\\' ? absolutePath : absolutePath + "\\", filename);
        File outFile = new File(path);
        if ( outFile.exists() ) {
            return false;
        }
        URL website = new URL(url);
        BufferedImage img = ImageIO.read(website);
        String formatName = url.substring(url.lastIndexOf("."));
        return ImageIO.write(img, formatName, outFile);
    }

}