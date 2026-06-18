package be.wacken.planner.application;

public record PersonRatingStars(String personName, int rating) {
    private static final String DINO_USER_ID_PREFIX = "21dad490-3b20-4377-b880-44fb4a93221c";
    private static final String SOFIE_USER_ID_PREFIX = "f4dbc343-5c61-476d-8352-024528ff";
    private static final String UUID_PATTERN =
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    public PersonRatingStars {
        if (personName == null || personName.isBlank()) {
            throw new IllegalArgumentException("personName must not be blank");
        }
        personName = personName.trim();
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("rating must be between 1 and 5");
        }
    }

    public String displayText() {
        return displayName() + " " + "★".repeat(rating);
    }

    private String displayName() {
        String normalizedName = personName.trim();
        String lowerName = normalizedName.toLowerCase();
        if (lowerName.startsWith(DINO_USER_ID_PREFIX)) {
            return "D";
        }
        if (lowerName.startsWith(SOFIE_USER_ID_PREFIX)) {
            return "S";
        }
        if (lowerName.matches(UUID_PATTERN)) {
            return "U";
        }
        return normalizedName.substring(0, 1).toUpperCase();
    }
}
