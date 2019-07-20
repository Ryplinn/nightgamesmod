package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.skills.damage.DamageType;

import java.util.Optional;

public class FlyingCarry extends Position {

    public FlyingCarry(CharacterType succ, CharacterType target) {
        super(succ, target, Stance.flying);
        this.domType = DomType.MALEDOM;
    }

    @Override
    public String describe(Combat c) {
        return String.format(
                        "%s are flying some twenty feet up in the air,"
                                        + " joined to %s by %s hips. %s is on top of %s and %s %s is pumping into %s %s.",
                        spectated() ? String.format("%s and %s", getTop().subject(), getBottom().subject()) : "You",
                        spectated() ? "each other" : "your partner",
                        spectated() ? "their" : "your",
                        getTop().subject(), getBottom().subject(), getTop().possessiveAdjective(),
                        getTop().body.getRandomInsertable().describe(getTop()), getBottom().possessiveAdjective(),
                        getBottom().body.getRandomPussy().describe(getBottom()));
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
        return top.equals(c.getType());
    }

    @Override
    public boolean kiss(Character c, Character target) {
        return (c.getType() == top || c.getType() == bottom) && (target.getType() == top || target.getType() == bottom);
    }

    @Override
    public boolean dom(Character c) {
        return top.equals(c.getType());
    }

    @Override
    public boolean sub(Character c) {
        return bottom.equals(c.getType());
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

    @Override
    public boolean inserted(Character c) {
        return top.equals(c.getType());
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
                getTop().pain(c, getBottom(), (int) DamageType.physical.modifyDamage(getBottom(), getTop(), Random.random(50, 75)));
                return Optional.of(new Mount(bottom, top));
            } else {
                c.write(getTop().getName()
                                + " falls to the ground and so do you. Fortunately, her body cushions your fall, but you're not sure she appreciates that as much as you do.");
                getTop().pain(c, getBottom(), (int) DamageType.physical.modifyDamage(getBottom(), getTop(), Random.random(50, 75)));
                return Optional.of(new Mount(bottom, top));
            }
        } else {
            return super.checkOngoing(c);
        }
    }

    @Override
    public Optional<Position> insertRandom(Combat c) {
        return Optional.of(new StandingOver(top, bottom));
    }

    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        if (getBottom().body.getRandomWings() != null) {
            if (writeMessage) {
                c.write(getBottom(), Formatter.format(
                                "In a desperate gamble for dominance, {self:subject} rides {other:name-do} wildly, making {other:direct-object} gasp and breaking {other:possessive} concentration. Shaking off {other:possessive} strong arms, {self:subject-action:start|starts} flying on {self:possessive} own and starts riding {other:direct-object} with more control in the air.",
                                getBottom(), getTop()));
            }
            return new FlyingCowgirl(bottom, top);
        } else {
            if (writeMessage) {
                c.write("Weakened by {self:possessive} squirming, {other:SUBJECT-ACTION:fall|falls} to the ground and so {self:action:do|does} {self:name-do}. Fortunately, {other:possessive} body cushions {self:possessive} fall, but you're not sure {self:action:she appreciates that as much as you do|you appreciate that as much as her}. While {other:subject-action:are|is} dazed, {self:subject-action:mount|mounts} {other:direct-object} and {self:action:start|starts} riding {other:direct-object} in a cowgirl position.");
            }
            getTop().pain(c, getBottom(), (int) DamageType.physical.modifyDamage(getBottom(), getTop(), Random.random(50, 75)));
            return new Cowgirl(bottom, top);
        }
    }
    
    @Override
    public int dominance() {
        return 5;
    }
}
