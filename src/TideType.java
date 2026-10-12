public enum TideType {
    HIGH("Hochwasser", "Schwimmen", 3_600, 10_800),
    LOW("Niedrigwasser", "Wattwanderung", -9_000, 3_600);

    private final String label;
    private final String activity;
    private final int from;
    private final int to;

    TideType(String label, String activity, int from, int to) {
        this.label = label;
        this.activity = activity;
        this.from = from;
        this.to = to;
    }

    public String getLabel() {
        return label;
    }

    public String getActivity() {
        return activity;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }
}
