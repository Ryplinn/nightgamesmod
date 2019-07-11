package nightgames.characters;

import nightgames.characters.trait.Trait;
import nightgames.nskills.tags.AttributeSkillTag;
import nightgames.nskills.tags.SkillTag;

public enum Attribute {
    power("weaker", "stronger", "physical strength", "power"),
    seduction("less seductive", "more seductive", "allure", "seductiveness"),
    cunning("less intelligent", "more intelligent", "guile", "cunning"),
    perception("less perceptive", "more perceptive", "keenness", "perception"),
    speed("slower", "faster", "quickness", "speed"),
    spellcasting("more mundane", "more in tune with mystic energies", "mystic energies", "arcane powers"),
    science("dumber", "more technologically inclined", "gadget know-how", "scientific knowledge"),
    darkness("like {self:pronoun-action:are} lacking some of {self:possessive} usual darkness", "more sinful", "sin", "darkness"),
    fetishism("like it's harder to fetishize things", "it's easier to dream about fetishes", "fantasies", "fetishes"),
    animism("tamer", "wilder", "instinct", "animism"),
    ki("like {self:pronoun-action:have} less aura", "more in control of your body", "spirit", "ki"),
    bio("like {self:pronoun-action:have} less control over {self:possessive} biology", "more in control of {self:possessive} biology", "essence", "biological control"),
    divinity("less divine", "more divine", "divinity", ""),
    willpower("like {self:pronoun-action:have} less self-control", "more psyched up", "self-control", "willpower"),
    medicine("like {self:pronoun-action:have} less medical knowledge", "{self:reflective} more in command of medical knowledge", "medical knowledge", ""),
    technique("like {self:pronoun-action:have} less technique", "more sexually-experienced", "sexual flair", "techniques"),
    submission("less in tune with your partner's needs", "more responsive", "submissiveness", "responsiveness"),
    hypnotism("less hypnotic", "like it's easier to bend other's wills", "entrancing demeanour", "hypnotic gaze"),
    nymphomania("like {self:pronoun-action:have} less sex drive", "hornier", "sex drive", "nymphomania"),
    slime("like {self:pronoun-action:have} less control over {self:possessive} slime", "more in control over {self:possessive} slime", "control over {self:possessive} amorphous body", "slime"),
    ninjutsu("less stealthy", "stealthier", "stealth and training", "ninjutsu"),
    temporal("like {self:pronoun-action:are} forgetting some finer details of the procrastinator", "better in tune with the finer details of the procrastinator", "knowledge of the procrastinator", "");

    private final SkillTag skillTag;
    private final String lowerVerb;
    private final String raiseVerb;
    private final String drainedDirectObject;
    private final String drainerDirectObject;
    Attribute(String lowerVerb, String raiseVerb, String drainedDirectObject, String drainerDirectObject) {
        skillTag = new AttributeSkillTag(this);
        this.lowerVerb = lowerVerb;
        this.raiseVerb = raiseVerb;
        this.drainedDirectObject = drainedDirectObject;
        this.drainerDirectObject = drainerDirectObject;
    }

    public SkillTag getSkillTag() {
        return skillTag;
    }

    public static boolean isBasic(Attribute a) {
        return a == power || a == seduction || a == cunning;
    }

    public static boolean isTrainable(Character self, Attribute a) {
        // Speed and Perception cannot be trained.
        // Basic attributes (Power, Seduction, Cunning) can always be trained.
        // Willpower can be trained, up to a level-dependent cap.
        // A few attributes only require a certain trait.
        // Most attributes require instruction before they can be increased at level-up.
        switch (a) {
            case willpower:
                return self.getWillpower().max() + 2 <= self.getMaxWillpowerPossible();
            case speed:
            case perception:
                return false;
            case power:
            case seduction:
            case cunning:
                return true;
            case divinity:
                return self.has(Trait.divinity);
            case nymphomania:
                return self.has(Trait.nymphomania);
            default:
                 return self.getPure(a) > 0;
        }
    }

    public String getLowerPhrase() {
        return lowerVerb;
    }

    public String getRaisePhrase() {
        return raiseVerb;
    }

    public String getDrainedDO() {
        return drainedDirectObject;
    }

    public String getDrainerOwnDO() {
        return drainerDirectObject.isEmpty() ? "own" : "own " + drainerDirectObject;
    }

    public String getDrainerDO() {
        return drainerDirectObject.isEmpty() ? drainedDirectObject : drainerDirectObject;
    }

    public static Attribute fromLegacyName(String legacyName) {
        String lowerCase = legacyName.toLowerCase();
        switch (lowerCase) {
            case "arcane":
                return spellcasting;
            case "fetish":
                return fetishism;
            case "submissive":
                return submission;
            case "hypnosis":
                return hypnotism;
            default:
                return Attribute.valueOf(lowerCase);
        }
    }
}
