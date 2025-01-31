package io.github.wysohn.triggerreactor.core.manager.trigger.location;

import io.github.wysohn.triggerreactor.core.bridge.IBlock;
import io.github.wysohn.triggerreactor.core.bridge.IItemStack;
import io.github.wysohn.triggerreactor.core.bridge.ILocation;
import io.github.wysohn.triggerreactor.core.bridge.IWorld;
import io.github.wysohn.triggerreactor.core.bridge.entity.IEntity;
import io.github.wysohn.triggerreactor.core.bridge.entity.IPlayer;
import io.github.wysohn.triggerreactor.core.config.InvalidTrgConfigurationException;
import io.github.wysohn.triggerreactor.core.main.TriggerReactorCore;
import io.github.wysohn.triggerreactor.core.manager.location.SimpleChunkLocation;
import io.github.wysohn.triggerreactor.core.manager.location.SimpleLocation;
import io.github.wysohn.triggerreactor.core.manager.trigger.AbstractTriggerManager;
import io.github.wysohn.triggerreactor.core.manager.trigger.ITriggerLoader;
import io.github.wysohn.triggerreactor.core.manager.trigger.Trigger;
import io.github.wysohn.triggerreactor.core.manager.trigger.TriggerInfo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LocationBasedTriggerManagerTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    ClickTriggerManager manager;
    ITriggerLoader<ClickTrigger> loader;
    TriggerReactorCore core;
    private TriggerInfo mockInfo;
    private ClickTrigger mockTrigger;

    @Before
    public void setUp() throws Exception {
        core = mock(TriggerReactorCore.class, RETURNS_DEEP_STUBS);
        Field instanceField = TriggerReactorCore.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, core);

        when(core.getExecutorManager().getBackedMap()).thenReturn(new HashMap<>());
        when(core.getPlaceholderManager().getBackedMap()).thenReturn(new HashMap<>());
        when(core.getVariableManager().getGlobalVariableAdapter()).thenReturn(new HashMap<>());
        when(core.getDataFolder()).thenReturn(folder.getRoot());

        loader = mock(ITriggerLoader.class);
        manager = new ClickTriggerManager(core, loader);
    }

    @Test
    public void reload() throws InvalidTrgConfigurationException {
        mockInfo = mock(TriggerInfo.class);
        mockTrigger = mock(ClickTrigger.class);

        when(mockInfo.getTriggerName()).thenReturn("world@1,2,3");
        when(loader.listTriggers(any(), any())).thenReturn(new TriggerInfo[]{mockInfo});
        when(loader.load(any())).thenReturn(mockTrigger);
        when(mockTrigger.getInfo()).thenReturn(mockInfo);

        manager.reload();
    }

    @Test
    public void getTriggerForLocation() throws InvalidTrgConfigurationException {
        reload();

        ClickTrigger trigger = manager.getTriggerForLocation(new SimpleLocation("world",
                                                                                1,
                                                                                2,
                                                                                3));
        assertEquals(mockTrigger, trigger);
    }

    @Test
    public void testGetTriggerForLocation() throws InvalidTrgConfigurationException {
        reload();

        TempLoc loc = new TempLoc("world", 1, 2, 3);
        ClickTrigger trigger = manager.getTriggerForLocation(loc);

        assertEquals(mockTrigger, trigger);
    }

    @Test
    public void startLocationSet() {
        IPlayer player = mock(IPlayer.class);
        UUID uuid = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(uuid);
        when(player.getName()).thenReturn("test");
        when(player.getLocation()).thenReturn(new TempLoc("world", 1, 2, 3));

        assertFalse(manager.isLocationSetting(player));

        manager.startLocationSet(player, "the script");
        assertTrue(manager.isLocationSetting(player));

        assertEquals("the script", manager.getSettingLocationScript(player));

        manager.stopLocationSet(player);
        assertFalse(manager.isLocationSetting(player));
    }

    @Test
    public void cutTrigger() throws InvalidTrgConfigurationException {
        reload();
        when(mockTrigger.clone()).thenReturn(mockTrigger);

        IPlayer player = mock(IPlayer.class);
        UUID uuid = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(uuid);
        when(player.getName()).thenReturn("test");
        when(player.getLocation()).thenReturn(new TempLoc("world", 1, 2, 3));

        assertTrue(manager.cutTrigger(player, new TempLoc("world", 1, 2, 3)));
        assertTrue(manager.pasteTrigger(player, new TempLoc("world", 1, 2, 4)));
        assertFalse(manager.pasteTrigger(player, new TempLoc("world", 1, 2, 4)));
        assertNull(manager.getTriggerForLocation(new TempLoc("world", 1, 2, 3)));
        assertNotNull(manager.getTriggerForLocation(new SimpleLocation("world", 1, 2, 4)));
    }

    @Test
    public void copyTrigger() throws InvalidTrgConfigurationException {
        reload();
        when(mockTrigger.clone()).thenReturn(mockTrigger);

        IPlayer player = mock(IPlayer.class);
        UUID uuid = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(uuid);
        when(player.getName()).thenReturn("test");
        when(player.getLocation()).thenReturn(new TempLoc("world", 1, 2, 3));

        assertTrue(manager.copyTrigger(player, new TempLoc("world", 1, 2, 3)));
        assertTrue(manager.pasteTrigger(player, new TempLoc("world", 1, 2, 4)));
        assertNotNull(manager.getTriggerForLocation(new SimpleLocation("world", 1, 2, 3)));
        assertNotNull(manager.getTriggerForLocation(new SimpleLocation("world", 1, 2, 4)));
    }

    @Test
    public void getTriggersInChunk() throws InvalidTrgConfigurationException {
        reload();

        Set<Map.Entry<SimpleLocation, Trigger>> result = manager.getTriggersInChunk(new SimpleChunkLocation("world", 0, 0));

        assertEquals(1, result.size());
        assertEquals(mockTrigger, result.iterator().next().getValue());
    }

    @Test
    public void handleLocationSetting() {
        IPlayer player = mock(IPlayer.class);
        UUID uuid = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(uuid);
        when(player.getName()).thenReturn("test");
        when(player.getLocation()).thenReturn(new TempLoc("world", 1, 2, 3));

        assertNull(manager.getTriggerForLocation(new SimpleLocation("world", 1, 2, 3)));

        manager.startLocationSet(player, "the script");
        assertTrue(manager.isLocationSetting(player));

        manager.handleLocationSetting(new TempLoc("world", 1, 2, 3), player);
        assertFalse(manager.isLocationSetting(player));

        assertNotNull(manager.getTriggerForLocation(new SimpleLocation("world", 1, 2, 3)));
    }

    @Test
    public void handleScriptEdit() throws InvalidTrgConfigurationException,
            AbstractTriggerManager.TriggerInitFailedException {
        reload();

        IPlayer player = mock(IPlayer.class);
        UUID uuid = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(uuid);
        when(player.getName()).thenReturn("test");
        when(player.getLocation()).thenReturn(new TempLoc("world", 1, 2, 3));

        manager.handleScriptEdit(player, mockTrigger);

        //TODO: EditManager need refactoring
//        verify(mockTrigger).setScript("the script");
    }

    @Test
    public void removeTriggerForLocation() throws InvalidTrgConfigurationException {
        reload();

        IPlayer player = mock(IPlayer.class);
        UUID uuid = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(uuid);
        when(player.getName()).thenReturn("test");
        when(player.getLocation()).thenReturn(new TempLoc("world", 1, 2, 3));

        manager.removeTriggerForLocation(new TempLoc("world", 1, 2, 3));
        assertNull(manager.getTriggerForLocation(new SimpleLocation("world", 1, 2, 3)));
    }

    @Test
    public void showTriggerInfo() throws InvalidTrgConfigurationException {
        reload();

        IPlayer player = mock(IPlayer.class);
        UUID uuid = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(uuid);
        when(player.getName()).thenReturn("test");
        when(player.getLocation()).thenReturn(new TempLoc("world", 1, 2, 3));

        manager.showTriggerInfo(player, new TempLoc("world", 1, 2, 3));
        verify(player, atLeastOnce()).sendMessage(anyString());
    }

    @Test
    public void onItemSwap() {
        IPlayer player = mock(IPlayer.class);
        UUID uuid = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(uuid);
        when(player.getName()).thenReturn("test");
        when(player.getLocation()).thenReturn(new TempLoc("world", 1, 2, 3));

        manager.startLocationSet(player, "the script");
        manager.onItemSwap(player);
        assertFalse(manager.pasteTrigger(player, new TempLoc("world", 1, 2, 3)));
    }

    @Test
    public void handleClick() throws InvalidTrgConfigurationException {
        reload();

        Object eventInstance = mock(Object.class);
        IBlock clickedBlock = mock(IBlock.class);
        IPlayer player = mock(IPlayer.class);
        UUID uuid = UUID.randomUUID();
        IItemStack itemInHand = mock(IItemStack.class);
        when(clickedBlock.getLocation()).thenReturn(new TempLoc("world", 1, 2, 3));
        when(player.getUniqueId()).thenReturn(uuid);
        when(player.getName()).thenReturn("test");
        when(player.getLocation()).thenReturn(new TempLoc("world", 1, 2, 3));

        manager.startLocationSet(player, "the script");
        manager.handleClick(eventInstance,
                            clickedBlock,
                            player,
                            itemInHand,
                            Activity.LEFT_CLICK_BLOCK);

        verify(mockTrigger).activate(eq(eventInstance), anyMap());
    }

    @Test
    public void handleWalk() throws InvalidTrgConfigurationException {
        reload();

        Object eventInstance = mock(Object.class);
        IBlock clickedBlock = mock(IBlock.class);
        IPlayer player = mock(IPlayer.class);
        UUID uuid = UUID.randomUUID();
        when(clickedBlock.getLocation()).thenReturn(new TempLoc("world", 1, 2, 3));
        when(player.getUniqueId()).thenReturn(uuid);
        when(player.getName()).thenReturn("test");
        when(player.getLocation()).thenReturn(new TempLoc("world", 1, 2, 3));

        manager.startLocationSet(player, "the script");
        manager.handleWalk(eventInstance,
                            player,
                            new SimpleLocation("world", 1, 3, 2),
                            new SimpleLocation("world", 1, 3, 3),
                            new TempBlock(new TempLoc("world", 1, 2, 3)));

        verify(mockTrigger).activate(eq(eventInstance), anyMap());
    }

    public static class TempLoc implements ILocation{
        private final String world;
        private final int x;
        private final int y;
        private final int z;

        public TempLoc(String world, int x, int y, int z) {
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public SimpleLocation toSimpleLocation() {
            return new SimpleLocation(world, x, y, z);
        }

        @Override
        public IWorld getWorld() {
            return new TempWorld(world);
        }

        @Override
        public <T> T get() {
            return null;
        }
    }

    public static class TempWorld implements IWorld{
        private final String name;

        public TempWorld(String name) {
            this.name = name;
        }

        @Override
        public Iterable<IEntity> getEntities() {
            return null;
        }

        @Override
        public IBlock getBlock(SimpleLocation clicked) {
            return null;
        }

        @Override
        public IBlock getBlock(ILocation location) {
            return new TempBlock(location);
        }
    }

    public static class TempBlock implements IBlock{
        private final ILocation location;

        public TempBlock(ILocation location) {
            this.location = location;
        }

        @Override
        public ILocation getLocation() {
            return location;
        }

        @Override
        public String getTypeName() {
            return "temptype";
        }

        @Override
        public <T> T get() {
            return null;
        }
    }
}