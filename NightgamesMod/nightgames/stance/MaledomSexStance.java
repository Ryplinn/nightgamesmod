package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.status.Stsflag;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MaledomSexStance implements DomHelper {
    @Override
    public float priorityMod(Character self, Position position) {
        float priority = 0;
        priority += position.getSubDomBonus(self, 4.0f);
        if (self.hasPussy()) {
            priority += self.body.getRandomPussy().priority(self);
        }
        if (self.hasDick()) {
            priority += self.body.getRandomCock().priority(self);
        }
        return priority;
    }

    @Override
    public Optional<Position> checkOngoing(Combat c, Position position) {
        Character inserter = inserted(position.getTop(), position) ? position.getTop() : position.getBottom();
        Character inserted = inserted(position.getTop(), position) ? position.getBottom() : position.getTop();

        Optional<Position> newStance = position.dickMissing(c, inserter, inserted);
        if (!newStance.isPresent()) {
            newStance = position.pussyMissing(c, inserter, inserted);
        }
        return newStance;
    }

    @Override
    public boolean oral(Character c, Character target, Position position) {
        return false;
    }

    @Override
    public boolean inserted(Character c, Position position) {
        return c.getType() == position.top;
    }

    @Override
    public boolean feet(Character c, Character target, Position position) {
        return false;
    }

    @Override
    public List<BodyPart> topParts(Position position) {
        return Stream.of(position.getDomSexCharacter().body.getRandomInsertable()).filter(part -> part != null && part.present())
                        .collect(Collectors.toList());
    }

    @Override
    public List<BodyPart> bottomParts(Position position) {
        return Stream.of(position.getBottom().body.getRandomPussy()).filter(part -> part != null && part.present())
                        .collect(Collectors.toList());
    }

    @Override
    public double pheromoneMod(Character self, Position position) {
        return 2;
    }
    
    @Override
    public int distance(Position position) {
        return 1;
    }

    @Override
    public void struggle(Combat c, Character struggler, Position position) {
        Character opponent = position.getPartner(c, struggler);
        boolean knotted = opponent.is(Stsflag.knotted);

        int selfM = Random.random(6, 11);
        int targM = Random.random(6, 11);
        if (knotted) {
            c.write(struggler,
                            Formatter.format("{self:SUBJECT-ACTION:struggle} fruitlessly against the lump of {other:name-possessive} knotted cock, "
                                            + "arousing the hell out of both of %s in the process.",
                            struggler, opponent, c.bothDirectObject(opponent)));
            selfM += 5;
        } else {
            c.write(struggler, Formatter.format("{self:SUBJECT-ACTION:try} to tip {other:name-do} off balance, but {other:pronoun-action:grip} {other:possessive} hips firmly, "
                            + "pushing {other:possessive} cock deep inside {self:direct-object} and pinning {self:direct-object} to the floor. "
                            + "The sensations from wrestling with {other:possessive} cock buried inside {self:direct-object} almost make {self:direct-object} cum.", struggler, opponent));
        }

        struggler.body.pleasure(opponent, opponent.body.getRandomInsertable(), struggler.body.getRandomPussy(), selfM, c);
        if (!opponent.has(Trait.strapped)) {
            opponent.body.pleasure(struggler, struggler.body.getRandomPussy(), opponent.body.getRandomCock(), targM, c);            
        }
    }

    @Override
    public void escape(Combat c, Character escapee, Position position) {
        Character opponent = position.getPartner(c, escapee);
        boolean knotted = opponent.is(Stsflag.knotted);

        int selfM = Random.random(6, 11);
        int targM = Random.random(6, 11);
        if (knotted) {
            c.write(escapee,
                            Formatter.format("{self:SUBJECT-ACTION:tickle} {other:name-do} and {self:action:try} to escape with {other:direct-object} distracted. "
                                            + "Problem is, the knot in {other:possessive} {other:body-part:cock} makes trying to pull out a arousing yet futile task.",
                            escapee, opponent));
            selfM += 5;
        } else {
            c.write(escapee, Formatter.format("{self:SUBJECT-ACTION:try} to escape {other:name-possessive} relentless pounding, "
                            + "but {other:pronoun-action:grip} {other:possessive} hips firmly, pushing {other:possessive} cock deep inside {self:direct-object} once again "
                            + "and pinning {self:direct-object} to the floor. "
                            + "The sensations from moving around so much with {other:possessive} cock buried inside {self:direct-object} almost make {self:direct-object} cum.", escapee, opponent));
        }

        escapee.body.pleasure(opponent, opponent.body.getRandomInsertable(), escapee.body.getRandomPussy(), selfM, c);
        if (!opponent.has(Trait.strapped)) {
            opponent.body.pleasure(escapee, escapee.body.getRandomPussy(), opponent.body.getRandomCock(), targM, c);            
        }
    }
}
