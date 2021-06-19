package fr.fonkio;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GondranListener implements EventListener {

    private BotData data;
    public GondranListener(BotData data) {
        this.data = data;
    }

    @Override
    public void onEvent(GenericEvent genericEvent) {
        if (genericEvent instanceof MessageReceivedEvent) {
            onMessage((MessageReceivedEvent)genericEvent);
        } else if (genericEvent instanceof ButtonClickEvent) {
            onButton((ButtonClickEvent)genericEvent);
        }
    }

    private void onButton(ButtonClickEvent event) {
        if (event.getMessage().equals(data.getPool())) {
            MessageAction ma;
            switch (event.getComponentId()) {
                case "reserver":
                    event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(789948897698381824l)).queue();
                    if (data.getNbPlaceReservee() >= 15) {
                        return;
                    }
                    if (data.getPoolResult().containsKey(event.getMember())) {
                        data.getPoolResult().put(event.getMember(), data.getPoolResult().get(event.getMember()) + 1);
                    } else {
                        data.getPoolResult().put(event.getMember(), 1);
                    }
                    data.setNbPlaceReservee(data.getNbPlaceReservee()+1);
                    event.deferEdit().queue();
                    ma = data.getPool().editMessage(poolUpdate(event.getUser(), PoolStatus.JOIN).build());
                    addButton(ma).queue();
                    break;
                case "annuler":
                    if (data.getPoolResult().containsKey(event.getMember())) {
                        if(data.getPoolResult().get(event.getMember())>1) {
                            data.getPoolResult().put(event.getMember(), data.getPoolResult().get(event.getMember())-1);
                        } else {
                            data.getPoolResult().remove(event.getMember());
                        }
                        data.setNbPlaceReservee(data.getNbPlaceReservee()-1);
                        event.deferEdit().queue();
                        ma = data.getPool().editMessage(poolUpdate(event.getUser(), PoolStatus.LEAVE).build());
                        addButton(ma).queue();
                    }
                    break;
            }
        }
    }

    private MessageAction addButton(MessageAction ma) {
        List<Component> buttons = new ArrayList<>();

        Button btnReserver = Button.success("reserver","+1 | Réserver");
        if (data.getNbPlaceReservee() >= 15) {
            btnReserver = btnReserver.asDisabled();
        }
        buttons.add(btnReserver);
        Button btnAnnuler = Button.danger("annuler","-1 | Se désinscrire");
        if(data.getNbPlaceReservee() <= 0) {
            btnAnnuler = btnAnnuler.asDisabled();
        }
        buttons.add(btnAnnuler);
        ma.setActionRow(buttons);
        return ma;
    }

    private EmbedBuilder poolUpdate(User user, PoolStatus status) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.GREEN);
        eb.setAuthor("Qui veut Among Us ?");
        eb.setTitle("Liste des joueurs présents");
        eb.setDescription("Pour t'ajouter ou te retirer, cliques sur les boutons\n" +
                "Tu peux cliquer plusieurs fois pour réserver des places à plusieurs personnes");
        eb.setFooter(String.format(status.getMessage(), user.getName()), user.getAvatarUrl());


        eb.setThumbnail("https://assets.letemps.ch/sites/default/files/styles/article_detail_mobile/public/media/2020/12/11/file7dl1zw2zdso10nejy3iz.jpg?itok=JUndPsxW");
        eb.addBlankField(false);
        if (data.getPoolResult().isEmpty()) {
            MessageEmbed.Field field = new MessageEmbed.Field("Aucun joueur", "", false);
            eb.addField(field);

        } else {
            for (Member m : data.getPoolResult().keySet()) {
                int nb = data.getPoolResult().get(m);
                MessageEmbed.Field field = new MessageEmbed.Field(m.getEffectiveName(), "Réserve "+data.getPoolResult().get(m)+" place"+(nb>1?"s":""), false);
                eb.addField(field);
            }
        }
        eb.addBlankField(false);

        MessageEmbed.Field field = new MessageEmbed.Field("Nombre de places", data.getNbPlaceReservee()+"/15 (Il reste "+(15-data.getNbPlaceReservee())+" place"+((15-data.getNbPlaceReservee())>1?"s":"")+")\n\n**Dernière action :**", false);
        eb.addField(field);
        return eb;
    }

    private void onMessage(MessageReceivedEvent event) {
        String message = event.getMessage().getContentDisplay();
        if(!message.startsWith(":")) {
            return;
        }
        if (message.startsWith(":poll")) {
            event.getMessage().delete().queue();
            data.setPoolResult(new HashMap<>());
            data.setNbPlaceReservee(0);
            event.getChannel().sendMessage(event.getGuild().getRoleById(789948897698381824l).getAsMention()).queue();
            MessageAction ma = event.getChannel().sendMessage(poolUpdate(event.getAuthor(), PoolStatus.CREATE).build());
            data.setPool(addButton(ma).complete());
        }
    }
}
