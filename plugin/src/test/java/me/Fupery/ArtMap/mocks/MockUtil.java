package me.Fupery.ArtMap.mocks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.io.Files;
import com.google.gson.GsonBuilder;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.UnsafeValues;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapView;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Colour.ArtDye;
import me.Fupery.ArtMap.api.Colour.BasicDye;
import me.Fupery.ArtMap.api.Colour.DyeType;
import me.Fupery.ArtMap.api.Colour.Palette;
import me.Fupery.ArtMap.Compatibility.CompatibilityManager;
import me.Fupery.ArtMap.api.Config.Configuration;
import me.Fupery.ArtMap.Easel.Canvas;
import me.Fupery.ArtMap.Easel.Canvas.CanvasCopy;
import me.Fupery.ArtMap.Heads.HeadsCache;
import me.Fupery.ArtMap.IO.Database.Map.Size;
import me.Fupery.ArtMap.Utils.Reflection;
import me.Fupery.ArtMap.Utils.Scheduler;
import me.Fupery.ArtMap.Utils.Scheduler.TaskScheduler;

/**
 * Keeping the reused mocks in one place.
 */
@SuppressWarnings( "deprecation" )
public class MockUtil {
    private ArtMap pluginMock; // Mocked plugin
    private ArtMap mockArtmap;
    private Server mockServer;
    //private BukkitScheduler mockScheduler;
    private PluginManager mockPluginManager;

    private static final Map<UUID,Player> mockPlayers = new HashMap<>();
    private static final Map<Integer,Canvas> mockCanvases = new HashMap<>();

    public MockUtil() {
        //Mock 10 players
        if(mockPlayers.isEmpty()) {
            for(int i=1; i<=10; i++) {
                UUID id = UUID.randomUUID();
                String name = "UnitTestPlayer"+i;
                Player mockPlayer = mock(Player.class);
                when(mockPlayer.getName()).thenReturn(name);
                when(mockPlayer.getUniqueId()).thenReturn(id);
                mockPlayers.put(id, mockPlayer);
            }
        }
        //Mock 10 canvases
        if(mockCanvases.isEmpty()) {
            for(int i=0; i<10; i++) {
                Canvas mockCanvas = mock(Canvas.class);
                when(mockCanvas.getMapId()).thenReturn(i);
                mockCanvases.put(i, mockCanvas);
            }
        }
        pluginMock = Mockito.mock(ArtMap.class);
    } // Hides constructor

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
    public ArtMap getPluginMock() {
        return this.pluginMock;
    }

    // Returns fully mocked Artmap
    public ArtMap getArtmapMock() {
        return this.mockArtmap;
    }

    //Return mockServer
    public Server getMockServer() {
        return this.mockServer;
    }

    public MockUtil mockPluginManager() {
        if(this.mockPluginManager != null) {
            return this;
        }
         //Mock Plugin Manager
         PluginManager mockPluginManager = mock(PluginManager.class);
         when(mockPluginManager.getPlugins()).thenReturn(new Plugin[0]);
         when(mockPluginManager.isPluginEnabled(anyString())).thenReturn(false);  //no compat plugins
         when(mockPluginManager.getPlugin(anyString())).thenReturn(null);
         this.mockPluginManager = mockPluginManager;
         return this;
    }

