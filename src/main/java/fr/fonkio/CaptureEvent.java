package fr.fonkio;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import org.json.JSONException;
import org.json.JSONObject;


import java.awt.*;
import java.util.*;


public class CaptureEvent {

    private BotData data;

    public CaptureEvent(BotData data) {
        this.data = data;
    }

    public void code(JSONObject jsonObject) throws JSONException {
        String code = jsonObject.getString("LobbyCode");
        if (data.getLastGame() != null && data.getLastGame().equals(code)) {
            return;
        }
        data.setLastGame(code);
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Among Us");
        eb.setTitle("Game lancée ! CODE = ["+code+"]");
        MessageEmbed.Field field = new MessageEmbed.Field("Code", code, true);
        eb.addField(field);
        String serveur = "";
        switch (jsonObject.getInt("Region")) {
            case 0:
                serveur = "North America";
                break;
            case 1:
                serveur = "Asia";
                break;
            case 2:
                serveur = "Europe";
                break;
        }
        String map = "";
        String image = "";
        switch (jsonObject.getInt("Map")) {
            case 0:
                map = "The Skeld";
                image = "https://media.discordapp.net/attachments/770738148774379570/770739056916430868/among-us-carte-skeld.png?width=1170&height=658";
                break;
            case 1:
                map = "Mira HQ";
                image = "https://preview.redd.it/h125n0dqzqp51.png?width=1110&format=png&auto=webp&s=f3d44220d9f9417c1c032af365b1acfc9ee8a93f";
                break;
            case 2:
                map = "Polus";
                image = "https://filmdaily.co/wp-content/uploads/2020/10/Polus-lede-1300x899.jpg";
                break;
            case 3:
                map = "AirShip";
                image = "https://www.atrium-sud.fr/documents/382981/118555141/logo+a+venir+bientot.png/fdcf83cd-bc74-4325-98c2-8183b210c486";
                break;
        }
        field = new MessageEmbed.Field("", "", true);
        eb.addField(field);
        field = new MessageEmbed.Field("Serveur", serveur, true);
        eb.addField(field);
        field = new MessageEmbed.Field("Map", map, true);
        eb.addField(field);
        field = new MessageEmbed.Field("", "", true);
        eb.addField(field);
        field = new MessageEmbed.Field("Créateur", data.getAuthor().getEffectiveName(), true);
        eb.addField(field);
        eb.setColor(Color.GREEN);
        eb.setImage(image);
        data.getTextChannel().sendMessage(eb.build()).queue();
        data.setMessageJoueurs(data.getTextChannel().sendMessage("Attente connexion joueurs...\n").complete());
        for(Joueur j : data.getJoueurs().values()) {
            int indice = j.getNumero();
            if (indice != -1) {
                data.getMessageJoueurs().addReaction(data.getNumber(indice)).queue();
            }
        }
        data.majMessage();
    }

    private boolean reunion = false;

    public void state(JSONObject jsonObject) throws JSONException {
        switch (jsonObject.getInt("NewState")) {
            case 3: //Menu
                System.out.println("Menu");
                for (Joueur joueur : data.getJoueurs().values()) {
                    joueur.setDisconnected(true);
                }
                data.umudAll();
                data.majMessage();
                break;
            case 0: //Lobby
                System.out.println("Lobby");
                for (Joueur j : data.getJoueurs().values()) {
                    j.setDead(false);
                }
                break;
            case 1: //Game
                System.out.println("Game");
                Timer timer;
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        data.mdAlive();
                        data.umDead();
                    }
                }, 7000);
                Timer timerReu;
                timerReu = new Timer();
                timerReu.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        reunion = false;
                    }
                }, 12000);
                break;
            case 2: //Reunion
                System.out.println("Réunion");
                reunion = true;
                data.mDead();
                data.umAliveUdAll();
                break;
            case 4: //Fin
                System.out.println("Fin");
                data.umudAll();
                break;
        }
    }

    public void playerAction(JSONObject eventData) throws JSONException {
        String name = eventData.getString("Name");
        switch (eventData.getInt("Action")) {
            case 0: //Join
                System.out.println(name + " a join");
                data.ajouterJoueur(name);
                data.majMessage();
                break;
            case 1: //Left
                System.out.println(name + " a quitté");
                if (data.getJoueurs().containsKey(name)) {
                    data.getJoueurs().get(name).setDisconnected(true);
                    data.majMessage();
                }
                break;
            case 2: //Death
                System.out.println(name + " est mort");
                data.getJoueurs().get(name).setDead(true);
                if (reunion) {
                    System.out.println("Suite à la réunion");
                    Member m = data.getJoueurs().get(name).getMember();
                    if (m != null && (data.getVoiceChannel().getMembers().contains(m))) {
                        m.deafen(false).queue();
                        App.dorald.getGuildById(m.getGuild().getId()).retrieveMember(m.getUser()).complete().mute(false).queue();
                    }

                }
                break;
        }
    }





}
