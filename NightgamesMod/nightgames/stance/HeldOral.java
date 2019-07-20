package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class HeldOral extends Position {
    public HeldOral(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.oralpin);
        facingType = FacingType.FACING;
    }

    @Override
    public String describe(Combat c) {
        return Formatter.format(
                        "{self:SUBJECT-ACTION:are|is} holding {other:name-do} down with {self:possessive} face nested between {other:possessive} legs.",
                        getTop(), getBottom());
    }

    @Override
    public boolean mobile(Character c) {
        return c.getType() != bottom && c.getType() != top;
    }

    @Override
    public boolean getUp(Character c) {
        return c.getType() == top;
    }
    public List<BodyPart> topParts() {
        BodyPart part = getTop().body.getRandom("mouth");
        if (part != null) {
            return Collections.singletonList(part);
        } else {
            return Collections.emptyList();
        }
    }

    public List<BodyPart> bottomParts() {
        if (getBottom().hasDick()) {
            return Collections.singletonList(getBottom().body.getRandom("cock"));
        } else if (getBottom().hasPussy()){
            return Collections.singletonList(getBottom().body.getRandomPussy());
        }
        return Collections.emptyList();
    }

    @Override
    public String image() {
        if (getBottom().hasDick()) {
            return "oralhold_fm.jpg";
        } else if (getBottom().hasPussy() && getTop().hasPussy()) {
            return "oralhold_ff.jpg";
        } else if (getBottom().hasPussy()) {
            return "oralhold_mf.jpg";
        }
        return "err.jpg";
    }

    @Override
    public boolean kiss(Character c, Character target) {
        return c.getType() != top && target.getType() != top;
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
        return false;
    }

    @Override
    public boolean reachBottom(Character c) {
        return false;
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
        return c.getType() == top;
    }

    @Override
    public boolean behind(Character c) {
        return false;
    }

    @Override
    public boolean inserted(Character c) {
        if (getBottom().hasDick()) {
            return c.getType() == bottom;
        } else {
            return false;
        }
    }

    @Override
    public float priorityMod(Character self) {
        float bonus = getSubDomBonus(self, 2);
        bonus += self.body.getRandom("mouth").priority(self);
        return bonus;
    }

    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        return new Mount(bottom, top);
    }

    @Override
    public boolean faceAvailable(Character target) {
        return target.getType() == bottom;
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
        return 3;
    }

    @Override
    public int distance() {
        return 1;
    }

    private void pleasureRandomCombination(Combat c, Character self, Character opponent, String pussyString, String cockString) {
        int targM = Random.random(6, 11);
        List<Runnable> possibleActions = new ArrayList<>();
        if (self.hasPussy()) {
            possibleActions.add(() -> {
                c.write(self, Formatter.format(pussyString, self, opponent));
                self.body.pleasure(opponent, opponent.body.getRandom("mouth"), self.body.getRandomPussy(), targM, c);
            });
        }
        if (self.hasDick()) {
            possibleActions.add(() -> {
                c.write(self, Formatter.format(cockString, self, opponent));
                self.body.pleasure(opponent, opponent.body.getRandom("mouth"), self.body.getRandomCock(), targM, c);
            });
        }
        Optional<Runnable> action = Random.pickRandom(possibleActions);
        action.ifPresent(Runnable::run);
    }

    @Override
    public void struggle(Combat c, Character struggler) {
        Character opponent = getPartner(c, struggler);
        pleasureRandomCombination(c, struggler, opponent,
                        "{self:SUBJECT-ACTION:try} to peel {other:name-do} off {self:possessive} legs, "
                        + "but {other:pronoun-action:hold} on tightly. "
                      + "After thoroughly exhausting {self:possessive} attempts, {other:pronoun-action:smile} smugly "
                      + "and {other:action:give} {self:possessive} clit "
                      + "a victorious little lick.", 
    
                        "{self:SUBJECT-ACTION:try} to peel {other:name-do} off {self:possessive} legs,"
                        + " but {other:pronoun-action:hold} on tightly. "
                      + "After thoroughly exhausting {self:possessive} attempts, {other:pronoun-action:smile} smugly"
                      + " and {other:action:run} {other:possessive} tongue "
                      + "along {self:possessive} shaft to demonstrate {other:possessive} victory.");
    }

    @Override
    public void escape(Combat c, Character escapee) {
        Character opponent = getPartner(c, escapee);
        pleasureRandomCombination(c, escapee, opponent,
                        "{self:SUBJECT-ACTION:try} to escape {other:name-possessive} grip on {self:possessive} waist,"
                        + " but {other:pronoun-action:hold} on tightly. "
                      + "After thoroughly exhausting every angle, {self:pronoun} can only give up in defeat. "
                      + "{other:PRONOUN-ACTION:smile} smugly and {other:action:give} {self:possessive} clit "
                      + "a victorious little lick.", 
    
                        "{self:SUBJECT-ACTION:try} to peel {other:name-possessive} grip on {self:possessive} waist,"
                        + " but {other:pronoun-action:hold} on tightly. "
                      + "After thoroughly exhausting every angle, {self:pronoun} can only give up in defeat. "
                      + "{other:PRONOUN-ACTION:smile} smugly and {other:action:run} {other:possessive} tongue "
                      + "along {self:possessive} shaft to demonstrate {other:possessive} victory.");
    }
}
