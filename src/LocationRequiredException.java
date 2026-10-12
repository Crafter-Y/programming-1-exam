public class LocationRequiredException extends RuntimeException {
    public LocationRequiredException() {
        super("At least one location is required!");
    }
}
