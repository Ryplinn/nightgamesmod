package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.skills.damage.DamageType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Engulfed extends Position {

    private boolean slimePitches;

    public Engulfed(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.engulfed);
        slimePitches = slimePitches();
    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return "You have engulfed " + getBottom().getName() + " inside your slime body, with only "
                            + getBottom().possessiveAdjective() + " face outside of you.";
        } else {
            return String.format("%s is holding %s entire body inside "
                            + "%s slime body, with only %s face outside.",
                            getTop().nameOrPossessivePronoun(), getBottom().nameOrPossessivePronoun(),
                            getTop().possessiveAdjective(), getBottom().possessiveAdjective());
        }
    }

    @Override
    public int pinDifficulty(Combat c, Character self) {
        return 15;
    }

    @Override
    public boolean mobile(Character c) {
        return c.getType() != bottom;
    }

    @Override
    public String image() {
        if (getBottom().hasPussy()) {
            return "engulfed_f.jpg";
        } else {
            return "engulfed_m.jpg";
        }
    }

    @Override
    public boolean kiss(Character c, Character target) {
        return c.getType() == top || (target.getType() == top && c.getType() != bottom);
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
        return c.getType() == top;
    }

    @Override
    public boolean reachBottom(Character c) {
        return c.getType() == top;
    }

    @Override
    public boolean prone(Character c) {
        return false;
    }


    @Override
    public boolean feet(Character c, Character target) {
        return c.getType() == top;
    }

    @Override
    public boolean oral(Character c, Character target) {
        return c.getType() == top;
    }

    @Override
    public boolean behind(Character c) {
        return c.getType() == top;
    }

    @Override
    public boolean front(Character c) {
        return true;
    }

    @Override
    public boolean inserted(Character c) {
        return slimePitches == (c.getType() == top);
    }

    @Override
    public Optional<Position> insertRandom(Combat c) {
        return Optional.of(new Neutral(top, bottom));
    }

    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        if (getBottom().has(Trait.slime)) {
            if (writeMessage) {
                c.write(getBottom(), String.format("%s %s slimy body a"
                                + "round %s, reversing %s hold.",
                                getBottom().subjectAction("swirls", "swirl"), getBottom().possessiveAdjective(),
                                getTop().nameOrPossessivePronoun(), getTop().possessiveAdjective()));
            }
            return super.reverse(c, writeMessage);
        }
        if (writeMessage) {
            c.write(getBottom(), String.format("%s loose from %s slimy grip and %s away from %s.",
                            getBottom().subjectAction("struggles", "struggle"), getTop().nameOrPossessivePronoun(),
                            getBottom().action("stagger", "staggers"), getTop().directObject()));
        }
        return new Neutral(top, bottom);
    }

    @Override
    public void decay(Combat c) {
        time++;
        getBottom().weaken(c, (int) DamageType.stance.modifyDamage(getTop(), getBottom(), 5));
        getTop().emote(Emotion.dominant, 10);
    }

    @Override
    public float priorityMod(Character self) {
        return dom(self) ? 5 : 0;
    }

    @Override
    public List<BodyPart> topParts() {
        List<BodyPart> parts = new ArrayList<>();
        if (slimePitches) {
            parts.addAll(getTop().body.get("cock"));
        } else {
            parts.addAll(getTop().body.get("pussy"));
            parts.addAll(getTop().body.get("ass"));
        }
        return parts.stream()
                    .filter(part -> part != null && part.present())
                    .collect(Collectors.toList());
    }

    @Override
    public List<BodyPart> bottomParts() {
        List<BodyPart> parts = new ArrayList<>();
        if (!slimePitches) {
            parts.addAll(getBottom().body.get("cock"));
        } else {
            parts.addAll(getBottom().body.get("pussy"));
            parts.addAll(getBottom().body.get("ass"));
        }
        return parts.stream()
                    .filter(part -> part != null && part.present())
                    .collect(Collectors.toList());
    }

    @Override
    public boolean faceAvailable(Character target) {
        return target.getType() == top;
    }

    @Override
    public double pheromoneMod(Character self) {
        return 10;
    }

    private boolean slimePitches() {
        if (!getTop().hasDick())
            return false;
        if (!getBottom().hasDick())
            return true;
        return Random.random(2) == 0;
    }
    
    @Override
    public int dominance() {
        return 5;
    }
    
    @Override
    public int distance() {
        return 0;
    }

    private void pleasureRandomCombination(Combat c, Character self, Character opponent) {
        int selfM = Random.random(6, 11);
        int targM = Random.random(6, 11);
        List<Runnable> possibleActions = new ArrayList<>();
        if (opponent.hasDick()) {
            if (self.hasPussy()) {
                possibleActions.add(() -> {
                    opponent.body.pleasure(self, self.body.getRandomPussy(), opponent.body.getRandomCock(), selfM, c);
                    self.body.pleasure(opponent, opponent.body.getRandomCock(), self.body.getRandomPussy(), targM, c);
                });
            }
            possibleActions.add(() -> {
                opponent.body.pleasure(self, self.body.getRandomAss(), opponent.body.getRandomCock(), selfM, c);
                self.body.pleasure(opponent, opponent.body.getRandomCock(), self.body.getRandomAss(), targM, c);
            });
        }
        if (self.hasDick()) {
            if (opponent.hasPussy()) {
                possibleActions.add(() -> {
                    opponent.body.pleasure(self, self.body.getRandomCock(), opponent.body.getRandomPussy(), selfM, c);
                    self.body.pleasure(opponent, opponent.body.getRandomPussy(), self.body.getRandomCock(), targM, c);
                });
            }
            possibleActions.add(() -> {
                opponent.body.pleasure(self, self.body.getRandomCock(), opponent.body.getRandomAss(), selfM, c);
                self.body.pleasure(opponent, opponent.body.getRandomAss(), self.body.getRandomCock(), targM, c);
            });
        }
        Optional<Runnable> action = Random.pickRandom(possibleActions);
        action.ifPresent(Runnable::run);
    }

    @Override
    public void struggle(Combat c, Character struggler) {
        Character opponent = getPartner(c, struggler);
        c.write(struggler, Formatter.format("{self:SUBJECT-ACTION:attempt} to find {self:possessive} way out of "
                        + "the endless slimy hell {self:pronoun-action:have} found {self:reflective} in. "
                        + "However, none of {self:possessive} attempts make any purchase, as {other:possessive} formless body merely swallows "
                        + "{self:direct-object} back up when {self:pronoun-action:try}. "
                        + "All it really ends up accomplishing is some friction between {self:possessive} genitals and {other:poss-pronoun}.", struggler, opponent));
        pleasureRandomCombination(c, struggler, opponent);
    }

    @Override
    public void escape(Combat c, Character escapee) {
        Character opponent = getPartner(c, escapee);
        c.write(escapee, Formatter.format("{self:SUBJECT-ACTION:attempt} to talk {self:possessive} way out of "
                        + "the endless slimy hell {self:pronoun-action:have} found {self:reflective} in. "
                        + "However, none of {self:possessive} attempts to have {other:name-do} release {self:direct-object} does any good, "
                        + "as {other:pronoun} just stares at {self:direct-object} emotionlessly while teasing {self:possessive} lower half encased in {other:possessive} slime.", escapee, opponent));
        pleasureRandomCombination(c, escapee, opponent);
    }
}
