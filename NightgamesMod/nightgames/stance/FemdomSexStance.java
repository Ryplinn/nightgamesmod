package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.status.CockBound;
import nightgames.status.Stsflag;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FemdomSexStance implements DomHelper {
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
        Character inserter = inserted(position.getDomSexCharacter(), position) ? position.getDomSexCharacter() : position.getBottom();
        Character inserted = inserted(position.getDomSexCharacter(), position) ? position.getBottom() : position.getDomSexCharacter();

        Optional<Position> newStance = position.dickMissing(c, inserter, inserted);
        if (!newStance.isPresent()) {
            newStance = position.pussyMissing(c, inserter, inserted);
        }
        return newStance;
    }

    @Override
    public List<BodyPart> topParts(Position position) {
        return Stream.of(position.getDomSexCharacter().body.getRandomPussy()).filter(part -> part != null && part.present())
                        .collect(Collectors.toList());
    }

    @Override
    public List<BodyPart> bottomParts(Position position) {
        return Stream.of(position.getBottom().body.getRandomInsertable()).filter(part -> part != null && part.present())
                        .collect(Collectors.toList());
    }

    @Override
    public boolean inserted(Character c, Position position) {
        return c.getType() == position.bottom;
    }

    @Override
    public boolean oral(Character c, Character target, Position position) {
        return false;
    }

    @Override
    public boolean feet(Character c, Character target, Position position) {
        return false;
    }

    @Override
    public double pheromoneMod(Character self, Position position) {
        return 3;
    }
    @Override
    public int distance(Position position) {
        return 1;
    }

    @Override
    public void struggle(Combat c, Character struggler, Position position) {
        Character opponent = position.getPartner(c, struggler);
        boolean cockbound = opponent.is(Stsflag.cockbound);

        int selfM = Random.random(6, 11);
        int targM = Random.random(6, 11);
        if (cockbound) {
            CockBound s = (CockBound) struggler.getStatus(Stsflag.cockbound);
            c.write(struggler,
                            Formatter.format("{self:SUBJECT-ACTION:try|tries} to struggle out of {other:possessive} iron grip on {self:possessive} dick. However, {other:possessive} "
                                            + s.binding
                                            + " has other ideas. {other:SUBJECT-ACTION:run|runs} {other:possessive} "
                                            + s.binding
                                            + " up and down {self:possessive} cock and leaves {self:direct-object} gasping with pleasure.",
                            struggler, opponent));
            selfM += 5;
        } else {
            c.write(struggler, Formatter.format("{self:SUBJECT-ACTION:try} to tip {other:name-do} off balance, but {other:pronoun-action:drop} {other:possessive} hips firmly, "
                            + "pushing {self:possessive} cock deep inside {other:reflective} and pinning {self:direct-object} to the floor. "
                            + "The sensations from wrestling with {self:possessive} cock buried inside {other:direct-object} almost make {self:direct-object} cum.", struggler, opponent));
        }
        if (!struggler.has(Trait.strapped)) {
            struggler.body.pleasure(opponent, opponent.body.getRandomPussy(), struggler.body.getRandomCock(), selfM, c);
        }
        opponent.body.pleasure(struggler, struggler.body.getRandomInsertable(), opponent.body.getRandomPussy(), targM, c);
    }

    @Override
    public void escape(Combat c, Character escapee, Position position) {
        Character opponent = position.getPartner(c, escapee);
        boolean cockbound = opponent.is(Stsflag.cockbound);

        int selfM = Random.random(6, 11);
        int targM = Random.random(6, 11);
        if (cockbound) {
            CockBound s = (CockBound) escapee.getStatus(Stsflag.cockbound);
            c.write(escapee,
                            Formatter.format("{self:SUBJECT-ACTION:try|tries} to escape {other:possessive} iron grip on {self:possessive} dick. However, {other:possessive} "
                                            + s.binding
                                            + " has other ideas. {other:SUBJECT-ACTION:run|runs} {other:possessive} "
                                            + s.binding
                                            + " up and down {self:possessive} cock and leaves {self:direct-object} gasping with pleasure.",
                            escapee, opponent));
            selfM += 5;
        } else {
            c.write(escapee, Formatter.format("{self:SUBJECT-ACTION:attempt} to escape {other:name-possessive} embrace, "
                            + "but {other:pronoun-action:drop} {other:possessive} hips firmly, pushing {self:possessive} "
                            + "cock deep inside {other:reflective} and pinning {self:direct-object} to the floor. "
                            + "The sensations from moving around so much with {self:possessive} cock buried inside {other:direct-object} almost make {self:direct-object} cum.", escapee, opponent));
        }
        if (!escapee.has(Trait.strapped)) {
            escapee.body.pleasure(opponent, opponent.body.getRandomPussy(), escapee.body.getRandomCock(), selfM, c);
        }
        opponent.body.pleasure(escapee, escapee.body.getRandomInsertable(), opponent.body.getRandomPussy(), targM, c);
    }
}
