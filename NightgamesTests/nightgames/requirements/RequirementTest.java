package nightgames.requirements;

import nightgames.actions.Movement;
import nightgames.areas.Area;
import nightgames.characters.Attribute;
import nightgames.characters.BlankPersonality;
import nightgames.characters.Emotion;
import nightgames.characters.NPC;
import nightgames.characters.body.CockPart;
import nightgames.characters.body.PussyPart;
import nightgames.characters.body.mods.FieryMod;
import nightgames.characters.body.mods.SizeMod;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Match;
import nightgames.items.Item;
import nightgames.modifier.standard.NoModifier;
import nightgames.stance.*;
import nightgames.status.Alert;
import nightgames.status.Stsflag;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static nightgames.requirements.RequirementShortcuts.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for Requirement functionality.
 */
public class RequirementTest {
    private static TrueRequirement trueReq() {
        return new TrueRequirement();
    }

    private static FalseRequirement falseReq() {
        return new FalseRequirement();
    }

    private NPC self;
    private NPC other;
    private Combat combat;

    @Before public void setUp() {
        self = new BlankPersonality("SelfTestNPC").character;
        other = new BlankPersonality("OtherTestNPC").character;
        Area area = new Area("TestArea", "TestArea description", Movement.beer);
        new Match(Arrays.asList(self, other), new NoModifier());
        combat = new Combat(self, other, area);
    }

    @Test public void analTest() {
        combat.setStance(new Anal(other, self));
        assertThat(anal().meets(combat, self, other), is(true));
        assertThat(anal().meets(combat, other, self), is(false));
    }

    @Test public void andTest() {
        // truth table tests
        assertThat(and().meets(combat, self, other), is(false));
        assertThat(and(trueReq()).meets(combat, self, other), is(true));
        assertThat(and(falseReq()).meets(combat, self, other), is(false));
        assertThat(and(trueReq(), trueReq()).meets(combat, self, other), is(true));
        assertThat(and(trueReq(), falseReq()).meets(combat, self, other), is(false));
        assertThat(and(falseReq(), trueReq()).meets(combat, self, other), is(false));
        assertThat(and(falseReq(), falseReq()).meets(combat, self, other), is(false));
        assertThat(and(trueReq(), trueReq(), trueReq()).meets(combat, self, other), is(true));
        assertThat(and(trueReq(), trueReq(), falseReq()).meets(combat, self, other), is(false));
        assertThat(and(trueReq(), falseReq(), trueReq()).meets(combat, self, other), is(false));
        assertThat(and(trueReq(), falseReq(), falseReq()).meets(combat, self, other), is(false));
        assertThat(and(falseReq(), trueReq(), trueReq()).meets(combat, self, other), is(false));
        assertThat(and(falseReq(), trueReq(), falseReq()).meets(combat, self, other), is(false));
        assertThat(and(falseReq(), falseReq(), trueReq()).meets(combat, self, other), is(false));
        assertThat(and(falseReq(), falseReq(), falseReq()).meets(combat, self, other), is(false));
    }

    @Test public void attributeTest() {
        self.att.put(Attribute.seduction, 20);
        other.att.put(Attribute.seduction, 18);
        AttributeRequirement req = attribute(Attribute.seduction, 19);
        assertThat(req.meets(combat, self, other), is(true));
        assertThat(req.meets(combat, other, self), is(false));
        other.att.put(Attribute.seduction, 19);
        assertThat(req.meets(combat, other, self), is(true));
    }

    @Test public void bodypartTest() {
        self.body.addReplace(PussyPart.generic, 1);
        other.body.addReplace(PussyPart.generic.applyMod(FieryMod.INSTANCE), 1);
        assertThat(bodypart("pussy").meets(combat, self, other), is(true));
        assertThat(bodypart("pussy").meets(combat, other, self), is(true));
        other.body.removeAll("pussy");
        assertThat(bodypart("pussy").meets(combat, other, self), is(false));
    }

    @Test public void domTest() {
        combat.setStance(new HeldOral(self, other));
        assertThat(dom().meets(combat, self, other), is(true));
        assertThat(dom().meets(combat, other, self), is(false));
    }

