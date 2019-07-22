package nightgames.gui;

import nightgames.characters.NPC;
import nightgames.characters.Player;
import nightgames.global.GameState;
import nightgames.global.TestGameState;
import nightgames.pet.FGoblin;
import nightgames.pet.FairyFem;
import nightgames.pet.PetCharacter;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Tests involving GUI colors.
 */
public class GUIColorTest {

    @Test public void characterColorTest() {
        GameState gameState = new TestGameState();
        gameState.init();
        Player summonerPlayer = new Player("SummonerPlayer");
        NPC summonerNPC = new NPC("SummonerNPC");
        PetCharacter playerPet = new FGoblin(summonerPlayer, 1, 1).getSelf();
        PetCharacter npcPet = new FairyFem(summonerNPC, 1, 1).getSelf();
        gameState.characterPool.putAll(summonerPlayer, summonerNPC);

        assertThat(GUIColor.characterColor(summonerPlayer), equalTo(GUIColor.PLAYER_COLOR));
        assertThat(GUIColor.characterColor(summonerNPC), equalTo(GUIColor.NPC_COLOR));
        assertThat(GUIColor.characterColor(playerPet), equalTo(GUIColor.PLAYER_PET_COLOR));
        assertThat(GUIColor.characterColor(npcPet), equalTo(GUIColor.NPC_PET_COLOR));
    }
}
