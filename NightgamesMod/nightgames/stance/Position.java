package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.Body;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.skills.Skill;
import nightgames.status.InsertedStatus;
import nightgames.utilities.ProseUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Position implements Cloneable {
    public CharacterType top;
    public CharacterType bottom;
    public int time;
    public Stance en;
    protected FacingType facingType = FacingType.NONE;
    protected DomType domType = DomType.NONE;

    public Position(CharacterType top, CharacterType bottom, Stance stance) {
        this.top = top;
        this.bottom = bottom;
        en = stance;
        time = 0;
    }

    public Character getTop() {
        return top.fromPoolGuaranteed();
    }

    public Character getBottom() {
        return bottom.fromPoolGuaranteed();
    }

    public Character getDomSexCharacter() {
        return getTop();
    }

    public int pinDifficulty(Combat c, Character self) {
        return 4;
    }

    public int getEscapeMod(Combat c, Character self) {
        int dc = 0;
        if (sub(self) && !mobile(self)) {
            dc -= pinDifficulty(c, self) * Math.max(-5, 10 - time);
        }
        return dc;
    }

    public void decay(Combat c) {
        time++;
    }

    /**
     * Checks for validity of current stance.
     *
     * @param c The ongoing combat.
     * @return The stance to change into. An empty optional indicates no change.
     */
    public Optional<Position> checkOngoing(Combat c) {
        return domType.helper.checkOngoing(c, this);
    }

    public float getSubDomBonus(Character self, float bonus) {
        if (!self.human()) {
            if (self.has(Trait.submissive) && sub(self)) {
                return bonus;
            }
            if ((!self.has(Trait.submissive) || self.has(Trait.flexibleRole)) && dom(self)) {
                return bonus;
            }
        }
        return 0;
    }

    public int distance() {
        return domType.helper.distance(this);
    }

    public abstract String describe(Combat c);

    public abstract boolean mobile(Character c);

    public abstract boolean kiss(Character c, Character target);

    public abstract boolean dom(Character c);

    public abstract boolean sub(Character c);

    public abstract boolean reachTop(Character c);

    public abstract boolean reachBottom(Character c);

    public abstract boolean prone(Character c);

    public boolean feet(Character c, Character target) {
        return domType.helper.feet(c, target, this);
    }

    public boolean oral(Character c, Character target) {
        return domType.helper.oral(c, target, this);
    }

    public abstract boolean behind(Character c);

    public boolean getUp(Character c) {
        return mobile(c) && c == getTop();
    }

    public boolean front(Character c) {
        return !behind(c);
    }

    public boolean inserted(Character c) {
        return domType.helper.inserted(c, this);
    }

    public boolean penisInserted(Character self) {
        if (self == null || self.body.getRandomCock() == null) {
            return false;
        }
        return inserted(self) || self.getInsertedStatus().stream().anyMatch(status -> status.getPitcher().equals(self) && status.getStickPart() != null && status.getStickPart().isType("cock"));
    }

    public abstract String image();

    public boolean inserted() {
        return inserted(getTop()) || inserted(getBottom());
    }

    public Optional<Position> insert(Combat c, Character pitcher, Character dom) {
        return facingType.insert(this, c, pitcher, dom);
    }

    public Optional<Position> insertRandom(Combat c) {
        return insertRandomDom(c, getTop());
    }

    public Collection<Skill> availSkills(Combat c, Character self) {
        return Collections.emptySet();
    }

    public boolean canthrust(Combat c, Character self) {
        return getDomSexCharacter() == self;
    }

    public boolean facing(Character c, Character target) {
        return (!behind(getTop()) && !behind(getBottom())) || (c != getBottom() && c != getTop()) || (target != getBottom() && target != getTop());
    }

    public float priorityMod(Character self) {
        return domType.helper.priorityMod(self, this);
    }

    public boolean fuckable(Character self) {
        Character target;

        if (self == getTop()) {
            target = getBottom();
        } else {
            target = getTop();
        }
        return (self.crotchAvailable() || self.has(Trait.strapped) && target.hasPussy()) && target.crotchAvailable()
                        && mobile(self) && !mobile(target)
                        && ((self.hasDick() || self.has(Trait.strapped)) && !behind(target) || !behind(self))
                        && self.canAct();
    }

    public Stance 
    enumerate() {
        return en;
    }

    @Override
    public Position clone() throws CloneNotSupportedException {
        return (Position) super.clone();
    }

    public Character other(Character character) {
        if (character.getType().equals(top)) {
            return getBottom();
        } else if (character.getType().equals(bottom)) {
            return getTop();
        }
        return null;
    }

    public Position reverse(Combat c, boolean writeMessage) {
        Position newStance;
        try {
            newStance = clone();
        } catch (CloneNotSupportedException e) {
            newStance = this;
        }
        newStance.bottom = top;
        newStance.top = bottom;
        return newStance;
    }

    public boolean anallyPenetrated(Combat combat) {
        return anallyPenetrated(combat, getTop()) || anallyPenetrated(combat, getBottom());
    }

    public boolean anallyPenetrated(Combat combat, Character self) {
        if (self == null || self.body.getRandomAss() == null) {
            return false;
        }
        List<BodyPart> parts = partsForStanceOnly(combat, self, getPartner(combat, self));
        return BodyPart.hasType(parts, "ass") || self.getInsertedStatus().stream().anyMatch(status -> status.getReceiver().equals(self) && status.getHolePart() != null && status.getHolePart().isType("ass"));
    }

    public Optional<Position> insertRandomDom(Combat c, Character target) {
        return facingType.insertRandomDom(this, c, target);
    }

    public Character getPartner(Combat c, Character self) {
        if (self == getTop()) {
            return getBottom();
        } else {
            return getTop();
        }
    }

    public List<Character> getAllPartners(Combat c, Character self) {
        return Collections.singletonList(getPartner(c, self));
    }

    public boolean paizuri(Character self, Character target) {
        return oral(self, target);
    }

    public List<BodyPart> topParts() {
        return this.domType.helper.topParts(this);
    }

    public List<BodyPart> bottomParts() {
        return this.domType.helper.bottomParts(this);
    }

    public BodyPart insertedPartFor(Combat combat, Character c) {
        return partsForStanceOnly(combat, c, combat.getOpponent(c)).stream().filter(part -> part.isType("cock") || part.isType("strapon")).findAny()
                        .orElse(Body.nonePart);
    }

    public BodyPart insertablePartFor(Combat combat, Character self, Character other) {
        BodyPart res = pussyPartFor(combat, self, other);
        if (res.isType("none")) {
            return assPartFor(combat, self, other);
        } else {
            return res;
        }
    }

    private BodyPart pussyPartFor(Combat combat, Character self, Character other) {
        return partsForStanceOnly(combat, self, other).stream().filter(part -> part.isType("pussy")).findAny().orElse(Body.nonePart);
    }

    private BodyPart assPartFor(Combat combat, Character self, Character other) {
        return partsForStanceOnly(combat, self, other).stream().filter(part -> part.isType("ass")).findAny().orElse(Body.nonePart);
    }

    public List<BodyPart> getPartsFor(Combat combat, Character self, Character other) {
        List<BodyPart> parts = new ArrayList<>(partsForStanceOnly(combat, self, other));
        Stream.concat(self.getInsertedStatus().stream(), other.getInsertedStatus().stream())
                        .filter(s -> (s.getPitcher() == self && s.getReceiver() == other) || (s.getReceiver() == self && s.getPitcher() == other))
                        .forEach(s -> {
                            if (s.getPitcher() == self) {
                                parts.add(s.getStickPart());
                            } else {
                                parts.add(s.getHolePart());
                            }
                        });
        return parts;
    }

    public List<BodyPart> partsForStanceOnly(Combat combat, Character self, Character other) {
        if (self.equals(getTop())) {
            return topParts();
        } else if (self.equals(getBottom())) {
            return bottomParts();
        } else {
            return Collections.emptyList();
        }
    }

    public boolean vaginallyPenetrated(Combat c) {
        return vaginallyPenetrated(c, getTop()) || vaginallyPenetrated(c, getBottom());
    }

    public boolean penetrated(Combat combat, Character c) {
        return vaginallyPenetrated(combat, c) || anallyPenetrated(combat, c);
    }
    
    public Character getPenetratedCharacter(Combat c, Character self) {
        return getPartner(c, self);
    }

    public boolean vaginallyPenetrated(Combat combat, Character self) {
        if (self == null || self.body.getRandomPussy() == null) {
            return false;
        }
        List<BodyPart> parts = partsForStanceOnly(combat, self, getPartner(combat, self));
        return (BodyPart.hasType(parts, "pussy") && inserted()) || self.getInsertedStatus().stream().anyMatch(status -> status.getReceiver().equals(self) && status.getHolePart() != null && status.getHolePart().isType("pussy"));
    }

    public boolean havingSexOtherNoStrapped(Combat c, Character self) {
        Character other = getPartner(c, self);
        return (penetratedBy(c, other, self) || penetratedBy(c, self, other)) && !other.has(Trait.strapped);
    }

    public boolean havingSexNoStrapped(Combat c) {
        return (penetratedBy(c, getTop(), getBottom()) && !getBottom().has(Trait.strapped)
                        || penetratedBy(c, getBottom(), getTop())) && !getTop().has(Trait.strapped);
    }

    public boolean havingSex(Combat c) {
        return penetratedBy(c, getDomSexCharacter(), getBottom()) || penetratedBy(c, getBottom(), getDomSexCharacter()) || en == Stance.trib;
    }

    public boolean havingSex(Combat c, Character self) {
        if (getDomSexCharacter() == self || getBottom() == self) {
            return havingSex(c);
        }
        return false;
    }

    public boolean penetratedBy(Combat c, Character inserted, Character inserter) {
        return vaginallyPenetratedBy(c, inserted, inserter) || anallyPenetratedBy(c, inserted, inserter);
    }

    public boolean vaginallyPenetratedBy(Combat c, Character inserted, Character inserter) {
        if (inserter != getPartner(c, inserted)) {
            return false;
        }
        List<BodyPart> parts = partsForStanceOnly(c, inserted, inserter);
        List<BodyPart> otherParts = partsForStanceOnly(c, inserter, inserted);
        return BodyPart.hasType(parts, "pussy") && (BodyPart.hasType(otherParts, "cock") || BodyPart.hasType(otherParts, "strapon"));
    }

    public boolean anallyPenetratedBy(Combat c, Character self, Character other) {
        if (other != getPartner(c, self)) {
            return false;
        }
        List<BodyPart> parts = partsForStanceOnly(c, self, other);
        List<BodyPart> otherParts = partsForStanceOnly(c, other, other);
        return (BodyPart.hasType(parts, "ass")
                        && (BodyPart.hasType(otherParts, "cock") || BodyPart.hasType(otherParts, "strapon"))) && inserted();
    }

    public boolean connected(Combat c) {
        return anallyPenetrated(c) || vaginallyPenetrated(c) || inserted();
    }

    public boolean faceAvailable(Character target) {
        return true;
    }

    /*
     * returns likelihood modification of applying pheromones. 1 is normal, 2 is twice as likely, .5 is half as likely, 0 is never
     */
    public double pheromoneMod(Character self) {
        return domType.helper.pheromoneMod(self, this);
    }
    
    /**
     * @return how dominant the dominant character is. positive for more dominant, negative for less.
     */
    public int dominance() {
        return 0;
    }

    public String name() {
        return getClass().getSimpleName();
    }

    /**
     * Stances have a dominance rating that benefits the dominant character, queried from Position.dominance().
     * 0: Not dominant at all. Seen in the Neutral position.
     * 1: Very give-and-take. Seen in the 69 position.
     * 2: Slightly dominant. Found in the TribadismStance and Mount positions.
     * 3: Average dominance. Missionary, Kneeling, Standing, and other "vanilla" positions all have this rating.
     * 4: High dominance. Anal positions and Pin are examples of positions with this rating.
     * 5: Absurd dominance. Exotic positions like Engulfed and FlyingCarry have this rating, as well as the more mundane FaceSitting and Smothering.
     *
     * @param self The character whose traits are checked to modify the current stance's dominance score.
     * @return The dominance of the current position, modified by one combatant's traits. Higher return values cause more willpower loss on each combat tick.
     * If a character is not the dominant character of the position, their effective dominance is 0.
     */
    public int getDominanceOfStance(Character self) {
        if (sub(self)) {
            return 0;
        }
        int stanceDominance = dominance();
        // It is unexpected, but not catastrophic if a character is at once a natural dom and submissive.
        if (self.has(Trait.naturalTop)) {
            // Rescales stance dominance values from 0-1-2-3-4-5 to 0-2-3-5-6-8
            stanceDominance = Double.valueOf(Math.ceil(stanceDominance * 1.5)).intValue();
        }
        if (self.has(Trait.submissive)) {
            // Rescales stance dominance values from 0-1-2-3-4-5 to 0-0-1-1-2-3
            stanceDominance = Double.valueOf(Math.floor(stanceDominance * 0.6)).intValue();
        }
        return Math.max(0, stanceDominance);
    }

    public boolean isBeingFaceSatBy(Character self, Character target) {
        return isFacesatOn(self) && isFaceSitting(target);
    }

    public boolean isFaceSitting(Character self) {
        return false;
    }
   
    public boolean isFacesatOn(Character self) {
        return false;
    }

    public boolean reversable(Combat c) {
        return reverse(c, false) != this;
    }

    public void struggle(Combat c, Character struggler) {
        domType.helper.struggle(c, struggler, this);
    }

    public void escape(Combat combat, Character escapee) {
        domType.helper.escape(combat, escapee, this);
    }

    public boolean isPartFuckingPartInserted(Combat c, Character inserter, BodyPart stick, Character inserted, BodyPart hole) {
        if (c == null || inserter == null || stick == null || inserted == null || hole == null) {
            return false;
        }
        if (hole.isType("mouth") && stick.isType("cock")) {
            // TODO fix me so this doesn't need to be true all the time.
            return true;
        }
        if (vaginallyPenetratedBy(c, inserted, inserter)) {
            return hole.isType("pussy") && stick.isType("cock");
        }
        if (anallyPenetratedBy(c, inserted, inserter)) {
            return hole.isType("ass") && stick.isType("cock");
        }
        List<InsertedStatus> insertedStatus = Stream.concat(inserter.getInsertedStatus().stream(), inserted.getInsertedStatus().stream()).collect(Collectors.toList());
        return insertedStatus.stream().anyMatch(is -> hole.equals(is.getHolePart()) && inserted.equals(is.getReceiver()) && inserter.equals(is.getPitcher()) && stick.equals(is.getStickPart()));
    }

    Optional<Position> dickMissing(Combat c, Character inserter, Character inserted) {
        if (inserter.hasInsertable()) {
            if (inserter.human()) {
                c.write(String.format("With %s %s gone, you groan in frustration and cease your merciless movements.",
                                inserter.possessiveAdjective(), ProseUtils.getDickWord()));
            } else {
                c.write(String.format("%s groans with frustration with the sudden disappearance of %s %s.",
                                inserted.getName(), inserter.nameOrPossessivePronoun(), ProseUtils.getDickWord()));
            }
            return insertRandom(c);
        }
        return Optional.empty();
    }

    Optional<Position> pussyMissing(Combat c, Character inserter, Character inserted) {
        if (!inserted.hasPussy() && !anallyPenetratedBy(c, inserted, inserter)) {
            if (inserted.human()) {
                c.write(String.format("With your %s suddenly disappearing, %s can't continue fucking you anymore.",
                                ProseUtils.getPussyWord(), inserter.subject()));
            } else {
                c.write(String.format("You groan with frustration with the sudden disappearance of %s %s.",
                                inserted.nameOrPossessivePronoun(), ProseUtils.getPussyWord()));
            }
            return insertRandom(c);
        }
        return Optional.empty();
    }

    Optional<Position> assholeMissing(Combat c, Character inserter, Character inserted) {
        if (inserted.body.getRandom("ass") == null) {
            if (inserted.human()) {
                c.write(String.format("With your %s suddenly disappearing, you can't continue riding %s anymore.",
                                ProseUtils.getAssholeWord(), inserter.getName()));
            } else {
                c.write(String.format("%s groans with frustration with the sudden disappearance of %s %s.",
                                inserted.getName(), inserted.possessiveAdjective(), ProseUtils.getAssholeWord()));
            }
            return insertRandom(c);
        }
        return Optional.empty();
    }
}