    @Test public void durationTest() {
        DurationRequirement duration = duration(5);
        assertThat(duration.meets(combat, self, other), is(true));
        duration.tick(4);
        assertThat(duration.meets(combat, self, other), is(true));
        duration.tick(1);
        assertThat(duration.meets(combat, self, other), is(false));
        duration.reset(-5);
        assertThat(duration.remaining() >= 0, is(true));
        assertThat(duration.meets(combat, self, other), is(false));
        duration.reset(10);
        assertThat(duration.meets(combat, self, other), is(true));
    }

    @Test public void insertedTest() {
        self.body.addReplace(new CockPart().applyMod(new SizeMod(SizeMod.COCK_SIZE_HUGE)), 1);
        combat.setStance(new FlyingCarry(self, other));
        assertThat(inserted().meets(combat, self, other), is(true));
        assertThat(inserted().meets(combat, other, self), is(false));
        assertThat(eitherinserted().meets(combat, self, other), is(true));
        assertThat(eitherinserted().meets(combat, other, self), is(true));
        combat.setStance(new Neutral(self, other));
        assertThat(eitherinserted().meets(combat, self, other), is(false));
        assertThat(eitherinserted().meets(combat, other, self), is(false));
    }

    @Test public void itemTest() {
        self.gain(Item.Beer, 6);
        other.gain(Item.Beer, 1);
        ItemRequirement sixpack = item(Item.Beer.amount(6));
        assertThat(sixpack.meets(combat, self, other), is(true));
        assertThat(sixpack.meets(combat, other, self), is(false));
        assertThat(sixpack.meets(null, self, null), is(true));
        self.consume(Item.Beer, 1);
        assertThat(sixpack.meets(combat, self, other), is(false));
        assertThat(sixpack.meets(null, self, null), is(false));
    }

    @Test public void levelTest() {
        LevelRequirement sophomore = level(2);
        assertThat(sophomore.meets(combat, self, other), is(false));
        assertThat(sophomore.meets(combat, other, self), is(false));
        self.addLevelsImmediate(null, 1);
        assertThat(sophomore.meets(combat, self, other), is(true));
        assertThat(sophomore.meets(combat, other, self), is(false));
    }

    @Test public void moodTest() {
        MoodRequirement inTheMood = mood(Emotion.horny);
        self.mood = Emotion.horny;
        other.mood = Emotion.nervous;
        assertThat(inTheMood.meets(combat, self, other), is(true));
        assertThat(inTheMood.meets(combat, other, self), is(false));
        self.emote(Emotion.dominant, 100);
        self.moodSwing(combat);
        other.emote(Emotion.horny, 100);
        other.moodSwing(combat);
        assertThat(inTheMood.meets(combat, self, other), is(false));
        assertThat(inTheMood.meets(combat, other, self), is(true));
    }

    @Test public void noneTest() {
        assertThat(none().meets(combat, self, other), is(true));
        assertThat(none().meets(combat, other, self), is(true));
    }

    @Test public void notTest() {
        assertThat(not(trueReq()).meets(combat, self, other), is(false));
        assertThat(not(trueReq()).meets(combat, other, self), is(false));
        assertThat(not(falseReq()).meets(combat, self, other), is(true));
        assertThat(not(falseReq()).meets(combat, other, self), is(true));
    }

    @Test public void orgasmTest() {
        assertThat(orgasms(1).meets(combat, self, other), is(false));
        assertThat(orgasms(1).meets(combat, other, self), is(false));
        self.doOrgasm(combat, other, self.body.getRandomPussy(), other.body.get("hands").get(0));
        assertThat(orgasms(1).meets(combat, self, other), is(true));
        assertThat(orgasms(1).meets(combat, other, self), is(false));
    }

    @Test public void orTest() {
        // truth table tests
        assertThat(or().meets(combat, self, other), is(false));
        assertThat(or(trueReq()).meets(combat, self, other), is(true));
        assertThat(or(falseReq()).meets(combat, self, other), is(false));
        assertThat(or(trueReq(), trueReq()).meets(combat, self, other), is(true));
        assertThat(or(trueReq(), falseReq()).meets(combat, self, other), is(true));
        assertThat(or(falseReq(), trueReq()).meets(combat, self, other), is(true));
        assertThat(or(falseReq(), falseReq()).meets(combat, self, other), is(false));
        assertThat(or(trueReq(), trueReq(), trueReq()).meets(combat, self, other), is(true));
        assertThat(or(trueReq(), trueReq(), falseReq()).meets(combat, self, other), is(true));
        assertThat(or(trueReq(), falseReq(), trueReq()).meets(combat, self, other), is(true));
        assertThat(or(trueReq(), falseReq(), falseReq()).meets(combat, self, other), is(true));
        assertThat(or(falseReq(), trueReq(), trueReq()).meets(combat, self, other), is(true));
        assertThat(or(falseReq(), trueReq(), falseReq()).meets(combat, self, other), is(true));
        assertThat(or(falseReq(), falseReq(), trueReq()).meets(combat, self, other), is(true));
        assertThat(or(falseReq(), falseReq(), falseReq()).meets(combat, self, other), is(false));
    }

