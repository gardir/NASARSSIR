

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
 * @version 0.1
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
                    checkEntry( (SyndEntryImpl) listItem, args[0] );
                }
            }

        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR: "+ex.getMessage());
        }
    }

    private static void checkEntry(SyndEntryImpl entry, String path) throws IOException {
        for ( Object listItem : entry.getEnclosures()) {
            if ( listItem instanceof SyndEnclosureImpl ) {
                SyndEnclosureImpl enclosure = (SyndEnclosureImpl) listItem;
                String url = enclosure.getUrl().replace("http://", "https://");
                String filename = url.substring(url.lastIndexOf("/") + 1);
                saveImageTo(url, path, filename);
            }
        }
    }

    private static void saveImageTo(String url, String absolutePath, String filename) throws IOException {
        URL website = new URL(url);
        File outFile = new File(String.format("%s%s", absolutePath.charAt(absolutePath.length()-1) == '/' ? absolutePath : absolutePath + "/", filename));
        BufferedImage img = ImageIO.read(website);
        ImageIO.write(img, "jpg", outFile);
    }

}