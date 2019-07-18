package nightgames.start;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nightgames.characters.body.*;
import nightgames.characters.body.mods.*;
import nightgames.json.JsonUtils;

import java.util.ArrayList;
import java.util.List;

class BodyConfiguration {

    protected Archetype type;
    protected GenitalConfiguration genitals;
    protected BreastsPart breasts;
    protected AssPart ass;
    protected EarPart ears;
    protected TailPart tail;
    protected WingsPart wings;
    protected List<TentaclePart> tentacles;
    protected Double hotness;
    private Double faceFemininity;

    private BodyConfiguration() {
    }
    
    private BodyConfiguration(BodyConfiguration primaryConfig, BodyConfiguration secondaryConfig) {
        type = primaryConfig.type;
        genitals = ConfigurationUtils.merge(primaryConfig.genitals, secondaryConfig.genitals);
        breasts = ConfigurationUtils.merge(primaryConfig.breasts, secondaryConfig.breasts);
        ass = ConfigurationUtils.merge(primaryConfig.ass, secondaryConfig.ass);
        faceFemininity = ConfigurationUtils.merge(primaryConfig.faceFemininity, secondaryConfig.faceFemininity);
        ears = ConfigurationUtils.merge(primaryConfig.ears, secondaryConfig.ears);
        tail = ConfigurationUtils.merge(primaryConfig.tail, secondaryConfig.tail);
        wings = ConfigurationUtils.merge(primaryConfig.wings, secondaryConfig.wings);
        tentacles = ConfigurationUtils.merge(primaryConfig.tentacles, secondaryConfig.tentacles);
        hotness = ConfigurationUtils.merge(primaryConfig.hotness, secondaryConfig.hotness);
    }

    static BodyConfiguration merge(BodyConfiguration primary,
                    BodyConfiguration secondary) {
        if (primary != null) {
            if (secondary != null) {
                return new BodyConfiguration(primary, secondary);
            }
            return primary;
        }
        return null;
    }

    static BodyConfiguration parse(JsonObject obj) {
        BodyConfiguration config = new BodyConfiguration();
        if (obj.has("archetype"))
            config.type = Archetype.valueOf(obj.get("archetype").getAsString().toUpperCase());
        if (obj.has("breasts"))
            config.breasts = (BreastsPart)new BreastsPart().applyMod(new SizeMod(obj.get("breasts").getAsInt()));
        if (obj.has("ass"))
            config.ass = obj.get("ass").getAsString().equals("basic") ?
                            AssPart.generateGeneric() :
                            (AssPart) AssPart.generateGeneric().applyMod(new SecondPussyMod());
        if (obj.has("ears"))
            config.ears = EarPart.valueOf(obj.get("ears").getAsString().toLowerCase());
        if (obj.has("tail") && !obj.get("tail").getAsString().equals("none"))
            config.tail = TailPart.valueOf(obj.get("tail").getAsString().toLowerCase());
        if (obj.has("wings") && !obj.get("wings").getAsString().equals("none"))
            config.wings = WingsPart.valueOf(obj.get("wings").getAsString().toLowerCase());

        if (obj.has("genitals"))
            config.genitals = GenitalConfiguration.parse(obj.getAsJsonObject("genitals"));
        
        if (obj.has("tentacles")) {
            List<TentaclePart> list = new ArrayList<>();
            JsonArray arr = obj.getAsJsonArray("tentacles");
            for (Object o : arr) {
                list.add(parseTentacle((JsonObject) o));
            }
            config.tentacles = list;
        }

        if (obj.has("hotness")) {
            config.hotness = (double) obj.get("hotness").getAsFloat();
        }

        if (obj.has("faceFemininity")) {
            config.faceFemininity = (double) obj.get("faceFemininity").getAsFloat();
        }
        return config;
    }

    private static TentaclePart parseTentacle(JsonObject o) {
        String desc = o.get("desc").getAsString();
        String fluids = o.get("fluids").getAsString();
        String attachpoint = o.get("attachpoint").getAsString();
        double hotness = o.get("hotness").getAsFloat();
        double pleasure = o.get("pleasure").getAsFloat();
        double sensitivity = o.get("sensitivity").getAsFloat();
        return new TentaclePart(desc, attachpoint, fluids, hotness, pleasure, sensitivity);
    }

    void apply(Body body) {
        if (type != null) {
            type.apply(body);
        }
        if (genitals != null) {
            genitals.apply(body);
        }
        replaceIfPresent(body, breasts);
        replaceIfPresent(body, ass);
        replaceIfPresent(body, ears);
        replaceIfPresent(body, tail);
        replaceIfPresent(body, wings);
        replaceIfPresent(body, new FacePart(body.getFace().hotness, faceFemininity));
        applyTentacles(body);
        if (hotness != null) {
            body.hotness = hotness;
        }
    }
    
    private void replaceIfPresent(Body body, BodyPart part) {
        if (part != null) {
            body.addReplace(part, 1);
        }
    }

