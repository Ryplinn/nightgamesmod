package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FFXTribThreesome extends Threesome {
    public FFXTribThreesome(CharacterType domSexCharacter, CharacterType top, CharacterType bottom) {
        super(domSexCharacter, top, bottom, Stance.trib);
    }

    @Override
    public String describe(Combat c) {
        return getDomSexCharacter().subjectAction("are", "is") + " holding " + getBottom().nameOrPossessivePronoun() + " legs across "
                        + getTop().possessiveAdjective() + " lap while grinding " + getDomSexCharacter()
                        .possessiveAdjective()
                        + " soaked cunt into " + getBottom().possessiveAdjective() + " pussy.";
    }

    @Override
    public float priorityMod(Character self) {
        return super.priorityMod(self) + 3;
    }

    @Override public List<Character> getAllPartners(Combat c, Character self) {
        return ((Position) this).getAllPartners(c, self);
    }

    @Override
    public Optional<Position> checkOngoing(Combat c) {
        if (!c.otherCombatantsContains(getDomSexCharacter())) {
            c.write(getBottom(), Formatter.format("With the disappearance of {self:name-do}, {other:subject-action:manage|manages} to escape.", getDomSexCharacter(), getBottom()));
            return Optional.of(new Neutral(top, bottom));
        }
        return Optional.empty();
    }

    @Override
    public List<BodyPart> partsForStanceOnly(Combat combat, Character self, Character other) {
        if (self == getDomSexCharacter() && other.getType() == bottom) {
            return topParts();
        }
        return self.getType().equals(bottom) ? bottomParts() : Collections.emptyList();
    }

    public Character getPartner(Combat c, Character self) {
        Character domSex = getDomSexCharacter();
        if (self.getType() == top) {
            return getBottom();
        } else if (domSex == self) {
            return getBottom();
        } else {
            return domSex;
        }
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
        if (getTop().useFemalePronouns()) {
            return "ThreesomeFFFTrib.jpg";
        } else {
            return "ThreesomeMFFTrib.jpg";
        }
    }

    @Override
    public boolean dom(Character c) {
        return c.getType() == top || c.getType() == domSexCharacter;
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
        return true;
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
    public Position reverse(Combat c, boolean writeMessage) {
        if (writeMessage) {
            c.write(getBottom(), Formatter.format("{self:SUBJECT-ACTION:manage|manages} to unbalance {other:name-do} and push {other:direct-object} off {self:reflective}.", getBottom(), getTop()));
        }
        return new Neutral(bottom, top);
    }

    @Override
    public Collection<Skill> availSkills(Combat c, Character self) {
        if (self.getType() != domSexCharacter) {
            return Collections.emptySet();
        } else {
            return self.getSkills().stream()
                            .filter(skill -> skill.requirements(c, self, getBottom()))
                            .filter(skill -> Skill.skillIsUsable(c, skill, getBottom()))
                            .filter(skill -> skill.type(c) == Tactics.fucking).collect(Collectors.toSet());
        }
    }

    @Override
    public List<BodyPart> topParts() {
        return Stream.of(getTop().body.getRandomPussy()).filter(part -> part != null && part.present())
                        .collect(Collectors.toList());
    }

    @Override
    public double pheromoneMod(Character self) {
        return 3;
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
                        + "{self:direct-object} long enough for {other:pronoun} to regain"
                        + " {other:possessive} grip on {self:possessive} leg.",
                        struggler, opponent, c.bothPossessive(opponent)));
        strugglePleasure(c, struggler, opponent);
    }

    @Override
    public void escape(Combat c, Character escapee) {
        Character opponent = getPartner(c, escapee);
        c.write(escapee, Formatter.format("{self:SUBJECT-ACTION:attempt} to rock {self:possessive} hips wildly, "
                        + "hoping it will distract {other:name-do} long enough for {self:direct-object} to escape. "
                        + "Sadly, it doesn't accomplish much other than arousing the hell out of both of %s."
                        , escapee, opponent, c.bothDirectObject(opponent)));
        strugglePleasure(c, escapee, opponent);
    }
}
