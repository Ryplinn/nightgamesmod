package nightgames.global;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for DebugFlags.
 */
public class DebugFlagsTest {
    @Before public void setUp() {
        DebugFlags.debug = new boolean[DebugFlags.values().length];
        DebugFlags.debugSimulation = 0;
    }

    @Test public void parseOneFlag() throws Exception {
        String[] args = {"DEBUG_SCENE"};
        DebugFlags.parseDebugFlags(args);
        assertTrue(DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE));
        assertFalse(DebugFlags.isDebugOn(DebugFlags.DEBUG_INITIATIVE));
    }

    @Test public void parseMultipleFlags() throws Exception {
        String[] args = {"DEBUG_INITIATIVE", "DEBUG_GUI", "DEBUG_LOADING"};
        DebugFlags.parseDebugFlags(args);
        assertTrue(DebugFlags.isDebugOn(DebugFlags.DEBUG_INITIATIVE));
        assertTrue(DebugFlags.isDebugOn(DebugFlags.DEBUG_GUI));
        assertTrue(DebugFlags.isDebugOn(DebugFlags.DEBUG_LOADING));
        assertFalse(DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE));
    }

    @Test public void noFlags() throws Exception {
        String[] args = {};
        DebugFlags.parseDebugFlags(args);
        for (DebugFlags flag : DebugFlags.values()) {
            assertFalse(DebugFlags.isDebugOn(flag));
        }
    }

    @Test(expected = DebugFlags.UnknownDebugFlagException.class) public void invalidFlag() throws Exception {
        String[] args = {"foo", "bar", "baz", "DEBUG_STRATEGIES"};
        try {
            DebugFlags.parseDebugFlags(args);
        } finally {
            assertTrue(DebugFlags.isDebugOn(DebugFlags.DEBUG_STRATEGIES));
            assertFalse(DebugFlags.isDebugOn(DebugFlags.DEBUG_GUI));
        }
    }
}
