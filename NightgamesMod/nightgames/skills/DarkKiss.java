package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.AddictionSymptom;
import nightgames.status.addiction.AddictionType;

public class DarkKiss extends Skill {

    DarkKiss(Character self) {
        super("Dark Kiss", self, 3);
        addTag(SkillTag.dark);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.human();
    }
    
    @Override
    public int getMojoBuilt(Combat c) {
        return 15;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().checkAnyAddiction(AddictionType.CORRUPTION)
                        && c.getStance().kiss(getSelf(), target)
                        && getSelf().canAct();
    }

    @Override
    public String describe(Combat c) {
        return "Feed some of your opponent's will to the demonic corruption taking hold of you.";
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        assert getSelf().human();
        c.write(getSelf(), String.format("You lean in and plant an intense kiss on %s lips. The corruption which Reyka"
                        + " has imbued you with stirs, and greedily draws %s willpower in through your connection, growing"
                        + " more powerful.", target.nameOrPossessivePronoun(), target.possessiveAdjective()));

        Addiction add = getSelf().getAnyAddiction(AddictionType.CORRUPTION).orElseThrow(() -> new SkillUnusableException(this));
        // TODO: Review Corruption's use of combat magnitude. I'm not sure its effect are consistent.
        float mag = add.activeTracker().map(AddictionSymptom::getCombatMagnitude).orElse(0.f);
        int min = (int) (mag * 3);
        int mod = (int) (mag * 8);
        int amt = min + Random.random(mod);
        target.loseWillpower(c, amt, false);
        add.activeTracker().ifPresent(symptom -> symptom.alleviateCombat(c, Addiction.HIGH_INCREASE));
        getSelf().addict(c, AddictionType.CORRUPTION, add.getCause(), Addiction.LOW_INCREASE);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new DarkKiss(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.pleasure;
    }
    
    @Override
    public boolean makesContact(){
        return true;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        return null;
    }

}
