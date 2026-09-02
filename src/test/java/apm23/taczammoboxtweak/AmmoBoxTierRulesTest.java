package apm23.taczammoboxtweak;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AmmoBoxTierRulesTest {
    @Test
    void capacitiesAreAbsolutePerTier() {
        assertEquals(1500, AmmoBoxTierRules.capacityForLevel(0));
        assertEquals(4000, AmmoBoxTierRules.capacityForLevel(1));
        assertEquals(6000, AmmoBoxTierRules.capacityForLevel(2));
        assertEquals(10000, AmmoBoxTierRules.capacityForLevel(3));
    }

    @Test
    void higherUnknownLevelsStayAtLevel2Capacity() {
        assertEquals(10000, AmmoBoxTierRules.capacityForLevel(4));
    }

    @Test
    void level2UsesDiamondOpenAndFilledModelStates() {
        assertTrue(AmmoBoxTierRules.isDiamondLevel2(3));
        assertFalse(AmmoBoxTierRules.isDiamondLevel2(2));
        assertEquals(4, AmmoBoxTierRules.diamondModelState(true));
        assertEquals(5, AmmoBoxTierRules.diamondModelState(false));
    }
}
