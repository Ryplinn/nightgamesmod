package nightgames.global;

import nightgames.daytime.Daytime;

/**
 * Indicates whether it is daytime (shopping and talking) or nighttime (sex-fighting).
 */
public enum Time {
    DAY("day"), NIGHT("night");

    public static Time time;
    protected static int date;
    final String desc;

    Time(String desc) {
        this.desc = desc;
    }

    static Time fromDesc(String desc) {
        desc = compatibility(desc);
        return Time.valueOf(desc.toUpperCase());
    }

    /**
     * Maps old descriptors to current descriptors to maintain save file compatibility.
     * <br/><br/>
     * Older save files used different descriptors for DAY and NIGHT (and different enum constants).
     *
     * @param oldDesc A description that may or may not be from an old version.
     * @return A current description suitable for use in fromDesc().
     */
    private static String compatibility(String oldDesc) {
        switch (oldDesc) {
            case "dawn":
                return DAY.desc;
            case "dusk":
                return NIGHT.desc;
            default:
                return oldDesc;
        }
    }

    public static Time getTime() {
        return time;
    }

    public String timeText() {
        switch (this) {
            case NIGHT:
                // We may be in between setting NIGHT and building the Match object
                // yup... silverbard pls :D
                if (Match.getMatch() == null) {
                    return "9:50 pm";
                } else if (Match.getMatch().getHour() >= 12) {
                    return Match.getMatch().getTime() + " am";
                } else {
                    return Match.getMatch().getTime() + " pm";
                }
            case DAY:
                if (Daytime.getDay() != null) {
                    return Daytime.getDay().getTime();
                } else {
                    return "10:00 am";
                }
            default:
                System.err.println("Unknown time of day: " + Time.getTime());
                return "#TIME_ERROR#";
        }
    }

    public static int getDate() {
        return date;
    }

    public static boolean isWeekend() {
        int dayOfWeek = date % 7;
        return dayOfWeek == 6 || dayOfWeek == 0;
    }
}
