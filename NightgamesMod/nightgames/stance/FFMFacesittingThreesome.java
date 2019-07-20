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

public class FFMFacesittingThreesome extends FFMCowgirlThreesome {
    public FFMFacesittingThreesome(CharacterType domSexCharacter, CharacterType top, CharacterType bottom) {
        super(domSexCharacter, top, bottom);
    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return "";
        } else {
            return Formatter.format("{self:SUBJECT-ACTION:are|is} pressing {self:POSSESSIVE} ass "
                            + "into {other:name-possessive} face while %s fucking {other:direct-object} in the Cowgirl position.", getTop(), getBottom(), getDomSexCharacter().subjectAction("are", "is"));
        }
    }

    @Override
    public List<BodyPart> partsForStanceOnly(Combat combat, Character self, Character other) {
        if (self == getDomSexCharacter() && other.getType() == bottom) {
            return topParts();
        }
        if (self.getType() == top) {
                return Stream.of(getTop().body.getRandomPussy()).filter(part -> part != null && part.present())
                                .collect(Collectors.toList());
        } else if (self.getType() == bottom) {
            if (other.getType() == top) {
                return Stream.of(getTop().body.getRandom("mouth")).filter(part -> part != null && part.present())
                                .collect(Collectors.toList());
            } else if (other.getType() == domSexCharacter) {
                return Stream.of(getTop().body.getRandomInsertable()).filter(part -> part != null && part.present())
                                .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }
    
    @Override
    public Optional<Position> checkOngoing(Combat c) {
        if (!c.otherCombatantsContains(getDomSexCharacter())) {
            c.write(getBottom(), Formatter.format(
                            "With the disappearance of {self:name-do}, {other:subject-action:manage|manages} to escape.",
                            getDomSexCharacter(), getBottom()));
            return Optional.of(new Neutral(top, bottom));
        }
        return null;
    }

    public List<Character> getAllPartners(Combat c, Character self) {
        if (self.getType() == bottom) {
            return Arrays.asList(getTop(), getDomSexCharacter());
        }
        return Collections.singletonList(getPartner(c, self));
    }

    @Override
    public String image() {
        return "ThreesomeFFMFacesitting.jpg";
    }

    @Override
    public float priorityMod(Character self) {
        return super.priorityMod(self) + 3;
    }


    @Override
    public boolean kiss(Character c, Character target) {
        return c.getType() != bottom && target.getType() != bottom;
    }

    @Override
    public int dominance() {
        return 5;
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
    public double pheromoneMod(Character self) {
        if (self.getType() == top) {
            return 10;
        } else if (self.getType() == domSexCharacter || self.getType() == bottom) {
            return 3;
        }
        return super.pheromoneMod(self);
    }

    public boolean isFaceSitting(Character self) {
        return self.getType() == top;
    }

    public boolean isFacesatOn(Character self) {
        return self.getType() == bottom;
    }
}
