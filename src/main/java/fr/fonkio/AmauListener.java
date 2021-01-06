package fr.fonkio;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.json.JSONException;
import org.json.JSONObject;


import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;


public class AmauListener implements EventListener {

    public TextChannel textChannel;
    public Member author;
    public VoiceChannel voiceChannel;
    private String lastGame;
    private Message messageJoueurs;
    private Map<String, Joueur> joueurs = new TreeMap<>();
    private Message pool;
    private Map<Member, Integer> poolResult;
    private int nbPlaceReservee = 0;

    @Override
    public void onEvent(GenericEvent genericEvent) {
        if (genericEvent instanceof MessageReceivedEvent) {
            onMessage((MessageReceivedEvent)genericEvent);
        } else if (genericEvent instanceof MessageReactionAddEvent) {
            onReaction((MessageReactionAddEvent) genericEvent);
        }
    }

    private void onReaction(MessageReactionAddEvent genericEvent) {
        if (!genericEvent.getMember().getUser().equals(genericEvent.getJDA().getSelfUser())) {
            if (genericEvent.retrieveMessage().complete().equals(messageJoueurs)) {
                genericEvent.getReaction().removeReaction(genericEvent.getUser()).queue();
                int indice = -1;
                switch (genericEvent.getReactionEmote().getEmoji()) {
                    case "1️⃣":
                        indice = 0;
                        break;
                    case "2️⃣":
                        indice = 1;
                        break;
                    case "3️⃣":
                        indice = 2;
                        break;
                    case "4️⃣":
                        indice = 3;
                        break;
                    case "5️⃣":
                        indice = 4;
                        break;
                    case "6️⃣":
                        indice = 5;
                        break;
                    case "7️⃣":
                        indice = 6;
                        break;
                    case "8️⃣":
                        indice = 7;
                        break;
                    case "9️⃣":
                        indice = 8;
                        break;
                    case "\uD83D\uDD1F":
                        indice = 9;
                        break;
                }
                if (indice != -1) {
                    for (Joueur joueur : joueurs.values())        {
                        if (joueur.getNumero() == indice) {
                            joueur.setMember(genericEvent.getMember());
                        } else {
                            if (joueur.getMember() != null && joueur.getMember().equals(genericEvent.getMember())){
                                joueur.setMember(null);
                            }
                        }
                    }
                    majMessage();
                }
            } else if (genericEvent.retrieveMessage().complete().equals(pool)) {
                genericEvent.getReaction().removeReaction(genericEvent.getUser()).queue();
                switch (genericEvent.getReactionEmote().getEmoji()) {
                    case "➕":
                        genericEvent.getGuild().addRoleToMember(genericEvent.getMember(), genericEvent.getGuild().getRoleById(789948897698381824l)).queue();
                        if (nbPlaceReservee >= 10) {
                            return;
                        }
                        if (poolResult.containsKey(genericEvent.getMember())) {
                            poolResult.put(genericEvent.getMember(), poolResult.get(genericEvent.getMember()) + 1);
                        } else {
                            poolResult.put(genericEvent.getMember(), 1);
                        }
                        nbPlaceReservee++;
                        pool.editMessage(poolUpdate().build()).queue();
                        break;
                    case "➖":
                        if (poolResult.containsKey(genericEvent.getMember())) {
                            if(poolResult.get(genericEvent.getMember())>1) {
                                poolResult.put(genericEvent.getMember(), poolResult.get(genericEvent.getMember())-1);
                            } else {
                                poolResult.remove(genericEvent.getMember());
                            }
                            nbPlaceReservee--;
                            pool.editMessage(poolUpdate().build()).queue();
                        }
                        break;
                }
            }
        }

    }

    private void onMessage(MessageReceivedEvent genericEvent) {
        String message = genericEvent.getMessage().getContentDisplay();
        if(!message.startsWith(":")) {
            return;
        }
        if(message.startsWith(":start")) {
            author = genericEvent.getGuild().getMember(genericEvent.getAuthor());
            textChannel = genericEvent.getTextChannel();
            voiceChannel = author.getVoiceState().getChannel();
            String ip = message.replace(":start ", "");
            try {
                App.client = new ClientAU(new URI("ws://"+ip+":42069/api"), this);
                App.client.connect();
                genericEvent.getMessage().delete().queue();
                PrivateChannel pc = author.getUser().openPrivateChannel().complete();
                pc.sendMessage("Connecté ! ").queue();
            } catch (URISyntaxException e) {
                PrivateChannel pc = author.getUser().openPrivateChannel().complete();
                pc.sendMessage("Erreur ! Voir console");
                e.printStackTrace();
            }
        } else if (message.startsWith(":add ")) {
            String pseudo = message.replace(":add ", "");
            ajouterJoueur(pseudo);
            majMessage();
        } else if (message.startsWith(":rm ")) {
            String pseudo = message.replace(":rm ", "");
            if (joueurs.containsKey(pseudo)) {
                joueurs.get(pseudo).setDisconnected(true);
            }
            majMessage();
        } else if (message.startsWith(":unmute")) {
            unmuteAll();
        } else if (message.startsWith(":pool")) {
            genericEvent.getMessage().delete().queue();
            poolResult = new HashMap<>();
            nbPlaceReservee = 0;
            genericEvent.getChannel().sendMessage(genericEvent.getGuild().getRoleById(789948897698381824l).getAsMention()).queue();
            pool = genericEvent.getChannel().sendMessage(poolUpdate().build()).complete();
            pool.addReaction("➕").queue();
            pool.addReaction("➖").queue();
        }
    }

