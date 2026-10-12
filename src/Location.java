public class Location {
    private final String id;
    private final String name;
    private final double lat;
    private final double lon;

    public Location(String id, String name, double lat, double lon) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", name, id);
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
