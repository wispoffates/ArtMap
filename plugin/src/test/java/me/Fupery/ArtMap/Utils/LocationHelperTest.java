package me.Fupery.ArtMap.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

public class LocationHelperTest {

    private static ServerMock server;
    private static World world;

    @BeforeAll
    public static void setup() {
        server = MockBukkit.mock();
        world = server.addSimpleWorld("test");
    }

    @AfterAll
    public static void teardown() {
        MockBukkit.unmock();
    }

    private Location base() {
        return new Location(world, 10, 64, 20);
    }

    @Test
    public void shiftTowardsCardinalDirections() {
        assertEquals(new Location(world, 10, 64, 19), new LocationHelper(base()).shiftTowards(BlockFace.NORTH));
        assertEquals(new Location(world, 10, 64, 21), new LocationHelper(base()).shiftTowards(BlockFace.SOUTH));
        assertEquals(new Location(world, 11, 64, 20), new LocationHelper(base()).shiftTowards(BlockFace.EAST));
        assertEquals(new Location(world, 9, 64, 20), new LocationHelper(base()).shiftTowards(BlockFace.WEST));
        assertEquals(new Location(world, 10, 65, 20), new LocationHelper(base()).shiftTowards(BlockFace.UP));
        assertEquals(new Location(world, 10, 63, 20), new LocationHelper(base()).shiftTowards(BlockFace.DOWN));
    }

    @Test
    public void shiftTowardsDiagonals() {
        assertEquals(new Location(world, 11, 64, 19), new LocationHelper(base()).shiftTowards(BlockFace.NORTH_EAST));
        assertEquals(new Location(world, 9, 64, 19), new LocationHelper(base()).shiftTowards(BlockFace.NORTH_WEST));
        assertEquals(new Location(world, 11, 64, 21), new LocationHelper(base()).shiftTowards(BlockFace.SOUTH_EAST));
        assertEquals(new Location(world, 9, 64, 21), new LocationHelper(base()).shiftTowards(BlockFace.SOUTH_WEST));
    }

    @Test
    public void shiftTowardsPartialShift() {
        assertEquals(new Location(world, 10, 64, 19.35), new LocationHelper(base()).shiftTowards(BlockFace.NORTH, 0.65));
        assertEquals(new Location(world, 10.65, 64, 20), new LocationHelper(base()).shiftTowards(BlockFace.EAST, 0.65));
    }

    @Test
    public void shiftTowardsSelfReturnsSameSpot() {
        assertEquals(base(), new LocationHelper(base()).shiftTowards(BlockFace.SELF));
    }

    @Test
    public void shiftDoesNotMutateOriginal() {
        Location original = base();
        new LocationHelper(original).shiftTowards(BlockFace.NORTH);
        assertEquals(base(), original, "shiftTowards must not modify the location passed in");
    }

    @Test
    public void centreSnapsToMiddleOfBlock() {
        Location offCentre = new Location(world, 10.9, 64.2, 20.1);
        Location centred = new LocationHelper(offCentre).centre().shiftTowards(BlockFace.SELF);
        assertEquals(new Location(world, 10.5, 64.5, 20.5), centred);
    }
}
