package fr.fonkio.utils;

public enum IdEnum {
    COMMAND_POLL("poll"),
    BUTTON_REGISTER("register"),
    BUTTON_UNREGISTER("unregister");
    private final String id;

    IdEnum(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static IdEnum parseString(String id) {
        for (IdEnum idEnum : IdEnum.values()) {
            if (idEnum.getId().equals(id)) {
                return idEnum;
            }
        }
        return null;
    }
}
