package fr.fonkio;

public enum PoolStatus {

    JOIN("%s vient de réserver une place"),
    LEAVE("%s vient de libérer une place"),
    CREATE("%s viens de lancer le sondage");

    private String message;

    PoolStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
