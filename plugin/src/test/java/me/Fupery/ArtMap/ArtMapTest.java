package me.Fupery.ArtMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.Fupery.ArtMap.mocks.MockUtil;

public class ArtMapTest {

    private MockUtil mocks;

    @BeforeEach
    public void setup() throws Exception {
        this.mocks = new MockUtil();
        // Must be a modern version: the test classpath carries the 1.21 paper-api,
        // so onEnable has to pick the modern palette/head retriever to load cleanly.
        this.mocks.mockServer("1.21.4-R0.1-SNAPSHOT");
    }

    @Test
    public void testArtMapPlugin() throws FileNotFoundException, InvalidDescriptionException {
        //Bukkit Server Mock
        Server mockServer = this.mocks.getMockServer();

        @SuppressWarnings( "deprecation" )
        JavaPluginLoader loader = new JavaPluginLoader(mockServer);

        File pluginYml = new File(getClass().getResource("/plugin.yml").getPath());
        PluginDescriptionFile desc = new PluginDescriptionFile(new FileInputStream(pluginYml));
        File datafolder = new File("./target/plugins/Artmap/");
        //File logFile = new File("./target/logs/artmap.log");
        datafolder.mkdirs();

        //Test Artmap enable
        ArtMap artmap = new ArtMap(loader,desc,datafolder,null);
        //spy the getCommand to come back with a mock
        artmap = Mockito.spy(artmap);
        Mockito.doReturn(mock(PluginCommand.class)).when(artmap).getCommand(any(String.class));

        Assertions.assertNotNull(artmap, "Artmap instnace null!");
        artmap.onEnable();
        Assertions.assertNotNull(ArtMap.instance(), "Artmap failed to enable.");
    }

    //Test artwork recycle does not delete map that is a completed artwork
}