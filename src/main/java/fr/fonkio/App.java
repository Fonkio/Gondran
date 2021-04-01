package fr.fonkio;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Hello world!
 *
 */
public class App {

    public static JDA dorald;

    public static void main(String[] args) throws IOException, URISyntaxException, LoginException, InterruptedException {
        Set<GatewayIntent> intents = new HashSet<>();
        intents.addAll(EnumSet.allOf(GatewayIntent.class));
        JDA gondran = JDABuilder.create(System.getenv("TOKEN"),intents).setAutoReconnect(true).build();
        gondran.awaitReady();


        gondran.addEventListener(new GondranListener(new BotData()));
        Activity act = new Activity() {
            @Override
            public boolean isRich() {
                return true;
            }
            @Override
            public String getUrl() {
                return null;
            }
            @Override
            public ActivityType getType() {
                return ActivityType.DEFAULT;
            }
            @Override
            public Timestamps getTimestamps() {
                return new Timestamps(System.currentTimeMillis(), System.currentTimeMillis()+10000000L);
            }
            @Override
            public String getName() {
                return "AmongUsBot \uD83C\uDF0C                                                                                  \nCréé par Fonkio";
            }
            @Override
            public Emoji getEmoji() {
                return new Activity.Emoji("milky_way");
            }
            @Override
            public RichPresence asRichPresence() {
                return null;
            }
        };
        gondran.getPresence().setActivity(act);
        Timer timer;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    dorald = JDABuilder.create(System.getenv("TOKENDORALD"),intents).setAutoReconnect(true).build();
                    dorald.getPresence().setActivity(act);
                } catch (LoginException e) {
                    e.printStackTrace();
                }
            }
        }, 30000);

    }


}
