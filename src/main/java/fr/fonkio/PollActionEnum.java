package fr.fonkio;

import fr.fonkiomessage.StringsConst;

public enum PollActionEnum {

    JOIN(StringsConst.JOIN_POLL),
    LEAVE(StringsConst.LEAVE_POLL),
    CREATE(StringsConst.CREATE_POLL);

    private String message;

    PollActionEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
