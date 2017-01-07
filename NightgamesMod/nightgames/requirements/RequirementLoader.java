package nightgames.requirements;

/**
 * Interface for loading requirements from, e.g., character JSON files.
 */
public interface RequirementLoader<T> {
    Requirement loadRequirement(String key, T reqData);

    AffectionRequirement loadAffection(T reqData);

    AnalRequirement loadAnal(T reqData);

    AndRequirement loadAnd(T reqData);

    AttractionRequirement loadAttraction(T reqData);

    AttributeRequirement loadAttribute(T reqData);

    BodyPartRequirement loadBodyPart(T reqData);

    CashRequirement loadCash(T reqData);

    DomRequirement loadDom(T reqData);

    DurationRequirement loadDuration(T reqData);

    InsertedRequirement loadInserted(T reqData);

    ItemRequirement loadItem(T reqData);

    LevelRequirement loadLevel(T reqData);

    MoodRequirement loadMood(T reqData);

    NoneRequirement loadNone(T reqData);

    NotRequirement loadNot(T reqData);

    OrgasmRequirement loadOrgasm(T reqData);

    OrRequirement loadOr(T reqData);

    ProneRequirement loadProne(T reqData);

    RandomRequirement loadRandom(T reqData);

    ResultRequirement loadResult(T reqData);

    ReverseRequirement loadReverse(T reqData);

    SpecificBodyPartRequirement loadSpecificBodyPart(T reqData);

    PositionRequirement loadPosition(T reqData);

    StatusRequirement loadStatus(T reqData);

    SubRequirement loadSub(T reqData);

    TraitRequirement loadTrait(T reqData);

    WinningRequirement loadWinning(T reqData);
}
