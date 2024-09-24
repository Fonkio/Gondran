package fr.fonkio;

import fr.fonkio.listener.EventButtonInteraction;
import fr.fonkio.listener.EventSlashCommandInteraction;
import fr.fonkio.utils.Configuration;
import fr.fonkio.utils.IdEnum;
import fr.fonkiomessage.StringsConst;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.IOException;
import java.util.*;

public class Gondran {

    public static Configuration CONFIGURATION;
    public static Map<Guild, Poll> POLL_DATA = new HashMap<>();
    private static JDA jda;

    static {
        Configuration configuration = null;
        try {
            configuration = new Configuration("data.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        CONFIGURATION = configuration;
    }

    public static void main(String[] args) throws InterruptedException {
        Set<GatewayIntent> intents = new HashSet<>(EnumSet.allOf(GatewayIntent.class));
        jda = JDABuilder.create(CONFIGURATION.getToken(), intents).setAutoReconnect(true).build();
        CONFIGURATION.save();
        jda.addEventListener(new EventButtonInteraction());
        jda.addEventListener(new EventSlashCommandInteraction());
        Activity act = new GondranActivity();
        jda.getPresence().setActivity(act);
        jda.updateCommands().addCommands(
                Commands.slash(IdEnum.COMMAND_POLL.getId(), StringsConst.COMMAND_POLL_DESC)
                        .addOption(OptionType.STRING, "nom-jeu", "Nom du jeu à proposer", true)
                        .addOption(OptionType.INTEGER, "nb-joueurs-max", "Nombre de joueurs max", true)
        ).queue();
        System.out.println("Bot connecté");
        CONFIGURATION.save();
    }


}
