package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MFMDoublePenThreesome extends Threesome {
    public MFMDoublePenThreesome(CharacterType domSexCharacter, CharacterType top, CharacterType bottom) {
        super(domSexCharacter, top, bottom, Stance.doggy);
        this.domType = DomType.MALEDOM;
    }

    @Override
    public boolean inserted(Character c) {
        return c.getType() == domSexCharacter || c.getType() == top;
    }

    @Override
    public boolean canthrust(Combat c, Character self) {
        return self.getType() == domSexCharacter || self.getType() == top;
    }

    @Override
    public float priorityMod(Character self) {
        return super.priorityMod(self) + 3;
    }

    @Override
    public List<BodyPart> partsForStanceOnly(Combat combat, Character self, Character other) {
        if (self == getDomSexCharacter() && other.getType() == bottom) {
            return topParts();
        } else if (self.getType() == top && other.getType() == bottom) {
            return Stream.of(getTop().body.getRandomInsertable()).filter(part -> part != null && part.present())
                            .collect(Collectors.toList());
        } else if (self.getType() == bottom) {
            if (other.getType() == top) {
                return Stream.of(getTop().body.getRandomAss()).filter(part -> part != null && part.present())
                                .collect(Collectors.toList());
            } else if (other.getType() == domSexCharacter) {
                return Stream.of(getTop().body.getRandomPussy()).filter(part -> part != null && part.present())
                                .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public boolean vaginallyPenetratedBy(Combat c, Character self, Character other) {
        return self.getType() == bottom && other.getType() == domSexCharacter;
    }

    @Override
    public boolean anallyPenetratedBy(Combat c, Character self, Character other) {        
        return self.getType() == bottom && other.getType() == top;
    }

    public Character getPartner(Combat c, Character self) {
        if (self.getType() == top) {
            return getBottom();
        } else if (self.getType() == domSexCharacter) {
            return getBottom();
        } else {
            return getDomSexCharacter();
        }
    }

    public List<Character> getAllPartners(Combat c, Character self) {
        if (self.getType() == bottom) {
            return Arrays.asList(getTop(), getDomSexCharacter());
        }
        return Collections.singletonList(getPartner(c, self));
    }

    @Override
    public String describe(Combat c) {
            return String.format("%s is fucking %s ass while %s pounding away at %s pussy.",
                            getTop().subject(), getBottom().nameOrPossessivePronoun(), getDomSexCharacter().subjectAction("are", "is"), getBottom().possessiveAdjective());
    }

    @Override
    public boolean mobile(Character c) {
        return c.getType() != bottom;
    }

    @Override
    public String image() {
        if (!getTop().useFemalePronouns()) {
            return "ThreesomeMFMDoublePen.jpg";
        }
        return "ThreesomeMFMDoublePenFuta.jpg";
    }

    @Override
    public boolean kiss(Character c, Character target) {
        return c.getType() != bottom && target.getType() != bottom;
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
        return c.getType() != bottom;
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
    public boolean behind(Character c) {
        return c.getType() == domSexCharacter;
    }

    @Override
    public Optional<Position> insertRandom(Combat c) {
        return Optional.of(new Mount(top, bottom));
    }

    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        if (writeMessage) {
            c.write(getBottom(), Formatter.format("{self:SUBJECT-ACTION:manage|manages} to unbalance {other:name-do} and push {other:direct-object} off {self:reflective}.", getBottom(), getTop()));
        }
        return new Neutral(bottom, top);
    }
    
    @Override
    public int dominance() {
        return 4;
    }

    @Override
    public Collection<Skill> availSkills(Combat c, Character self) {
        if (self.getType() != domSexCharacter) {
            return Collections.emptySet();
        } else {
            return self.getSkills().stream()
                            .filter(skill -> skill.requirements(c, self, getBottom()))
                            .filter(skill -> Skill.skillIsUsable(c, skill, self, getBottom()))
                            .filter(skill -> skill.type(c, self) == Tactics.fucking).collect(Collectors.toSet());
        }
    }
}
