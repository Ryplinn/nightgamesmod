package nightgames.stance;

/**
 * TODO: Write class-level documentation.
 */
public enum DomType {
    NONE(new NeutraldomSexStance()),
    FEMDOM(new FemdomSexStance()),
    MALEDOM(new MaledomSexStance())
    ;

    public DomHelper helper;

    DomType(DomHelper helper) {
        this.helper = helper;
    }

}
