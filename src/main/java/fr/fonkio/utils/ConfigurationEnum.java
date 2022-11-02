package fr.fonkio.utils;

public enum ConfigurationEnum {
    TAG_ROLE("tagrole", ""),
    TOKEN("token", "Mettre le token ici");



    ConfigurationEnum(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    private final String key;
    private final String defaultValue;

    public String getKey() {
        return key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
