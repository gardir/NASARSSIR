import org.junit.jupiter.api.Test;

class NASARSSIRTest {

    @Test
    void isNasaImage() {
        String test1 = "http://www.nasa.gov/sites/default/files/thumbnails/image/potw1903a.jpg";
        String test2 = "http://www.nasa.gov/sites/default/files/thumbnails/image/2.21-iss058e016863_highres.jpg";
        String test3 = "https://www.nasa.gov/sites/default/files/images/722342main_challenger_full_full.jpg";
        assert NASARSSIR.isNasaImage(test1);
        assert NASARSSIR.isNasaImage(test2);
        assert NASARSSIR.isNasaImage(test3);
    }
}