package me.Fupery.ArtMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.mockito.Mockito;

public class MockUtil {
    private Plugin pluginMock;  // Mocked plugin

    private MockUtil() {} // Hides constructor

    // Initialises the mock utility with mocked plugin class
    public static MockUtil setUp() {
        return new MockUtil().mockPluginClass();
    } 

    // Mocks Plugin class
    private MockUtil mockPluginClass() {
        pluginMock = Mockito.mock(Plugin.class);
        return this;
    }

     // Mocks JavaPlugin.getLogger
    public MockUtil mockLogger() {
        Logger testLogger = Logger.getLogger("TestLogger");
        Mockito.when(pluginMock.getLogger()).thenReturn(testLogger);
        return this;
    }

    // Mocks JavaPlugin.getDataFolder
    public MockUtil mockDataFolder(File folder) {
        Mockito.when(pluginMock.getDataFolder()).thenReturn(folder);
        return this;
    } 

     // Mocks file getting such as config.yml & plugin.yml
    public MockUtil mockResourceFetching() throws Exception {
        mockPluginDescription();
        File configYml = new File(getClass().getResource("/config.yml").getPath());
        Mockito.when(pluginMock.getResource("config.yml")).thenReturn(new FileInputStream(configYml));
        return this;
    }

     // Mocks JavaPlugin.getDescription
    private MockUtil mockPluginDescription() throws InvalidDescriptionException, FileNotFoundException {
        File pluginYml = new File(getClass().getResource("/plugin.yml").getPath());
        PluginDescriptionFile desc = new PluginDescriptionFile(new FileInputStream(pluginYml));
        Mockito.when(pluginMock.getDescription()).thenReturn(desc);
        return this;
    }

    // Returns mocked plugin
    public Plugin getPluginMock() {
        return pluginMock;
    }
}