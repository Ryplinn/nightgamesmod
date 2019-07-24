package nightgames.skills;

/**
 * SkillUnusableException is intended for situations where something goes wrong when resolving the use of a skill,
 * after first checking whether it was usable.
 */
public class SkillUnusableException extends RuntimeException {

    private static final long serialVersionUID = 2449705009333893124L;
    private final Skill.SkillUsage usage;

    SkillUnusableException(Skill.SkillUsage usage) {
        this.usage = usage;
    }

    @Override public String getMessage() {
        return "Something went wrong between checking whether " + usage.skill.getName()
                        + " was usable and resolving the skill.";
    }
}