    @Test public void positionTest() {
        self.body.addReplace(new CockPart(), 1);
        PositionRequirement flyfuck = position("FlyingCarry");
        combat.setStance(new FlyingCarry(self, other));
        assertThat(flyfuck.meets(combat, self, other), is(true));
        assertThat(flyfuck.meets(combat, other, self), is(true));
        assertThat(position("flying").meets(combat, self, other), is(false));
        combat.setStance(new Neutral(self, other));
        assertThat(flyfuck.meets(combat, self, other), is(false));
        assertThat(flyfuck.meets(combat, other, self), is(false));
    }

    @Test public void proneTest() {
        assertThat(prone().meets(combat, self, other), is(false));
        assertThat(prone().meets(combat, other, self), is(false));
        combat.setStance(new FaceSitting(self, other));
        assertThat(prone().meets(combat, self, other), is(false));
        assertThat(prone().meets(combat, other, self), is(true));
    }

    @Test public void resultTest() {
        ResultRequirement strappedOn = result(Result.strapon);
        assertThat(strappedOn.meets(combat, self, other), is(false));
        assertThat(strappedOn.meets(combat, other, self), is(false));
        combat.state = Result.strapon;
        assertThat(strappedOn.meets(combat, other, self), is(true));
        assertThat(strappedOn.meets(combat, self, other), is(true));
    }

    @Test public void reverseTest() {
        combat.setStance(new Anal(other, self));
        assertThat(rev(anal()).meets(combat, self, other), is(false));
        assertThat(rev(anal()).meets(combat, other, self), is(true));
    }

    @Test public void specificBodyPartTest() {
        self.body.addReplace(PussyPart.generic, 1);
        other.body.addReplace(PussyPart.generic.applyMod(FieryMod.INSTANCE), 1);
        SpecificBodyPartRequirement fierypussy = specificpart(PussyPart.generic.applyMod(FieryMod.INSTANCE));
        assertThat(fierypussy.meets(combat, self, other), is(false));
        assertThat(fierypussy.meets(combat, other, self), is(true));
        other.body.removeAll("pussy");
        assertThat(fierypussy.meets(combat, other, self), is(false));
    }

    @Test public void statusTest() {
        StatusRequirement caffeinated = status(Stsflag.alert);
        self.add(combat, new Alert(self));
        assertThat(caffeinated.meets(combat, self, other), is(true));
        assertThat(caffeinated.meets(combat, other, self), is(false));
    }

    @Test public void subTest() {
        combat.setStance(new HeldOral(self, other));
        assertThat(sub().meets(combat, self, other), is(false));
        assertThat(sub().meets(combat, other, self), is(true));
    }

    @Test public void traitTest() {
        self.add(Trait.alwaysready);
        TraitRequirement dtf = trait(Trait.alwaysready);
        assertThat(dtf.meets(combat, self, other), is(true));
        assertThat(dtf.meets(combat, other, self), is(false));
    }

    @Test public void winningEvenFootingTest() {
        assertThat(winning().meets(combat, self, other), is(false));
        assertThat(winning().meets(combat, other, self), is(false));
    }

    @Test public void winningSelfAheadTest() {
        self.getWillpower().setMax(100);
        self.getWillpower().set(100);
        other.getWillpower().setMax(200);
        other.getWillpower().set(100);
        assertThat(winning().meets(combat, self, other), is(true));
        assertThat(winning().meets(combat, other, self), is(false));
    }

    @Test public void winningOtherAheadTest() {
        self.getWillpower().setMax(100);
        self.getWillpower().set(50);
        other.getWillpower().setMax(50);
        other.getWillpower().set(50);
        assertThat(winning().meets(combat, self, other), is(false));
        assertThat(winning().meets(combat, other, self), is(true));
    }
}
