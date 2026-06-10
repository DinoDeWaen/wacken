package be.wacken.planner.application;

public record PersonRatingStars(String personName, int rating) {
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
        return personName + " " + "★".repeat(rating);
    }
}
