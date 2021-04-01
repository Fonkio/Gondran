package fr.fonkio;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.java_websocket.client.WebSocketClient;

import java.util.Map;
import java.util.TreeMap;

public class BotData {
    public WebSocketClient client;
    private Message pool;
    private Message listeJoueurs;
    private Message messageJoueurs;
    private Map<String, Joueur> joueurs = new TreeMap<>();
    private int nbPlaceReservee = 0;
    private Map<Member, Integer> poolResult;
    public TextChannel textChannel;
    public Member author;
    public VoiceChannel voiceChannel;
    private String lastGame;

    public Map<String, Joueur> getJoueurs() {
        return joueurs;
    }

    public void setJoueurs(Map<String, Joueur> joueurs) {
        this.joueurs = joueurs;
    }

    public Map<Member, Integer> getPoolResult() {
        return poolResult;
    }

    public void setPoolResult(Map<Member, Integer> poolResult) {
        this.poolResult = poolResult;
    }

    public void setMessageJoueurs(Message messageJoueurs) {
        this.messageJoueurs = messageJoueurs;
    }

    public Message getMessageJoueurs() {
        return messageJoueurs;
    }

    public void setClient(WebSocketClient client) {
        this.client = client;
    }

    public void setPool(Message pool) {
        this.pool = pool;
    }

    public void setListeJoueurs(Message listeJoueurs) {
        this.listeJoueurs = listeJoueurs;
    }

    public void setNbPlaceReservee(int nbPlaceReservee) {
        this.nbPlaceReservee = nbPlaceReservee;
    }

    public void setTextChannel(TextChannel textChannel) {
        this.textChannel = textChannel;
    }

    public void setAuthor(Member author) {
        this.author = author;
    }

    public void setVoiceChannel(VoiceChannel voiceChannel) {
        this.voiceChannel = voiceChannel;
    }

    public void setLastGame(String lastGame) {
        this.lastGame = lastGame;
    }

    public WebSocketClient getClient() {
        return client;
    }

    public Message getPool() {
        return pool;
    }

    public Message getListeJoueurs() {
        return listeJoueurs;
    }

    public int getNbPlaceReservee() {
        return nbPlaceReservee;
    }

    public TextChannel getTextChannel() {
        return textChannel;
    }

    public Member getAuthor() {
        return author;
    }

    public VoiceChannel getVoiceChannel() {
        return voiceChannel;
    }

    public String getLastGame() {
        return lastGame;
    }

    public void majMessage() {
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

        messageJoueurs.editMessage(sb.toString()).queue();

    }

    public String getNumber(int i) {
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

    private int getFirstEmpty() {
        for(int i = 0; i < 10; i ++) { //Recherche place libre
            boolean find = false;
            for(Joueur j : getJoueurs().values()) {
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
            for(Joueur j : getJoueurs().values()) {
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

    public void removeJoueur(String joueur) {
        if (joueurs.containsKey(joueur)) {
            joueurs.remove(joueur);
        }
    }

    public void mdAlive() {
        System.out.println("mute / deafen vivants");
        for(Joueur j : getJoueurs().values()){
            if (!j.isDead()) {
                Member m = j.getMember();
                if (getVoiceChannel().getMembers().contains(j.getMember()) && (!m.getVoiceState().isGuildMuted())) {

                    App.dorald.getGuildById(m.getGuild().getId()).retrieveMember(j.getMember().getUser()).complete().mute(true).queue();;
                }
                if (getVoiceChannel().getMembers().contains(j.getMember()) && (!m.getVoiceState().isGuildDeafened())) {

                    j.getMember().deafen(true).queue();
                }
            }
        }
    }

    public void umudAll() {
        System.out.println("Unmute / undeafen tout le monde");
        for(Member m : getVoiceChannel().getMembers()){
            if (m.getVoiceState().isGuildDeafened()) {
                m.deafen(false).queue();
            }
            if (m.getVoiceState().isGuildMuted()) {
                App.dorald.getGuildById(m.getGuild().getId()).retrieveMember(m.getUser()).complete().mute(false).queue();
            }
        }
    }

    public void umAliveUdAll() {
        System.out.println("Unmute / undeafen les vivants");
        for(Joueur j : getJoueurs().values()){
            Member m = j.getMember();
            if (m != null && (getVoiceChannel().getMembers().contains(m))) {
                if (m.getVoiceState().isGuildDeafened()) {
                    m.deafen(false).queue();
                }
                if (m.getVoiceState().isMuted() && (!j.isDead())) {
                    App.dorald.getGuildById(m.getGuild().getId()).retrieveMember(m.getUser()).complete().mute(false).queue();
                }
            }
        }

    }

    public void umDead() {
        System.out.println("Unmute Les morts");
        for(Joueur j : getJoueurs().values()){
            Member m = j.getMember();
            if (m != null && (j.isDead()) && (getVoiceChannel().getMembers().contains(m))) {
                App.dorald.getGuildById(m.getGuild().getId()).retrieveMember(m.getUser()).complete().mute(false).queue();
            }
        }
    }

    public void mDead() {
        System.out.println("Mute Les morts");
        for(Joueur j : getJoueurs().values()){
            Member m = j.getMember();
            if (m != null && (j.isDead()) && (getVoiceChannel().getMembers().contains(m))) {
                App.dorald.getGuildById(m.getGuild().getId()).retrieveMember(m.getUser()).complete().mute(true).queue();
            }
        }
    }
}
