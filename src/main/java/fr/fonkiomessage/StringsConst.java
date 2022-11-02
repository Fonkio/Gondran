package fr.fonkiomessage;

import net.dv8tion.jda.api.entities.emoji.Emoji;

public class StringsConst {
    public static final String COMMAND_POLL_DESC = "Lancer un sondage";
    public static final String NO_MORE_SIT = "Il n'y a plus de place";
    public static final String NOT_REGISTERED = "Vous n'êtes pas inscrit";
    public static final String ALREADY_NO_SIT_LEFT = "Vous n'avez plus de place réservée";
    public static final String JOIN_POLL = "%s vient de réserver une place";
    public static final String LEAVE_POLL = "%s vient de libérer une place";
    public static final String CREATE_POLL = "%s viens de lancer le sondage";
    public static final String WHO_AMONGUS = "Qui veut Among Us ?";
    public static final String BOT_ACTIVITY = "/poll \uD83C\uDF0C                               \nCréé par Fonkio";
    public static final String PLAYER_LIST = "Liste des joueurs présents";
    public static final String POLL_INSTRUCTION = "Pour t'ajouter ou te retirer, cliques sur les boutons\n" +
            "Tu peux cliquer plusieurs fois pour réserver des places à plusieurs personnes";
    public static final String REGISTER_BUTTON = " | Réserver";
    public static final String UNREGISTER_BUTTON = " | Se désinscrire";
    public static final String ERROR = "Erreur";
    public static final String OLD_POLL = "Vous venez de répondre à un sondage qui n'est plus actif !";

    public static final Emoji PLUS_EMOJI = Emoji.fromUnicode("➕");

    public static final Emoji MINUS_EMOJI = Emoji.fromUnicode("➖");
}
