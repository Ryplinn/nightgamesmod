package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TribadismStance extends Position {
    public TribadismStance(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.trib);
    }

    @Override
    public String describe(Combat c) {
        return getTop().subjectAction("are", "is") + " holding " + getBottom().nameOrPossessivePronoun() + " legs across "
                        + getTop().possessiveAdjective() + " chest while grinding " + getTop().possessiveAdjective()
                        + " soaked cunt into " + getBottom().possessiveAdjective() + " pussy.";
    }

    @Override
    public boolean mobile(Character c) {
        return c.getType() != bottom;
    }

    @Override
    public boolean kiss(Character c, Character target) {
        return true;
    }

    @Override
    public boolean inserted(Character c) {
        return false;
    }

    @Override
    public String image() {
        return "trib.jpg";
    }

    @Override
    public boolean dom(Character c) {
        return c.getType() == top;
    }

    @Override
    public boolean sub(Character c) {
        return c.getType() == bottom;
    }

    @Override
    public boolean reachTop(Character c) {
        return true;
    }

    @Override
    public boolean reachBottom(Character c) {
        return c.getType() != bottom;
    }

    @Override
    public boolean prone(Character c) {
        return c.getType() == bottom;
    }

    @Override
    public boolean feet(Character c, Character target) {
        return false;
    }

    @Override
    public boolean oral(Character c, Character target) {
        return false;
    }

    @Override
    public boolean behind(Character c) {
        return false;
    }

    @Override
    public Optional<Position> insertRandom(Combat c) {
        return Optional.of(new Mount(top, bottom));
    }

    @Override
    public List<BodyPart> bottomParts() {
        return Stream.of(getBottom().body.getRandomPussy()).filter(part -> part != null && part.present())
                        .collect(Collectors.toList());
    }

    @Override
    public List<BodyPart> topParts() {
        return Stream.of(getTop().body.getRandomPussy()).filter(part -> part != null && part.present())
                        .collect(Collectors.toList());
    }

    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        if (writeMessage) {
            c.write(getBottom(), Formatter.format(
                            "In a desperate gamble for dominance, {self:subject} shakes {self:possessive} hips wildly, making {other:direct-object} yelp and breaking {other:possessive} concentration. "
                            + "Taking that chance, {self:pronoun-action:swing} {self:possessive} legs on top of {other:direct-object} and take control for {self:reflective}.",
                            getBottom(), getTop()));
        }
        return new TribadismStance(bottom, top);
    }

    @Override
    public double pheromoneMod(Character self) {
        if (self.getType() == bottom) {
            return 10;
        }
        return 2;
    }
    
    @Override
    public int dominance() {
        return 2;
    }

    @Override
    public int distance() {
        return 1;
    }

    private void strugglePleasure(Combat c, Character self, Character opponent) {
        int selfM = Random.random(6, 11);
        int targM = Random.random(6, 11);
        self.body.pleasure(opponent, opponent.body.getRandomPussy(), self.body.getRandomPussy(), selfM, c);
        opponent.body.pleasure(self, self.body.getRandomPussy(), opponent.body.getRandomPussy(), targM, c);
    }

    @Override
    public void struggle(Combat c, Character struggler) {
        Character opponent = getPartner(c, struggler);
        c.write(struggler, Formatter.format("{self:SUBJECT-ACTION:struggle} in {other:name-possessive} grip, "
                        + "but the slippery sensation of %s sexes sliding against each other distracts "
                        + "{self:direct-object} long enough for {other:pronoun} to "
                        + "regain {other:possessive} grip on {self:possessive} leg.",
                        struggler, opponent, c.bothPossessive(opponent)));
        strugglePleasure(c, struggler, opponent);
    }

    @Override
    public void escape(Combat c, Character escapee) {
        Character opponent = getPartner(c, escapee);
        c.write(escapee, Formatter.format("{self:SUBJECT-ACTION:attempt} to rock {self:possessive} hips wildly, "
                        + "hoping it will distract {other:name-do} long enough for {self:direct-object} to escape. "
                        + "Sadly, it doesn't accomplish much other than arousing the hell out of both of %s.",
                        escapee, opponent, c.bothDirectObject(opponent)));
        strugglePleasure(c, escapee, opponent);
    }
}
