package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.skills.damage.DamageType;

import java.util.Optional;

public class FlyingCowgirl extends Position {

    public FlyingCowgirl(CharacterType succ, CharacterType target) {
        super(succ, target, Stance.flying);
        this.domType = DomType.FEMDOM;
    }

    @Override
    public String describe(Combat c) {
        return String.format(
                        "%s are flying some twenty feet up in the air,"
                                        + " joined to %s by %s hips. %s on top of %s and %s %s is strangling %s %s.",
                                        spectated() ? String.format("%s and %s", getTop().subject(), getBottom().subject()) : "You",
                                                        spectated() ? "each other" : "your partner",
                                                        spectated() ? "their" : "your",
                        getTop().subjectAction("are", "is"), getBottom().subject(), getTop().possessiveAdjective(),
                        getTop().body.getRandomPussy().describe(getTop()), getBottom().possessiveAdjective(),
                        getBottom().body.getRandomInsertable().describe(getBottom()));
    }
    
    private boolean spectated() {
        return !(getTop().human() || getBottom().human());
    }

    @Override
    public String image() {
        return "flying.jpg";
    }

    @Override
    public boolean mobile(Character c) {
        return c.getType() == top;
    }

    @Override
    public boolean kiss(Character c, Character target) {
        return (c.getType() == top || c.getType() == bottom) && (target.getType() == top || target.getType() == bottom);
    }

    @Override
    public boolean dom(Character c) {
        return c.getType() == top;
    }

    @Override
    public boolean sub(Character c) {
        return c.getType() == top;
    }

    @Override
    public boolean reachTop(Character c) {
        return c.getType() == top || c.getType() == bottom;
    }

    @Override
    public boolean reachBottom(Character c) {
        return c.getType() == top;
    }

    @Override
    public boolean prone(Character c) {
        return c.getType() == bottom;
    }

    @Override
    public boolean behind(Character c) {
        return false;
    }

    public boolean flying() {
        return true;
    }

    @Override
    public void decay(Combat c) {
        time++;
        getTop().weaken(c, (int) DamageType.stance.modifyDamage(getBottom(), getTop(), 3));
    }

    @Override
    public Optional<Position> checkOngoing(Combat c) {
        if (getTop().getStamina().get() < 5) {
            if (getTop().human()) {
                c.write("You're too tired to stay in the air. You plummet to the ground and " + getBottom().getName()
                                + " drops on you heavily, knocking the wind out of you.");
            } else {
                c.write(getTop().getName()
                                + " falls to the ground and so do you. Fortunately, her body cushions your fall, but you're not sure she appreciates that as much as you do.");
            }
            getTop().pain(c, getBottom(), (int) DamageType.physical.modifyDamage(getBottom(), getTop(), Random.random(50, 75)));
            return Optional.of(new Mount(bottom, top));
        } else {
            return super.checkOngoing(c);
        }
    }

    @Override
    public Optional<Position> insertRandom(Combat c) {
        return Optional.of(new Mount(top, bottom));
    }

    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        if (getBottom().body.getRandomWings() != null) {
            if (writeMessage) {
                c.write(getBottom(), Formatter.format(
                                "In a desperate gamble for dominance, {self:subject-action:piston|pistons} wildly into {other:name-do}, making {other:direct-object} yelp and breaking {other:possessive} concentration. Shaking off {other:possessive} limbs coiled around {self:subject}, {self:subject-action:start|starts} flying on {self:possessive} own and starts fucking {other:direct-object} back in the air.",
                                getBottom(), getTop()));
            }
            return new FlyingCarry(bottom, top);
        } else {
            if (writeMessage) {
                c.write(Formatter.format("Weakened by {self:possessive} squirming, {other:SUBJECT-ACTION:fall|falls} to the ground and so {self:action:do|does} {self:name-do}. Fortunately, {other:possessive} body cushions {self:possessive} fall, but you're not sure {self:action:if she appreciates that as much as you do|if you appreciate that as much as she does}. "
                                + "While {other:subject-action:are|is} dazed, {self:subject-action:mount|mounts} {other:direct-object} and {self:action:start|starts} fucking {other:direct-object} in a missionary position.", getBottom(), getTop()));
            }
            getTop().pain(c, getBottom(), (int) DamageType.physical.modifyDamage(getBottom(), getTop(), Random.random(50, 75)));
            return new Missionary(bottom, top);
        }
    }
    
    @Override
    public int dominance() {
        return 6;
    }
}
