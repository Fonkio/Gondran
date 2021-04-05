package fr.fonkio;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class GondranListener implements EventListener {

    private BotData data;
    public GondranListener(BotData data) {
        this.data = data;
    }

    @Override
    public void onEvent(GenericEvent genericEvent) {
        if (genericEvent instanceof MessageReceivedEvent) {
            onMessage((MessageReceivedEvent)genericEvent);
        } else if (genericEvent instanceof MessageReactionAddEvent) {
            onReaction((MessageReactionAddEvent) genericEvent);
        }
    }

    private void onReaction(MessageReactionAddEvent event) {
        if (!event.getMember().getUser().equals(event.getJDA().getSelfUser())) {
            if (event.retrieveMessage().complete().equals(data.getMessageJoueurs())) {
                event.getReaction().removeReaction(event.getUser()).queue();
                int indice = -1;
                switch (event.getReactionEmote().getEmoji()) {
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
                    for (Joueur joueur : data.getJoueurs().values())        {
                        if (joueur.getNumero() == indice) {
                            joueur.setMember(event.getMember());
                        } else {
                            if (joueur.getMember() != null && joueur.getMember().equals(event.getMember())){
                                joueur.setMember(null);
                            }
                        }
                    }
                    data.majMessage();
                }
            } else if (event.retrieveMessage().complete().equals(data.getPool())) {
                event.getReaction().removeReaction(event.getUser()).queue();
                switch (event.getReactionEmote().getEmoji()) {
                    case "➕":
                        event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(789948897698381824l)).queue();
                        if (data.getNbPlaceReservee() >= 10) {
                            return;
                        }
                        if (data.getPoolResult().containsKey(event.getMember())) {
                            data.getPoolResult().put(event.getMember(), data.getPoolResult().get(event.getMember()) + 1);
                        } else {
                            data.getPoolResult().put(event.getMember(), 1);
                        }
                        data.setNbPlaceReservee(data.getNbPlaceReservee()+1);
                        data.getPool().editMessage(poolUpdate().build()).queue();
                        break;
                    case "➖":
                        if (data.getPoolResult().containsKey(event.getMember())) {
                            if(data.getPoolResult().get(event.getMember())>1) {
                                data.getPoolResult().put(event.getMember(), data.getPoolResult().get(event.getMember())-1);
                            } else {
                                data.getPoolResult().remove(event.getMember());
                            }
                            data.setNbPlaceReservee(data.getNbPlaceReservee()-1);
                            data.getPool().editMessage(poolUpdate().build()).queue();
                        }
                        break;
                }
            }
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
        if (data.getPoolResult().isEmpty()) {
            MessageEmbed.Field field = new MessageEmbed.Field("Aucun joueur", "", false);
            eb.addField(field);
            return eb;
        }
        for (Member m : data.getPoolResult().keySet()) {
            int nb = data.getPoolResult().get(m);
            MessageEmbed.Field field = new MessageEmbed.Field(m.getEffectiveName(), "Réserve "+data.getPoolResult().get(m)+" place"+(nb>1?"s":""), false);
            eb.addField(field);

        }
        eb.addBlankField(false);
        MessageEmbed.Field field = new MessageEmbed.Field("Nombre de places", data.getNbPlaceReservee()+"/10 (Il reste "+(10-data.getNbPlaceReservee())+" place"+((10-data.getNbPlaceReservee())>1?"s":"")+")" , false);
        eb.addField(field);
        return eb;
    }

    private void onMessage(MessageReceivedEvent genericEvent) {
        String message = genericEvent.getMessage().getContentDisplay();
        if(!message.startsWith(":")) {
            return;
        }
        if(message.startsWith(":start")) {
            data.setAuthor(genericEvent.getGuild().getMember(genericEvent.getAuthor()));
            data.setTextChannel(genericEvent.getTextChannel());
            data.setVoiceChannel(data.getAuthor().getVoiceState().getChannel());
            String ip = message.replace(":start ", "");
            data.getTextChannel().sendMessage("Connexion du bot en cours ...").queue();
            try {

                data.setClient(new ClientAU(new URI("ws://"+ip+":42069/api"), new CaptureEvent(data), this.data));
                data.getClient().connect();
                genericEvent.getMessage().delete().queue();
            } catch (URISyntaxException e) {
                data.getTextChannel().sendMessage("Echec de la connexion ! Voir console").queue();
                e.printStackTrace();
            }
        } else if (message.startsWith(":add ")) {
            String pseudo = message.replace(":add ", "");
            genericEvent.getMessage().delete().queue();
            data.ajouterJoueur(pseudo);
            data.majMessage();
        } else if (message.startsWith(":rm ")) {
            String pseudo = message.replace(":rm ", "");
            genericEvent.getMessage().delete().queue();
            data.removeJoueur(pseudo);
            data.majMessage();
        }else if (message.startsWith(":rm ")) {
            genericEvent.getMessage().delete().queue();
            String pseudo = message.replace(":rm ", "");
            if (data.getJoueurs().containsKey(pseudo)) {
                data.getJoueurs().get(pseudo).setDisconnected(true);
            }
            data.majMessage();
        } else if (message.startsWith(":unmute")) {
            genericEvent.getMessage().delete().queue();
            data.umudAll();
        } else if (message.startsWith(":poll")) {
            genericEvent.getMessage().delete().queue();
            data.setPoolResult(new HashMap<>());
            data.setNbPlaceReservee(0);
            genericEvent.getChannel().sendMessage(genericEvent.getGuild().getRoleById(789948897698381824l).getAsMention()).queue();
            data.setPool(genericEvent.getChannel().sendMessage(poolUpdate().build()).complete());
            data.getPool().addReaction("➕").queue();
            data.getPool().addReaction("➖").queue();
        } else if (message.startsWith(":stop")) {
            genericEvent.getMessage().delete().queue();
            data.getClient().close();
            data.getTextChannel().sendMessage("Bot stoppé !");
        } else if (message.startsWith(":help")) {
            genericEvent.getMessage().delete().queue();
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Liste des commandes");
            String prefix = ":";
            eb.addBlankField(false);
            eb.addField(prefix+"start [Adresse]", "Lance la connexion sur l'API AmongUsCapture (penser à ouvrir le port 42069)", false);
            eb.addField(prefix+"add [Pseudo en jeu]", "Ajouter une personne qui n'est pas dans la liste / déconnectée", false);
            eb.addField(prefix+"rm [Pseudo en jeu]", "Supprimer une personne de la partie", false);
            eb.addField(prefix+"unmute", "Demute tout le monde en cas de problème", true);
            eb.addField(prefix+"poll", "Lance un sondage", true);
            eb.addField(prefix+"stop", "Déconnecte le bot de l'API", true);
            eb.setColor(Color.MAGENTA);
            genericEvent.getChannel().sendMessage(eb.build()).queue();
        }
    }
}
