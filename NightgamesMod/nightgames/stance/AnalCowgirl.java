package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnalCowgirl extends AnalSexStance {

    public AnalCowgirl(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.anal);
    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return String.format("You're sitting on top of %s with your ass squeezing her cock.",
                            getBottom().nameDirectObject());
        } else {
            return String.format("%s flat on %s back with %s cock buried inside %s ass.",
                            getBottom().subjectAction("are", "is"), getBottom().possessiveAdjective(),
                            getBottom().possessiveAdjective(), getTop().nameOrPossessivePronoun());
        }
    }

    @Override
    public boolean mobile(Character c) {
        return c.getType() != bottom;
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
    public boolean behind(Character c) {
        return false;
    }

    @Override
    public boolean inserted(Character c) {
        return c.getType() == bottom;
    }

    @Override
    public Optional<Position> insertRandom(Combat c) {
        return Optional.of(new Mount(top, bottom));
    }

    @Override
    public String image() {
        return "anal_cowgirl.jpg";
    }

    @Override
    public Optional<Position> checkOngoing(Combat c) {
        Character inserter = inserted(getTop()) ? getTop() : getBottom();
        Character inserted = inserted(getTop()) ? getBottom() : getTop();

        Optional<Position> newStance = dickMissing(c, inserter, inserted);
        if (!newStance.isPresent()) {
            newStance = assholeMissing(c, inserter, inserted);
        }
        return newStance;
    }

    @Override
    public boolean anallyPenetrated(Combat combat, Character self) {
        return self.getType() == top;
    }

    @Override
    public List<BodyPart> topParts() {
        return Stream.of(getTop().body.getRandomAss()).filter(part -> part != null && part.present())
                        .collect(Collectors.toList());
    }

    @Override
    public List<BodyPart> bottomParts() {
        return Stream.of(getBottom().body.getRandomInsertable()).filter(part -> part != null && part.present())
                        .collect(Collectors.toList());
    }
    
    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        if (writeMessage) {
            c.write(getBottom(), Formatter
                            .format("{self:SUBJECT-ACTION:manage|manages} to unbalance {other:name-do} and push {other:direct-object} forward onto {other:possessive} hands and knees. {self:SUBJECT-ACTION:follow|follows} {other:direct-object}, still inside {other:possessive} tight ass, and {self:SUBJECT-ACTION:continue|continues} "
                                            + "to fuck {other:direct-object} from behind.", getBottom(), getTop()));
        }
        return new Anal(bottom, top);
    }
    
    @Override
    public int dominance() {
        return 4;
    }
}
