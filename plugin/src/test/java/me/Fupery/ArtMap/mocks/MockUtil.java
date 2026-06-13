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
import java.util.UUID;
import java.util.logging.Logger;

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
import org.bukkit.inventory.PlayerInventory;
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
import me.Fupery.ArtMap.IO.PixelTableManager;
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
    private PluginManager mockPluginManager;

    private static final Map<UUID,Player> mockPlayers = new HashMap<>();
    private static final Map<Integer,Canvas> mockCanvases = new HashMap<>();
    // Stand-in for the NMS world map storage: map id -> 128x128 colour data.
    private final Map<Integer,byte[]> worldMapData = new HashMap<>();
    private final Map<Integer,MapView> mapViews = new HashMap<>();

    public MockUtil() {
        //Mock 10 players
        if(mockPlayers.isEmpty()) {
            for(int i=1; i<=10; i++) {
                UUID id = UUID.randomUUID();
                String name = "UnitTestPlayer"+i;
                Player mockPlayer = mock(Player.class);
                when(mockPlayer.getName()).thenReturn(name);
                when(mockPlayer.getUniqueId()).thenReturn(id);
                // non-null location: ArtSession.end teleports on dismount
                when(mockPlayer.getLocation()).thenAnswer(inv -> new org.bukkit.Location(null, 0, 64, 0));
                mockPlayerInventory(mockPlayer);
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

    /**
     * Builds a MapView mock that remembers its renderers, so Map.setRenderer's
     * remove-then-add cycle behaves like the real thing.
     */
    private MapView buildMapView(int id) {
        MapView view = mock(MapView.class);
        when(view.getId()).thenReturn(id);
        List<org.bukkit.map.MapRenderer> renderers = new ArrayList<>();
        when(view.getRenderers()).thenAnswer(i -> new ArrayList<>(renderers));
        Mockito.doAnswer(i -> { renderers.add(i.getArgument(0)); return null; })
                .when(view).addRenderer(any(org.bukkit.map.MapRenderer.class));
        when(view.removeRenderer(any(org.bukkit.map.MapRenderer.class)))
                .thenAnswer(i -> renderers.remove((org.bukkit.map.MapRenderer) i.getArgument(0)));
        return view;
    }

    /**
     * Gives a player mock a working main/off hand so brush and recipe checks can
     * read what the "client" is holding.
     */
    private void mockPlayerInventory(Player player) {
        PlayerInventory inv = mock(PlayerInventory.class);
        // lazy AIR: new ItemStack() needs the server, not set yet at mock build time
        ItemStack[] hands = new ItemStack[2];
        when(inv.getItemInMainHand()).thenAnswer(i -> hands[0] != null ? hands[0] : (hands[0] = new ItemStack(Material.AIR)));
        when(inv.getItemInOffHand()).thenAnswer(i -> hands[1] != null ? hands[1] : (hands[1] = new ItemStack(Material.AIR)));
        Mockito.doAnswer(i -> { hands[0] = i.getArgument(0); return null; })
                .when(inv).setItemInMainHand(any(ItemStack.class));
        Mockito.doAnswer(i -> { hands[1] = i.getArgument(0); return null; })
                .when(inv).setItemInOffHand(any(ItemStack.class));
        when(player.getInventory()).thenReturn(inv);
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
        // real server formatting; ProtocolLib parses getVersion()
        String mcVersion = version.contains("-") ? version.substring(0, version.indexOf('-')) : version;
        when(mockServer.getVersion()).thenReturn("git-MockUtil (MC: " + mcVersion + ")");
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
            int id = (Integer) invocation.getArguments()[0];
            return mapViews.computeIfAbsent(id, this::buildMapView);
        });

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
         when(mockArtmap.getScheduler()).thenReturn(mockScheduler);

         //Mock getting resource
         when(mockArtmap.getTextResourceFile(anyString())).thenAnswer( invocation -> {
             if("lang.yml".equals(invocation.getArguments()[0])) {
                return new InputStreamReader(MockUtil.class.getResourceAsStream("../../../../lang.yml"));
             }
            return new InputStreamReader(MockUtil.class.getResourceAsStream((String) invocation.getArguments()[0]));
         });

         //stateful per-map store so painted pixels survive setMap -> getMap
         Reflection mockReflection = mock(Reflection.class);
         when(mockReflection.getMap(any(MapView.class))).thenAnswer(invocation -> {
             MapView view = invocation.getArgument(0);
             byte[] data = worldMapData.computeIfAbsent(view.getId(), id -> new byte[Size.MAX.value]);
             return Arrays.copyOf(data, data.length);
         });
         try {
             Mockito.doAnswer(invocation -> {
                 MapView view = invocation.getArgument(0);
                 byte[] colors = invocation.getArgument(1);
                 worldMapData.put(view.getId(), Arrays.copyOf(colors, colors.length));
                 return null;
             }).when(mockReflection).setWorldMap(any(MapView.class), any(byte[].class));
         } catch (NoSuchFieldException | IllegalAccessException e) {
             throw new IllegalStateException("Stubbing a mock cannot throw", e);
         }
         when(mockArtmap.getReflection()).thenReturn(mockReflection);

         //mock HeadsCahce
         HeadsCache cache = mock(HeadsCache.class);
         when(mockArtmap.getHeadsCache()).thenReturn(cache);

         //mock DataTabes
         PixelTableManager pixelTableManager = PixelTableManager.buildTables(mockArtmap);
         when(mockArtmap.getPixelTable()).thenReturn(pixelTableManager);

         this.mockArtmap = mockArtmap;
         return this;
    }

    /**
     * The player mocks are shared across tests, so op/permission stubs added by one
     * test leak into the next. Call this in @Before to restore the defaults.
     */
    public void resetMockPlayerPermissions() {
        for (Player player : mockPlayers.values()) {
            when(player.isOp()).thenReturn(false);
            when(player.hasPermission(anyString())).thenReturn(false);
        }
    }

    public Player[] getRandomMockPlayers(int count) {
        if (count > mockPlayers.size()) {
            throw new IllegalArgumentException("Only " + mockPlayers.size() + " mock players available, requested " + count);
        }
        List<Player> players = new ArrayList<>(mockPlayers.values());
        Collections.shuffle(players);
        return players.subList(0, count).toArray(new Player[count]);
    }

    public Canvas[] getRandomMockCanvases(int count) {
        if (count > mockCanvases.size()) {
            throw new IllegalArgumentException("Only " + mockCanvases.size() + " mock canvases available, requested " + count);
        }
        List<Canvas> canvases = new ArrayList<>(mockCanvases.values());
        Collections.shuffle(canvases);
        return canvases.subList(0, count).toArray(new Canvas[count]);
    }

    public CanvasCopy mockCanvasCopy(Canvas canvas) {
        int id = canvas.getMapId();
        CanvasCopy mockCanvasCopy = mock(CanvasCopy.class);
        when(mockCanvasCopy.getOriginalId()).thenReturn(id);
        return mockCanvasCopy;
    }
}