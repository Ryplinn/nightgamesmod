package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.CounterStatus;
import nightgames.status.Stsflag;

public abstract class CounterBase extends Skill {
    protected String description;   // format string
    private int duration;

    CounterBase(String name, int cooldown, String description) {
        this(name, cooldown, description, 0);
    }

    CounterBase(String name, int cooldown, String description, int duration) {
        super(name, cooldown);
        addTag(SkillTag.counter);
        this.description = description;
        this.duration = duration;
    }

    public String getBlockedString(Combat c, Character user, Character target) {
        return Formatter.format(
                        "{self:SUBJECT-ACTION:block|blocks} {other:name-possessive} attack and {self:action:move|moves} in for a counter. "
                                        + "However, {other:subject-action:were|was} wary of {self:direct-object} and {other:action:jump|jumps} back before {self:subject} can catch {other:direct-object}.",
                        user, target);
    }

    @Override
    public final boolean resolve(Combat c, Character user, Character target) {
        if (user.human()) {
            c.write(user, deal(c, 0, Result.setup, user, target));
        } else if (!target.is(Stsflag.blinded)) {
            c.write(user, receive(c, 0, Result.setup, user, target));
        } else {
            printBlinded(c, user);
        }
        user.add(c, new CounterStatus(user.getType(), this, description, duration));
        return true;
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 0;
    }

    public abstract void resolveCounter(Combat c, Character user, Character target);

    @Override
    public int speed(Character user) {
        return 20;
    }
}
