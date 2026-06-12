package me.Fupery.ArtMap.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Compatibility.CompatibilityManager;
import me.Fupery.ArtMap.api.Config.Configuration;
import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.mocks.MockUtil;

public class LangTest {

    private MockUtil mocks;

    @BeforeEach
    public void setup() throws Exception {
        mocks = new MockUtil();
        mocks.mockServer("1.14.4").mockArtMap();
    }

    @Test
    public void testLangInit() throws NoSuchFieldException, SecurityException, FileNotFoundException, IOException,
            InvalidConfigurationException {
        ArtMap mockPlugin = this.mocks.getArtmapMock();
        CompatibilityManager mockCompatibilityManager = mock(CompatibilityManager.class);
        when(ArtMap.instance().getCompatManager()).thenReturn(mockCompatibilityManager);
        Configuration config = new Configuration(mockPlugin);
        Lang.load(mockPlugin, config);
        String[] needCanvas = Lang.ActionBar.NEED_CANVAS.get();
        Assertions.assertFalse(needCanvas.length==0, "NEED_CANVAS should not have returned empty!");
        Assertions.assertEquals("§4§lPlace a canvas on the easel to paint. §3§l/art §4§lfor more info.", needCanvas[0]);
    }
}