    private void applyTentacles(Body body) {
        if (tentacles != null) {
            body.removeAll("tentacles");
            tentacles.forEach(body::add);

        }
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ass == null) ? 0 : ass.hashCode());
        result = prime * result + ((breasts == null) ? 0 : breasts.hashCode());
        result = prime * result + ((ears == null) ? 0 : ears.hashCode());
        result = prime * result + ((genitals == null) ? 0 : genitals.hashCode());
        result = prime * result + ((hotness == null) ? 0 : hotness.hashCode());
        result = prime * result + ((tail == null) ? 0 : tail.hashCode());
        result = prime * result + ((tentacles == null) ? 0 : tentacles.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((wings == null) ? 0 : wings.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BodyConfiguration other = (BodyConfiguration) obj;
        if (ass == null) {
            if (other.ass != null)
                return false;
        } else if (!ass.equals(other.ass))
            return false;
        if (breasts == null) {
            if (other.breasts != null)
                return false;
        } else if (!breasts.equals(other.breasts))
            return false;
        if (ears == null) {
            if (other.ears != null)
                return false;
        } else if (!ears.equals(other.ears))
            return false;
        if (genitals == null) {
            if (other.genitals != null)
                return false;
        } else if (!genitals.equals(other.genitals))
            return false;
        if (hotness == null) {
            if (other.hotness != null) {
                return false;
            }
        } else if (!hotness.equals(other.hotness))
            return false;
        if (tail == null) {
            if (other.tail != null)
                return false;
        } else if (!tail.equals(other.tail))
            return false;
        if (tentacles == null) {
            if (other.tentacles != null)
                return false;
        } else if (!tentacles.equals(other.tentacles))
            return false;
        if (type != other.type)
            return false;
        if (wings == null) {
            return other.wings == null;
        } else
            return wings.equals(other.wings);
    }



    static class GenitalConfiguration {
        CockConfiguration cock;
        PussyPart pussy;

        GenitalConfiguration() {
        }

        public static GenitalConfiguration parse(JsonObject object) {
            GenitalConfiguration config = new GenitalConfiguration();
            if (object.has("cock")) {
                CockConfiguration cock = new CockConfiguration();
                JsonObject cockJson = object.getAsJsonObject("cock");
                JsonUtils.getOptional(cockJson, "length").map(JsonElement::getAsInt)
                                .ifPresent(length -> cock.length = length);
                if (cockJson.has("type")) {
                    cock.type = CockMod.getFromType(cockJson.get("type").getAsString()).orElse(null);
                }
                config.cock = cock;
            }

            JsonUtils.getOptional(object, "pussy").ifPresent(modClass -> {
                if (modClass.isJsonPrimitive() && modClass.getAsString().equals("normal")) {
                    config.pussy = PussyPart.generic;
                } else {
                    PartMod pussyMod = JsonUtils.getGson().fromJson(modClass, PartMod.class);
                    config.pussy = (PussyPart)PussyPart.generic.applyMod(pussyMod);
                }
            });
            return config;
        }

        private void apply(Body body) {
            body.removeAll("cock");
            body.removeAll("pussy");
            if (cock != null) {
                body.add(cock.build());
            }
            if (pussy != null) {
                body.add(pussy);
            }
        }
    }

    static class CockConfiguration {
        CockMod type;
        int length;

        CockConfiguration() {
            length = 6;
        }

        private CockPart build() {
            CockPart generic = (CockPart) new CockPart().applyMod(new SizeMod(length));
            return type != null ? (CockPart) generic.applyMod(type) : generic;
        }
    }

    enum Archetype {
        REGULAR(null, PussyPart.generic),
        DEMON(CockMod.incubus, PussyPart.generic.applyMod(DemonicMod.INSTANCE)),
        CAT(CockMod.primal, PussyPart.generic.applyMod(FeralMod.INSTANCE)),
        CYBORG(CockMod.bionic, PussyPart.generic.applyMod(CyberneticMod.INSTANCE)),
        ANGEL(CockMod.blessed, PussyPart.generic.applyMod(DivineMod.INSTANCE)),
        WITCH(CockMod.runic, PussyPart.generic.applyMod(ArcaneMod.INSTANCE)),
        SLIME(CockMod.slimy, PussyPart.generic.applyMod(GooeyMod.INSTANCE));
        private final CockMod cockMod;
        private final BodyPart pussy;

        Archetype(CockMod cockMod, BodyPart pussy) {
            this.cockMod = cockMod;
            this.pussy = pussy;
        }

        private void apply(Body body) {
            if (body.has("cock") && this != REGULAR) {
                body.addReplace(body.getRandomCock().applyMod(cockMod), 1);
            }
            if (body.has("pussy") && this != REGULAR)
                body.addReplace(pussy, 1);
            switch (this) {
                case ANGEL:
                    body.add(WingsPart.angelic);
                    break;
                case CAT:
                    body.add(TailPart.cat);
                    body.add(EarPart.cat);
                    break;
                case DEMON:
                    body.add(WingsPart.demonic);
                    body.add(TailPart.demonic);
                    body.add(EarPart.pointed);
                    break;
                default:
                    break;
            }
        }
    }
}
