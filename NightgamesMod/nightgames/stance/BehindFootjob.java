package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;

public class BehindFootjob extends Position {
    public BehindFootjob(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.behindfootjob);
        this.facingType = FacingType.BEHIND;
    }

    @Override
    public String describe(Combat c) {
        return Formatter.format(
                        "{self:SUBJECT-ACTION:are|is} holding {other:name-do} from behind with {self:possessive} legs wrapped around {other:direct-object}",
                        getTop(), getBottom());
    }

    @Override
    public int pinDifficulty(Combat c, Character self) {
        return 6;
    }

    @Override
    public boolean mobile(Character c) {
        return c.getType() != bottom;
    }

    @Override
    public String image() {
        if (getBottom().hasDick()) {
            return "behind_footjob.jpg";
        } else {
            return "heelgrind.jpg";
        }
    }

    @Override
    public boolean kiss(Character c, Character target) {
        return c.getType() != top && c.getType() != bottom;
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
        return c.getType() != bottom;
    }

    @Override
    public boolean reachBottom(Character c) {
        return c.getType() != top && c.getType() != bottom;
    }

    @Override
    public boolean prone(Character c) {
        return false;
    }

    @Override
    public boolean feet(Character c, Character target) {
        return target.getType() == bottom;
    }

    @Override
    public boolean oral(Character c, Character target) {
        return target.getType() == bottom && c.getType() != top;
    }

    @Override
    public boolean behind(Character c) {
        return c.getType() == top;
    }

    @Override
    public boolean inserted(Character c) {
        return false;
    }

    @Override
    public float priorityMod(Character self) {
        return getSubDomBonus(self, 4.0f);
    }

    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        if (writeMessage) {
            c.write(getBottom(), Formatter.format(
                            "{self:SUBJECT-ACTION:summon} what little willpower {self:pronoun-action:have}"
                            + " left and {self:pronoun-action:grab} {other:name-possessive} feet and pull"
                            + " them off {self:name-possessive} crotch. Taking advantage"
                            + " of the momentum, {self:subject-action:push} {other:direct-object}"
                            + " back with {self:name-possessive} body and {self:action:hold} {other:direct-object}"
                            + " down while sitting on top of {other:direct-object}.",
                            getBottom(), getTop()));
        }
        return new ReverseMount(bottom, top);
    }

    @Override
    public double pheromoneMod(Character self) {
        return 1.5;
    }
    
    @Override
    public int dominance() {
        return 4;
    }

    @Override
    public int distance() {
        return 1;
    }

    @Override
    public void struggle(Combat c, Character struggler) {
        if (struggler.hasDick()) {
            c.write(struggler, Formatter.format("{self:SUBJECT-ACTION:attempt} to twist out of {other:name-possessive} grip, but "
                            + " {other:pronoun-action:wraps} {other:possessive} legs around {self:possessive} waist and steps "
                            + "on {self:possessive} cock hard, making {self:direct-object} yelp.", struggler, getTop()));
            struggler.body.pleasure(getTop(), getTop().body.getRandom("feet"), struggler.body.getRandomCock(), Random.random(6, 11), c);
        } else {
            c.write(struggler, Formatter.format("{self:SUBJECT-ACTION:attempt} to twist out of {other:name-possessive} grip, but "
                            + " {other:pronoun-action:wrap} {other:possessive} legs around {self:possessive} waist and digs {other:possessive} "
                            + "heels into {self:possessive} pussy, making {self:direct-object} yelp.", struggler, getTop()));
            struggler.body.pleasure(getTop(), getTop().body.getRandom("feet"), struggler.body.getRandomPussy(), Random
                            .random(6, 11), c);
        }
    }

    @Override
    public void escape(Combat c, Character escapee) {
        c.write(escapee, Formatter.format("{self:SUBJECT-ACTION:try} to escape {other:name-possessive} hold, but with"
                        + " {other:direct-object} behind {self:direct-object} with {other:possessive} long legs wrapped around {self:possessive} waist securely, there is nothing {self:pronoun} can do.",
                        escapee, getTop()));
    }
}
