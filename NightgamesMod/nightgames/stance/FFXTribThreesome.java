package nightgames.stance;

import java.util.*;
import java.util.stream.Collectors;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;

public class FFXTribThreesome extends Position {
    private CharacterType domSexCharacter;

    public FFXTribThreesome(CharacterType domSexCharacter, CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.trib);
        this.domSexCharacter = domSexCharacter;
    }

    @Override
    public String describe(Combat c) {
        return getDomSexCharacter().subjectAction("are", "is") + " holding " + bottom.nameOrPossessivePronoun() + " legs across "
                        + top.possessiveAdjective() + " lap while grinding " + getDomSexCharacter()
                        .possessiveAdjective()
                        + " soaked cunt into " + bottom.possessiveAdjective() + " pussy.";
    }

    @Override
    public float priorityMod(Character self) {
        return super.priorityMod(self) + 3;
    }

    @Override
    public Character getDomSexCharacter() {
        return domSexCharacter;
    }

    @Override
    public Optional<Position> checkOngoing(Combat c) {
        if (!c.otherCombatantsContains(getDomSexCharacter())) {
            c.write(bottom, Formatter.format("With the disappearance of {self:name-do}, {other:subject-action:manage|manages} to escape.", getDomSexCharacter(), bottom));
            c.setStance(new Neutral(top, bottom));
        }
        return null;
    }

    @Override
    public void setOtherCombatants(List<? extends Character> others) {
        for (Character other : others) {
            if (other.equals(domSexCharacter)) {
                domSexCharacter = other;
            }
        }
    }

    @Override
    public List<BodyPart> partsForStanceOnly(Combat combat, Character self, Character other) {
        if (self == getDomSexCharacter() && other == bottom) {
            return topParts(combat);
        }
        return self.equals(bottom) ? bottomParts() : Collections.emptyList();
    }

    public Character getPartner(Combat c, Character self) {
        Character domSex = getDomSexCharacter();
        if (self == top) {
            return bottom;
        } else if (domSex == self) {
            return bottom;
        } else {
            return domSex;
        }
    }

    @Override
    public boolean mobile(Character c) {
        return c != bottom;
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
        if (top.useFemalePronouns()) {
            return "ThreesomeFFFTrib.jpg";
        } else {
            return "ThreesomeMFFTrib.jpg";
        }
    }

    @Override
    public boolean dom(Character c) {
        return c == top || c == domSexCharacter;
    }

    @Override
    public boolean sub(Character c) {
        return c == bottom;
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
        return c == bottom;
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
        return new Mount(top, bottom);
    }

    @Override
    public List<BodyPart> bottomParts() {
        return Arrays.asList(bottom.body.getRandomPussy()).stream().filter(part -> part != null && part.present())
                        .collect(Collectors.toList());
    }

    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        if (writeMessage) {
            c.write(bottom, Formatter.format("{self:SUBJECT-ACTION:manage|manages} to unbalance {other:name-do} and push {other:direct-object} off {self:reflective}.", bottom, top));
        }
        return new Neutral(bottom, top);
    }

    @Override
    public Collection<Skill> availSkills(Combat c, Character self) {
        if (self != domSexCharacter) {
            return Collections.emptySet();
        } else {
            Collection<Skill> avail = self.getSkills().stream()
                            .filter(skill -> skill.requirements(c, self, bottom))
                            .filter(skill -> Skill.skillIsUsable(c, skill, bottom))
                            .filter(skill -> skill.type(c) == Tactics.fucking).collect(Collectors.toSet());
            return avail;
        }
    }

    @Override
    public List<BodyPart> topParts(Combat c) {
        return Arrays.asList(top.body.getRandomPussy()).stream().filter(part -> part != null && part.present())
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