    private EmbedBuilder poolUpdate() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.PINK);
        eb.setAuthor("Qui veut Among Us ?");
        eb.setTitle("Liste des joueurs présents");
        eb.setDescription("Pour t'ajouter ou te retirer, cliques sur les réactions");
        eb.setFooter("Tu peux réagir plusieurs fois pour réserver des places à plusieurs personnes");
        eb.setThumbnail("https://assets.letemps.ch/sites/default/files/styles/article_detail_mobile/public/media/2020/12/11/file7dl1zw2zdso10nejy3iz.jpg?itok=JUndPsxW");
        eb.addBlankField(false);
        if (poolResult.isEmpty()) {
            MessageEmbed.Field field = new MessageEmbed.Field("Aucun joueur", "", false);
            eb.addField(field);
            return eb;
        }
        for (Member m : poolResult.keySet()) {
            int nb = poolResult.get(m);
            MessageEmbed.Field field = new MessageEmbed.Field(m.getEffectiveName(), "Réserve "+poolResult.get(m)+" place"+(nb>1?"s":""), false);
            eb.addField(field);

        }
        eb.addBlankField(false);
        MessageEmbed.Field field = new MessageEmbed.Field("Nombre de places", nbPlaceReservee+"/10 (Il reste "+(10-nbPlaceReservee)+" place"+((10-nbPlaceReservee)>1?"s":"")+")" , false);
        eb.addField(field);
        return eb;
    }

    public void code(JSONObject jsonObject) throws JSONException {
        String code = jsonObject.getString("LobbyCode");
        if (lastGame != null && lastGame.equals(code)) {
            return;
        }
        this.lastGame = code;
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
        }
        field = new MessageEmbed.Field("", "", true);
        eb.addField(field);
        field = new MessageEmbed.Field("Serveur", serveur, true);
        eb.addField(field);
        field = new MessageEmbed.Field("Map", map, true);
        eb.addField(field);
        field = new MessageEmbed.Field("", "", true);
        eb.addField(field);
        field = new MessageEmbed.Field("Créateur", author.getEffectiveName(), true);
        eb.addField(field);
        eb.setColor(Color.GREEN);
        eb.setImage(image);
        textChannel.sendMessage(eb.build()).queue();
        messageJoueurs = textChannel.sendMessage("Attente connexion joueurs...\n").complete();
        for(Joueur j : joueurs.values()) {
            int indice = j.getNumero();
            if (indice != -1) {
                messageJoueurs.addReaction(getNumber(indice)).queue();
            }
        }
        majMessage();
    }

    public void state(JSONObject jsonObject) throws JSONException {
        switch (jsonObject.getInt("NewState")) {
            case 3: //Menu
                for (Joueur joueur : joueurs.values()) {
                    joueur.setDisconnected(true);
                }
                unmuteAll();
                majMessage();
                break;
            case 0: //Lobby
                for (Joueur j : joueurs.values()) {
                    j.setDead(false);
                }
                break;
            case 1: //Game
                Timer timer;
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        muteAll();
                    }
                }, 7000);
                break;
            case 2: //Reunion
                unmuteAlive();
                break;
            case 4: //Fin
                unmuteAll();
                break;
        }
    }

    private void muteAll() {
        for(Joueur j : joueurs.values()){
            if (j.getMember() != null && (!j.getMember().getVoiceState().isGuildDeafened()) && (voiceChannel.getMembers().contains(j.getMember()))) {
                j.getMember().deafen(true).queue();
            }
        }
    }

    private void unmuteAll() {
        for(Member m : voiceChannel.getMembers()){
            if (m.getVoiceState().isGuildDeafened()) {
                m.deafen(false).queue();
            }
            if (m.getVoiceState().isGuildMuted()) {
                m.mute(false).queue();
            }
        }
    }

    private void unmuteAlive() {
        for(Joueur j : joueurs.values()){
            if (j.getMember() != null && (j.isDead()) && (voiceChannel.getMembers().contains(j.getMember()))) {
                j.getMember().mute(true).queue();
            }
        }
        for(Joueur j : joueurs.values()){
            if (j.getMember() != null && (!j.isDead()) && j.getMember().getVoiceState().isGuildDeafened() && (voiceChannel.getMembers().contains(j.getMember()))) {
                j.getMember().deafen(false).queue();
            }
        }
        for(Joueur j : joueurs.values()){
            if (j.getMember() != null && (j.isDead()) && (voiceChannel.getMembers().contains(j.getMember()))) {
                j.getMember().deafen(false).queue();
            }
        }
    }

    private int getFirstEmpty() {
        for(int i = 0; i < 10; i ++) { //Recherche place libre
            boolean find = false;
            for(Joueur j : joueurs.values()) {
                if (j.getNumero() == i) {
                    find = true;
                }
            }
            if (!find) {
                System.out.println(i);
                return i;
            }
        }
        for(int i = 0; i < 10; i ++) { //Recherche place prise par un deco
            for(Joueur j : joueurs.values()) {
                if (j.getNumero() == i) {
                    if (j.isDisconnected()) {
                        j.setNumero(-1);
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public void ajouterJoueur(String joueur) {
        if (joueurs.containsKey(joueur)) {
            joueurs.get(joueur).setDisconnected(false);
            if(joueurs.get(joueur).getNumero() == -1) {
                joueurs.get(joueur).setNumero(getFirstEmpty());
            }
        } else {
            joueurs.put(joueur, new Joueur(null));
            joueurs.get(joueur).setNumero(getFirstEmpty());
            joueurs.get(joueur).setDisconnected(false);
        }
        if (messageJoueurs != null) {
            messageJoueurs.addReaction(getNumber(joueurs.get(joueur).getNumero())).queue();
        }

    }

    public void playerAction(JSONObject eventData) throws JSONException {
        String name = eventData.getString("Name");
        switch (eventData.getInt("Action")) {
            case 0: //Join
                ajouterJoueur(name);
                majMessage();
                break;
            case 1: //Left
                if (joueurs.containsKey(name)) {
                    joueurs.get(name).setDisconnected(true);
                    majMessage();
                }
                break;
            case 2: //Death
                joueurs.get(name).setDead(true);
                break;
        }
    }

    private void majMessage() {
        if(messageJoueurs == null) {
            return;
        }
        if (joueurs.isEmpty()) {
            messageJoueurs.editMessage("Attente connexion joueurs...\n").queue();
            return;
        }
        StringBuilder sb = new StringBuilder("Réagissez par votre numéro\n");
        for (int i = 0; i < 10; i++) {
            String entry = null;
            for (String j : joueurs.keySet()) {
                if (joueurs.get(j).getNumero() == i) {
                    entry = j;
                }
            }
            if (entry != null) {
                int numero = joueurs.get(entry).getNumero();
                if (numero != -1) {
                    sb.append("Joueur " + getNumber(numero) + " : " + entry +" ");
                    if(joueurs.get(entry).getMember() != null){
                        sb.append("("+joueurs.get(entry).getMember().getAsMention()+")");
                    }
                    if(joueurs.get(entry).isDisconnected()) {
                        sb.append(" *déconnecté*");
                    }
                    sb.append("\n");
                }
            }
        }
        /*for (Iterator<Map.Entry<String, Joueur>> it = joueurs.entrySet().iterator(); it.hasNext();) {
            Entry <String, Joueur> entry = it.next();
            int numero = entry.getValue().getNumero();
            if (numero != -1) {
                sb.append("Joueur " + getNumber(numero) + " : " + entry.getKey() +" ");
                if(entry.getValue().getMember() != null){
                    sb.append("("+entry.getValue().getMember().getAsMention()+")");
                }
                if(entry.getValue().isDisconnected()) {
                    sb.append(" *déconnecté*");
                }
                sb.append("\n");
            }
        }*/

        /*for (String nom : joueurs.keySet()) {
            int numero = joueurs.get(nom).getNumero();
            if (numero != -1) {
                sb.append("Joueur " + getNumber(numero) + " : " + nom +" ");
                if(joueurs.get(nom).getMember() != null){
                    sb.append("("+joueurs.get(nom).getMember().getAsMention()+")");
                }
                if(joueurs.get(nom).isDisconnected()) {
                    sb.append(" *déconnecté*");
                }
                sb.append("\n");
            }

        }*/
        messageJoueurs.editMessage(sb.toString()).queue();
        System.out.println(joueurs);

    }

    private String getNumber(int i) {
        switch (i) {
            case 0:
                return "1️⃣";
            case 1:
                return "2️⃣";
            case 2:
                return "3️⃣";
            case 3:
                return "4️⃣";
            case 4:
                return "5️⃣";
            case 5:
                return "6️⃣";
            case 6:
                return "7️⃣";
            case 7:
                return "8️⃣";
            case 8:
                return "9️⃣";
            case 9:
                return "\uD83D\uDD1F";
        }
        return "";
    }

}
