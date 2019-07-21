package nightgames.skills;

import java.util.List;

import nightgames.characters.custom.CustomStringEntry;
import nightgames.requirements.Requirement;

public class LoadedSkillData {
    String name;
    float priorityMod;
    List<Requirement> usableRequirements;
    List<Requirement> skillRequirements;
    List<CustomStringEntry> labels;
    public String description;
    public Tactics tactics;
    int mojoCost;
    int mojoBuilt;
    public int cooldown;
    public boolean makesContact;
    public int accuracy;
    public int speed;
}
