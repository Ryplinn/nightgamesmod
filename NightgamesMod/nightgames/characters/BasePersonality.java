package nightgames.characters;

import nightgames.actions.Action;
import nightgames.actions.Movement;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockMod;
import nightgames.characters.body.CockPart;
import nightgames.characters.custom.AiModifiers;
import nightgames.characters.custom.CharacterLine;
import nightgames.characters.custom.CommentSituation;
import nightgames.characters.custom.RecruitmentData;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Random;
import nightgames.global.*;
import nightgames.items.Item;
import nightgames.skills.Skill;
import nightgames.status.addiction.Addiction;
import nightgames.utilities.MathUtils;

import java.util.*;

public abstract class BasePersonality implements Personality {
    /**
     *
     */
    private static final long serialVersionUID = 2279220186754458082L;
    final boolean isStartCharacter;
    protected List<PreferredAttribute> preferredAttributes;
    protected CockMod preferredCockMod;
    protected AiModifiers mods;

    protected BasePersonality(boolean isStartCharacter) {
        this.isStartCharacter = isStartCharacter;
        preferredCockMod = CockMod.error;
        preferredAttributes = new ArrayList<>();
    }

    abstract public void setGrowth(NPC selfNPC);

    @Override
    public void rest(int time, NPC selfNPC) {
        if (!preferredCockMod.equals(CockMod.error) && selfNPC.rank > 0) {
            Optional<BodyPart> optDick = selfNPC.body.get("cock")
                                                       .stream()
                                                       .filter(part -> part.moddedPartCountsAs(selfNPC, preferredCockMod))
                                                       .findAny();
            if (optDick.isPresent()) {
                CockPart part = (CockPart) optDick.get();
                selfNPC.body.remove(part);
                selfNPC.body.add(part.applyMod(preferredCockMod));
            }
        }
        for (Addiction addiction : selfNPC.getAddictions()) {
            if (addiction.atLeast(Addiction.Severity.LOW)) {
                Character cause = addiction.getCause();
                int affection = selfNPC.getAffection(cause);
                int affectionDelta = affection - selfNPC.getAffection(GameState.getGameState().characterPool.getPlayer());
                // day 10, this would be (10 + sqrt(10) * 5) * .7 = 18 affection lead to max
                // day 60, this would be (10 + sqrt(70) * 5) * .7 = 36 affection lead to max
                double chanceToDoDaytime = .25 + (addiction.getMagnitude() / 2) + MathUtils
                                .clamp((affectionDelta / (10 + Math.sqrt(Time.getDate()) * 5)), -.7, .7);
                if (Random.randomdouble() < chanceToDoDaytime) {
                    addiction.aggravate(null, Addiction.MED_INCREASE);
                    addiction.flagDaytime();
                    selfNPC.gainAffection(cause, 1);
                    DebugFlags.DEBUG_ADDICTION
                                    .printf("%s did daytime for %s (%s), chance = %f\n", selfNPC.getTrueName(),
                                                    addiction.getType().name(), cause.getTrueName(), chanceToDoDaytime);
                }
            }
        }
    }

    protected void buyUpTo(Item item, int number, NPC selfNPC) {
        while (selfNPC.money > item.getPrice() && selfNPC.count(item) < number) {
            selfNPC.money -= item.getPrice();
            selfNPC.gain(item);
        }
    }

    @Override
    public Skill chooseSkill(HashSet<Skill> available, Combat c, NPC selfNPC) {
        HashSet<Skill> tactic;
        Skill chosen;
        ArrayList<WeightedSkill> priority = Decider.parseSkills(available, c, selfNPC);
        if (!Flag.checkFlag(Flag.dumbmode)) {
            chosen = Decider.prioritizeNew(selfNPC, priority, c);
        } else {
            chosen = selfNPC.prioritize(priority);
        }
        if (chosen == null) {
            tactic = available;
            Skill[] actions = tactic.toArray(new Skill[0]);
            return actions[Random.random(actions.length)];
        } else {
            return chosen;
        }
    }

    @Override
    public Action move(Collection<Action> available, Collection<Movement> radar, NPC selfNPC) {
        return Decider.parseMoves(available, radar, selfNPC);
    }

    @Override
    public void pickFeat(NPC selfNPC) {
        ArrayList<Trait> available = new ArrayList<>();
        for (Trait feat : Trait.getFeats(selfNPC)) {
            if (!selfNPC.has(feat)) {
                available.add(feat);
            }
        }
        if (available.size() == 0) {
            return;
        }
        selfNPC.add((Trait) available.toArray()[Random.random(available.size())]);
    }

    @Override
    public String image(Combat c, NPC selfNPC) {
        return selfNPC.getType().toString().toLowerCase() + "_" + selfNPC.mood.name() + ".jpg";
    }

    public String defaultImage(Combat c, NPC selfNPC) {
        return selfNPC.getTrueName()
                        .toLowerCase() + "_confident.jpg";
    }

    @Override
    public void ding(Character self) {
        self.getGrowth().levelUp(self);
        onLevelUp(self);
        self.distributePoints(preferredAttributes);
    }

    @Override
    public List<PreferredAttribute> getPreferredAttributes() {
        return preferredAttributes;
    }

    protected void onLevelUp(Character self) {
        // NOP
    }

    @Override
    public String describeAll(Combat c, Character self) {
        StringBuilder b = new StringBuilder();
        b.append(self.getRandomLineFor(CharacterLine.DESCRIBE_LINER, c, c.getOpponent(self)));
        b.append("<br/><br/>");
        self.body.describe(b, c.getOpponent(self), " ");
        b.append("<br/>");
        for (Trait t : self.getTraits()) {
            t.describe(self, b);
            b.append(' ');
        }
        b.append("<br/>");
        return b.toString();
    }

    @Override
    public RecruitmentData getRecruitmentData(NPC selfNPC) {
        return null;
    }

    @Override
    public AiModifiers getAiModifiers(NPC selfNPC) {
        if (mods == null)
            resetAiModifiers(selfNPC);
        return mods;
    }
    
    @Override
    public void setAiModifiers(AiModifiers mods, NPC selfNPC) {
        this.mods = mods;
    }
    
    @Override
    public void resetAiModifiers(NPC selfNPC) {
        mods = AiModifiers.getDefaultModifiers(selfNPC.getType());
    }
    
    @Override
    public String resist3p(Combat c, Character target, Character assist) {
        return null;
    }

    @Override
    public Map<CommentSituation, String> getComments(Combat c, NPC selfNPC) {
        Map<CommentSituation, String> all = CommentSituation.getDefaultComments(selfNPC.getType());
        Map<CommentSituation, String> applicable = new HashMap<>();
        all.entrySet()
           .stream()
           .filter(e -> e.getKey()
                         .isApplicable(c, selfNPC, c.getOpponent(selfNPC)))
           .forEach(e -> applicable.put(e.getKey(), e.getValue()));
        return applicable;
    }
}
