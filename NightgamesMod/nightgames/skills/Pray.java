package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.GameState;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.AddictionType;

public class Pray extends Skill {

    public Pray() {
        super("Pray", 2);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.human();
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.getAnyAddiction(AddictionType.ZEAL).map(addiction -> addiction.wasCausedBy(target))
                        .orElse(false) && user.canRespond();
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Pray to your goddess for guidance";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        c.write(user,
                        Formatter.format("{self:SUBJECT-ACTION:bow} {self:possessive} head and close {self:possessive} eyes,"
                                        + " whispering a quick prayer to Angel for guidance. {other:SUBJECT-ACTION:look} at {self:direct-object} strangely, but "
                                        + " the knowledge that Angel is there for {self:direct-object} reinvigorates {self:possessive} spirit"
                                        + " and strengthens {self:possessive} faith.", user, target));
        int amt = Math.round((user.getAnyAddiction(AddictionType.ZEAL)
                        .orElseThrow(() -> new SkillUnusableException(new SkillUsage<>(this, user, target))).getMagnitude() * 8));
        user.restoreWillpower(c, amt);
        user.addict(c, AddictionType.ZEAL, GameState.getGameState().characterPool.getCharacterByType("Angel"), Addiction.LOW_INCREASE);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Pray();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.recovery;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        throw new UnsupportedOperationException();
    }

}
