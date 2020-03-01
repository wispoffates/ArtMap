package me.Fupery.ArtMap;

import static org.mockito.ArgumentMatchers.anyString;

import java.io.FileNotFoundException;
import java.util.logging.Logger;


import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.Test;
import org.mockito.Mockito;

import de.schlichtherle.io.File;
import de.schlichtherle.io.FileInputStream;

public class ArtMapTest {

    @Test
    public void testArtMapPlugin() throws FileNotFoundException, InvalidDescriptionException {
        //Bukkit Server Mock
        Server mockServer = Mockito.mock(Server.class);
        PluginManager mockPluginManager = Mockito.mock(PluginManager.class);
        Logger testLogger = Logger.getLogger("TestLogger");
        BukkitScheduler mockScheduler = Mockito.mock(BukkitScheduler.class);
        //Mock Plugin Manager
        Mockito.when(mockPluginManager.isPluginEnabled(anyString())).thenReturn(false);  //no compat plugins
        Mockito.when(mockPluginManager.getPlugin(anyString())).thenReturn(null);

        //Mockito.when(mockBukkit.getPluginManager()).thenReturn(mockPluginManager);
        Mockito.when(mockServer.getBukkitVersion()).thenReturn("1.15-MOCK");
        Mockito.when(mockServer.getLogger()).thenReturn(testLogger);
        Mockito.when(mockServer.getPluginManager()).thenReturn(mockPluginManager);
       
        Mockito.when(mockServer.getScheduler()).thenReturn(mockScheduler);
        Bukkit.setServer(mockServer);

        JavaPluginLoader loader = new JavaPluginLoader(mockServer);
        File pluginYml = new File(getClass().getResource("/plugin.yml").getPath());
        PluginDescriptionFile desc = new PluginDescriptionFile(new FileInputStream(pluginYml));
        File datafolder = new File("./target/plugins/Artmap/");
        //File logFile = new File("./target/logs/artmap.log");
        datafolder.mkdirs();
        ArtMap artmap = new ArtMap(loader,desc,datafolder,null);
        //artmap.onEnable();
    }

    //Test artwork recycle does not delete map that is a completed artwork
}