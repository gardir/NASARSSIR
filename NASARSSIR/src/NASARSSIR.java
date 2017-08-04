

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
            for ( Object listItem : entries ) {
                if ( listItem instanceof SyndEntryImpl ) {
                    if ( !checkEntry( (SyndEntryImpl) listItem, args[0] ) ) {
                        System.out.println("Image already saved. Aborting...");
                        System.exit(0);
                    }
                }
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
        for ( Object listItem : entry.getEnclosures()) {
            if ( listItem instanceof SyndEnclosureImpl ) {
                SyndEnclosureImpl enclosure = (SyndEnclosureImpl) listItem;
                String url = enclosure.getUrl().replace("http://", "https://");
                String filename = url.substring(url.lastIndexOf("/") + 1);
                if ( isNasaImage(enclosure.getUrl()) ) {
                    if ( !saveImageTo(url, path, filename) ) {
                        return false;
                    }
                    System.out.format("Saved '%s' to '%s'\n", filename, path);
                }
            }
        }
        return true;
    }

    /**
     * Checks if a given url is of an image from 'http://www.nasa.gov/sites/default/files/thumbnails/image/'
     * @param url the url to check
     * @return boolean value representing if it is an image from given source
     */
    private static boolean isNasaImage(String url) {
        return url.startsWith("http://www.nasa.gov/sites/default/files/thumbnails/image/") &&
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
        String path = String.format("%s%s", absolutePath.charAt(absolutePath.length()-1) == '/' ? absolutePath : absolutePath + "/", filename);
        File outFile = new File(path);
        if ( outFile.exists() ) {
            return false;
        }
        URL website = new URL(url);
        BufferedImage img = ImageIO.read(website);
        return ImageIO.write(img, "jpg", outFile);
    }

}