    public MockUtil mockServer(String version) {
        if(this.mockServer != null) {
            return this;
        }
        this.mockPluginManager();
        //Real Logger
        Logger testLogger = Logger.getLogger("TestLogger");
        Server mockServer = mock(Server.class);

        BukkitScheduler mockBukkitScheduler = mock(BukkitScheduler.class);
        when(mockServer.getVersion()).thenReturn(version);
        when(mockServer.getBukkitVersion()).thenReturn(version);
        when(mockServer.getLogger()).thenReturn(testLogger);
        when(mockServer.getScheduler()).thenReturn(mockBukkitScheduler);
        when(mockServer.getPluginManager()).thenReturn(this.mockPluginManager);
        when(mockServer.getPlayer(any(UUID.class))).thenAnswer( invocation -> {
            return mockPlayers.get(invocation.getArguments()[0]);
        });
        when(mockServer.getOfflinePlayer(any(UUID.class))).thenAnswer( invocation -> {
            UUID id = (UUID) invocation.getArguments()[0];
            OfflinePlayer player = mockPlayers.get(id);
            if(player == null) {
                player = mock(OfflinePlayer.class);
                when(player.getUniqueId()).thenReturn(id);
                when(player.getName()).thenReturn("UnknownPlayer_"+id);
            }
            return player;
        });
        ItemFactory mockItemFactory = mock(ItemFactory.class);
        when(mockItemFactory.getItemMeta(any(Material.class))).thenAnswer( invocation -> {
            Material mat = (Material) invocation.getArguments()[0];
            if(mat == Material.FILLED_MAP || mat == Material.MAP) {
                MapMeta meta = mock(MapMeta.class);
                return(meta);
            } else if(mat == Material.PLAYER_HEAD) {
                SkullMeta skull = mock(SkullMeta.class);
                return skull;
            } else {
                ItemMeta meta = mock(ItemMeta.class);
                return meta;
            }
        });
        when(mockServer.getItemFactory()).thenReturn(mockItemFactory);
        UnsafeValues mockUnsafeValues = mock(UnsafeValues.class);
        when(mockServer.getUnsafe()).thenReturn(mockUnsafeValues);
        when(mockServer.getMap(anyInt())).thenAnswer( invocation-> {
            MapView mockMapView = mock(MapView.class);
            when(mockMapView.getId()).thenReturn((Integer) invocation.getArguments()[0]);
            return mockMapView;
        });

        //this.mockScheduler = mockBukkitScheduler;
        this.mockServer = mockServer;
        try {
            Bukkit.setServer(mockServer);
        } catch ( UnsupportedOperationException e ) {
            //eat this the server is all ready set.
        }
        return this;
    }

