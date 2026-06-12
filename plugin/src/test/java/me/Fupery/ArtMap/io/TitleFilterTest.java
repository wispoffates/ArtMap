package me.Fupery.ArtMap.io;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import me.Fupery.ArtMap.IO.TitleFilter;
import me.Fupery.ArtMap.mocks.MockUtil;

public class TitleFilterTest {

    private static TitleFilter filter;

    @BeforeAll
    public static void setup() throws Exception {
        // TitleFilter.check consults the configured language and swear filter.
        MockUtil mocks = new MockUtil();
        mocks.mockServer("1.14.4").mockArtMap();
        mocks.mockDataFolder(new File("target/plugins/Artmap/")).mockLogger();
        filter = new TitleFilter(new String[] { "badword" });
    }

    @Test
    public void acceptsSimpleTitle() {
        assertTrue(filter.check("Sunset"), "A plain title should be accepted");
    }

    @Test
    public void acceptsTitleWithSpaces() {
        assertTrue(filter.check("My Artwork"), "Spaces are legal in titles");
    }

    @Test
    public void rejectsTooShort() {
        assertFalse(filter.check("ab"), "Titles under 3 characters should be rejected");
    }

    @Test
    public void rejectsTooLong() {
        assertFalse(filter.check("12345678901234567"), "Titles over 16 characters should be rejected");
    }

    @Test
    public void acceptsBoundaryLengths() {
        assertTrue(filter.check("abc"), "3 characters is the minimum legal length");
        assertTrue(filter.check("1234567890123456"), "16 characters is the maximum legal length");
    }

    @Test
    public void rejectsIllegalCharacters() {
        assertFalse(filter.check("art@home"), "@ should be rejected");
        assertFalse(filter.check("semi;colon"), "; should be rejected");
        assertFalse(filter.check("back\\slash"), "backslash should be rejected");
        assertFalse(filter.check("art.png"), ". should be rejected");
    }
}
