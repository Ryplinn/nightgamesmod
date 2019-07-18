package nightgames.combat;

import nightgames.actions.Movement;
import nightgames.areas.Area;
import nightgames.characters.BlankPersonality;
import nightgames.characters.CharacterType;
import nightgames.characters.NPC;
import nightgames.characters.trait.Trait;
import nightgames.global.GameState;
import nightgames.global.Match;
import nightgames.global.TestGameState;
import nightgames.modifier.standard.NoModifier;
import nightgames.stance.Stance;
import nightgames.stance.TestPosition;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

/**
 * Unit tests for combat methods.
 */
public class CombatTest {
    private NPC self;
    private NPC other;
    private Combat combat;

    @Before public void setUp() throws Exception {
        GameState testState = new TestGameState();
        self = new BlankPersonality("SelfTestNPC").character;
        other = new BlankPersonality("OtherTestNPC").character;
        Match.match = new Match(Arrays.asList(self, other), new NoModifier());
        Area area = new Area("TestArea", "TestArea description", Movement.beer);
        combat = new Combat(self, other, area);
    }

    @Test public void getDominanceOfStanceNoTraits() throws Exception {
        // Neutral position. No dominance involved, so neither character should lose willpower.
        combat.setStance(new TestPosition(self, other, Stance.neutral, 0));
        assertThat(combat.getStance().getDominanceOfStance(self), equalTo(0));
        assertThat(combat.getStance().getDominanceOfStance(other), equalTo(0));

        // Self is dominant. Other should lose willpower but self should not.
        combat.setStance(new TestPosition(self, other, Stance.engulfed, 5));
        assertThat(combat.getStance().getDominanceOfStance(self), equalTo(5));
        assertThat(combat.getStance().getDominanceOfStance(other), equalTo(0));

        // Negative position dominance. Not a valid dominance value, but we'll accept it and treat it like a neutral position.
        combat.setStance(new TestPosition(self, other, Stance.coiled, -5));
        assertThat(combat.getStance().getDominanceOfStance(self), equalTo(0));
        assertThat(combat.getStance().getDominanceOfStance(other), equalTo(0));
    }


    @Test public void getDominanceOfStanceSmqueen() throws Exception {
        self.add(Trait.smqueen);
        // Neutral position. No dominance involved, so neither character should lose willpower, regardless of traits.
        combat.setStance(new TestPosition(self, other, Stance.neutral, 0));
        assertThat(combat.getStance().getDominanceOfStance(self), equalTo(0));
        assertThat(combat.getStance().getDominanceOfStance(other), equalTo(0));

        // Self is dominant. Other should lose willpower but self should not. Trait increases effective stance dominance.
        combat.setStance(new TestPosition(self, other, Stance.engulfed, 5));
        assertThat(combat.getStance().getDominanceOfStance(self), equalTo(5));
        assertThat(combat.getStance().getDominanceOfStance(other), equalTo(0));

        // Negative position dominance. Not a valid dominance value, but we'll accept it and treat it like a neutral position.
        combat.setStance(new TestPosition(self, other, Stance.coiled, -5));
        assertThat(combat.getStance().getDominanceOfStance(self), equalTo(0));
        assertThat(combat.getStance().getDominanceOfStance(other), equalTo(0));
    }


    @Test public void getDominanceOfStanceSubmissive() throws Exception {
        self.add(Trait.submissive);
        // Neutral position. No dominance involved, so neither character should lose willpower, regardless of traits.
        combat.setStance(new TestPosition(self, other, Stance.neutral, 0));
        assertThat(combat.getStance().getDominanceOfStance(self), equalTo(0));
        assertThat(combat.getStance().getDominanceOfStance(other), equalTo(0));

        // Self is dominant. Other should lose willpower but self should not. Trait decreases effective stance dominance.
        combat.setStance(new TestPosition(self, other, Stance.engulfed, 5));
        assertThat(combat.getStance().getDominanceOfStance(self), equalTo(3));
        assertThat(combat.getStance().getDominanceOfStance(other), equalTo(0));

        // Negative position dominance. Not a valid dominance value, but we'll accept it and treat it like a neutral position.
        combat.setStance(new TestPosition(self, other, Stance.coiled, -5));
        assertThat(combat.getStance().getDominanceOfStance(self), equalTo(0));
        assertThat(combat.getStance().getDominanceOfStance(other), equalTo(0));
    }

    @Test public void mercyAfterCombatSelfLoses() throws Exception {
        self.getWillpower().empty();
        combat.runCombatNoDelay();
        assertTrue("There should be a winner of this combat", combat.winner.isPresent());
        assertThat("The winner should be OtherTestNPC", combat.winner.get().getName(), equalTo(other.getName()));
        assertFalse("SelfTestNPC lost to OtherTestNPC and should not be eligible for further combat.",
                        self.eligible(other));
    }

    @Test public void mercyAfterCombatSelfWins() throws Exception {
        other.getWillpower().empty();
        combat.runCombatNoDelay();
        assertTrue("There should be a winner of this combat", combat.winner.isPresent());
        assertThat("The winner should be SelfTestNPC", combat.winner.get().getName(), equalTo(self.getName()));
        assertFalse("OtherTestNPC lost to SelfTestNPC and should not be eligible for further combat.",
                        other.eligible(self));
    }

    @Test public void mercyAfterCombatDraw() throws Exception {
        self.getWillpower().empty();
        other.getWillpower().empty();
        combat.timer = Combat.NPC_TURN_LIMIT + 1;
        combat.runCombatNoDelay();
        assertTrue("The fight should be over", combat.isEnded() && combat.winner.isPresent());
        assertThat("The winner should be neither SelfTestNPC nor OtherTestNPC", combat.winner.get().getName(),
                        both(not(self.getName())).and(not(other.getName())));
        assertFalse("Both characters have lost and should be ineligible for further combat with each other",
                        self.eligible(other) || other.eligible(self));
    }
}