    public MockUtil mockArtMap() throws NoSuchFieldException, SecurityException, FileNotFoundException,
            IOException, InvalidConfigurationException, InvalidDescriptionException {
        if(this.mockArtmap != null) {
            return this;
        }

         //Make sure the config directory is present and config.yml is in it
         File dataDir = new File("target/plugins/Artmap/");
         dataDir.mkdirs();
         File configFile = new File("target/plugins/Artmap/config.yml");
         if(!configFile.exists()) {
             File cfgFile = new File("target/classes/config.yml");
             Files.copy(cfgFile, configFile);
         }

         // Mock ArtMap ArtMap.instance().
         ArtMap mockArtmap = mock(ArtMap.class);
         ArtMap.setInstance(mockArtmap);
         //when(ArtMap.instance()).thenReturn(mockArtmap);
         //mock compatManger
         CompatibilityManager mockCompatibilityManager = mock(CompatibilityManager.class);
         when(mockArtmap.getCompatManager()).thenReturn(mockCompatibilityManager);
         //mock logger
         Logger testLogger = Logger.getLogger("TestLogger");
         when(mockArtmap.getLogger()).thenReturn(testLogger);
         when(mockArtmap.getDataFolder()).thenReturn(dataDir);
         //mock configuration
         FileConfiguration fileConfig = new YamlConfiguration();
         fileConfig.load("target/plugins/Artmap/config.yml");
         when(mockArtmap.getConfig()).thenReturn(fileConfig);
         Configuration config = new Configuration(mockArtmap);
         when(mockArtmap.getConfiguration()).thenReturn(config);
         // Mock the description
         File pluginYml = new File(getClass().getResource("/plugin.yml").getPath());
         PluginDescriptionFile desc = new PluginDescriptionFile(new FileInputStream(pluginYml));
         Mockito.when(mockArtmap.getDescription()).thenReturn(desc);
         // Mock the server return
         when(mockArtmap.getServer()).thenReturn(this.mockServer);
 
         // Mock the scheduler
         Scheduler mockScheduler = mock(Scheduler.class);
         TaskScheduler mockTaskScheduler = mock(TaskScheduler.class);
         when(mockArtmap.getGson(anyBoolean())).then(invocation -> {
            GsonBuilder builder = new GsonBuilder();
            if ((boolean) invocation.getArguments()[0]) {
                builder.setPrettyPrinting();
            }
            return builder.create();
         });

         //Mock the dye pallete
         when(ArtMap.instance().getDyePalette()).thenReturn(new Palette(){
         
             @Override
             public ArtDye[] getDyes(DyeType dyeType) {
                 return null;
             }
         
             @Override
             public ArtDye getDye(ItemStack item) {
                 return null;
             }
         
             @Override
             public BasicDye getDefaultColour() {
                 BasicDye dye = mock(BasicDye.class);
                 when(dye.getColour()).thenReturn(Byte.valueOf("0"));
                 return dye;
             }

             @Override
             public ArtDye getDye(byte color) {
                 return null;
             }
         });

         when(mockTaskScheduler.run(any(Runnable.class))).thenAnswer(new Answer<BukkitTask>() {
 
             @Override
             public BukkitTask answer(InvocationOnMock invocation) throws Throwable {
                 Runnable run = invocation.getArgument(0);
                 run.run();
                 return null;
             }
             
         });
         mockScheduler.ASYNC = mockTaskScheduler;
         mockScheduler.SYNC = mockTaskScheduler;
         //FieldSetter.setField(mockScheduler, mockScheduler.getClass().getField("SYNC"), mockTaskScheduler);
         //FieldSetter.setField(mockScheduler, mockScheduler.getClass().getField("ASYNC"), mockTaskScheduler);
         when(mockArtmap.getScheduler()).thenReturn(mockScheduler);

         //Mock getting resource
         when(mockArtmap.getTextResourceFile(anyString())).thenAnswer( invocation -> {
             if("lang.yml".equals(invocation.getArguments()[0])) {
                 System.out.println("DEBUG: getting test lang.yml");
                 //System.out.println(MockUtil.class.getResource(".").getPath());
                return new InputStreamReader(MockUtil.class.getResourceAsStream("../../../../lang.yml"));
             }
            return new InputStreamReader(MockUtil.class.getResourceAsStream((String) invocation.getArguments()[0]));
         });

         //mock Reflection 
         Reflection mockReflection = mock(Reflection.class);
         //have get map return an all white map
         byte[] mapOutput = new byte[Size.MAX.value];
         Arrays.fill(mapOutput, Byte.valueOf("0"));
         when(mockReflection.getMap(any(MapView.class))).thenReturn(mapOutput);
         when(mockArtmap.getReflection()).thenReturn(mockReflection);

         //mock HeadsCahce
         HeadsCache cache = mock(HeadsCache.class);
         when(mockArtmap.getHeadsCache()).thenReturn(cache);

         this.mockArtmap = mockArtmap;
         return this;
    }

    private Player getRandomMockPlayer() {
        List<Entry<UUID,Player>> playerList = mockPlayers.entrySet().stream().collect(Collectors.toList());
        Collections.shuffle(playerList);
        return playerList.get(0).getValue();     
    }

    public Player[] getRandomMockPlayers(int count) {
        List<Player> players = new ArrayList<>();
        int filled = 0;
        while(filled<count) {
            Player player = getRandomMockPlayer();
            if(!players.contains(player)) {
                players.add(player);
                filled++;
            }
        }
        return players.toArray(new Player[count]);
    }

    private Canvas getRandomMockCanvas() {
        List<Entry<Integer,Canvas>> canvasList = mockCanvases.entrySet().stream().collect(Collectors.toList());
        Collections.shuffle(canvasList);
        return canvasList.get(0).getValue();     
    }

    public Canvas[] getRandomMockCanvases(int count) {
        List<Canvas> canvases = new ArrayList<>();
        int filled = 0;
        while(filled<count) {
            Canvas canvas = getRandomMockCanvas();
            if(!canvases.contains(canvas)) {
                canvases.add(canvas);
                filled++;
            }
        }
        return canvases.toArray(new Canvas[count]);
    }

    public CanvasCopy mockCanvasCopy(Canvas canvas) {
        int id = canvas.getMapId();
        CanvasCopy mockCanvasCopy = mock(CanvasCopy.class);
        when(mockCanvasCopy.getOriginalId()).thenReturn(id);
        return mockCanvasCopy;
    }
}