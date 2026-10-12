public class TideValue {
    private final String locationId;
    private final long time;
    private final TideType type;
    private final float value;

    public TideValue(String locationId, long time, TideType type, float value) {
        this.locationId = locationId;
        this.time = time;
        this.type = type;
        this.value = value;
    }

    public String getLocationId() {
        return locationId;
    }

    public long getTime() {
        return time;
    }

    public TideType getType() {
        return type;
    }

    public float getValue() {
        return value;
    }
}
