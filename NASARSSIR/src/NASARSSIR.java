

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static void main(String[] args) throws IOException {
        if ( args.length > 0 ) {
            String path = args[0];
            path = path.charAt(path.length() - 1) == '\\' ? path : path + "\\";
            saveEntriesTo(path);
        } else {
            System.out.println("Missing argument: path to directory where images will be saved to");
        }
    }

    /**
     * Checks if an Syndicate Entry implementation has an enclosed list of image urls,
     * @param path path to directory to save images in
     * @throws IOException if the image save could not be done by ImageIO
     */
    private static void saveEntriesTo(String path) throws IOException {
        try {
            URL feedUrl = new URL("https://www.nasa.gov/rss/dyn/lg_image_of_the_day.rss");

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));

            List entries = feed.getEntries();
            int i = 0;
            for ( Object listItem : entries ) {
                if ( listItem instanceof SyndEntryImpl ) {
                    if ( !checkEntry( (SyndEntryImpl) listItem, path ) ) {
                        System.out.println("Image failed to save.");
                        System.exit(1);
                    }
                }
                i++;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR: "+ex.getMessage());
            throw new IOException(ex.getMessage());
        }
    }

    /**
     * Checks if an Syndicate Entry implementation has an enclosed list of image urls,
     * @param entry the entry to check
     * @param path path to save image if entry is valid
     * @return a boolean value representing all images saved or _one_ failed
     * @throws IOException if the image save could not be done by ImageIO
     */
    private static boolean checkEntry(SyndEntryImpl entry, String path) throws IOException {
        String url = ((SyndEnclosureImpl) entry.getEnclosures().get(0)).getUrl().replace("http://", "https://");
        String description = entry.getUri();
        String descriptiveName = description.substring(description.lastIndexOf("/") + 1);
        String imageName = url.substring(url.lastIndexOf("/") + 1);
        if ( isNasaImage(url) ) {
            return saveEntry(path, url, imageName, descriptiveName);
        } else {
            System.out.format("'%s' is not a NasaImage url\n", url);
        }
        return true;
    }


    /**
     * Tries to save image from url with imagename as a more descriptive name on the given path.
     * @param path is the folder to save the image
     * @param url where the image is available
     * @param imageName the raw name of the image, usually not descriptive
     * @param descriptiveName the name to save the image as
     * @throws IOException if the image could not be saved with descriptive name at given path
     */
    private static boolean saveEntry(String path, String url, String imageName, String descriptiveName) throws IOException {
        String filepath = String.format("%s%s",
                path,
                imageName);
        File earlyVersionOutFile = new File(filepath);
        String formatName = url.substring(url.lastIndexOf(".") + 1);
        descriptiveName = String.format("%s.%s", descriptiveName, formatName);
        String newFilePath = String.format("%s%s",
                path,
                descriptiveName);
        File currentVersionOutFile = new File(newFilePath);
        if ( earlyVersionOutFile.exists() ) {
            if ( !earlyVersionOutFile.renameTo(currentVersionOutFile) ) {
                System.out.format("Failed to rename '%s' to '%s'\n", imageName, descriptiveName);
                return false;
            }
            System.out.format("Renamed '%s' to '%s'\n", imageName, descriptiveName);
        } else if ( !currentVersionOutFile.exists() ) {
            URL website = new URL(url);
            BufferedImage img = ImageIO.read(website);
            if ( !ImageIO.write(img, formatName, currentVersionOutFile) ) {
                System.out.format("ImageIo failed to write on file '%s'\n", descriptiveName);
                return false;
            } else {
                System.out.format("Saved '%s'\n", descriptiveName);
            }
        } else {
            System.out.format("'%s' already exists\n", descriptiveName);
        }
        return true;
    }

    /**
     * Checks if a given url is of an image from 'http://www.nasa.gov/sites/default/files/thumbnails/image/'
     * @param url the url to check
     * @return boolean value representing if it is an image from given source
     */
    protected static boolean isNasaImage(String url) {
        Pattern imagePattern = Pattern.compile("https{0,1}://www.nasa.gov/.*images{0,1}/.*.(?>jpeg|(?>jpg|png))");
        Matcher matcher = imagePattern.matcher(url);
//        return url.startsWith("https://www.nasa.gov/sites/default/files/thumbnails/image/") &&
//                (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith("png") );
        return matcher.matches();
    }

}