package fr.fonkio;

import fr.fonkiomessage.StringsConst;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Poll {

    private int nbReserved = 0;
    private Member lastActionAuthor;
    private PollActionEnum lastAction;
    private final Map<Member, Integer> result;

    private String messageId;

    public Poll(Member author) {
        this.lastActionAuthor = author;
        this.lastAction = PollActionEnum.CREATE;
        this.result = new HashMap<>();
    }

    public void register(Member member) throws ReservationException {
        if (nbReserved == Gondran.MAX_RESERVATION) {
            throw new ReservationException(StringsConst.NO_MORE_SIT);
        }
        int newAmount;
        if (result.containsKey(member)) {
            newAmount = result.get(member)+1;
        } else {
            newAmount = 1;
        }
        this.result.put(member, newAmount);
        this.nbReserved++;
        this.lastAction = PollActionEnum.JOIN;
        this.lastActionAuthor = member;
    }

    public void unregister(Member member) throws ReservationException {
        int newAmount;
        if (result.containsKey(member)) {
            newAmount = result.get(member)-1;
        } else {
            throw new ReservationException(StringsConst.NOT_REGISTERED);
        }
        if (newAmount < 0) {
            throw new ReservationException(StringsConst.ALREADY_NO_SIT_LEFT);
        }
        result.put(member, newAmount);
        nbReserved--;
        this.lastAction = PollActionEnum.LEAVE;
        this.lastActionAuthor = member;
    }

    public EmbedBuilder getPoolEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.GREEN);
        eb.setAuthor(StringsConst.WHO_AMONGUS);
        eb.setTitle(StringsConst.PLAYER_LIST);
        eb.setDescription(StringsConst.POLL_INSTRUCTION);
        eb.setFooter(String.format(lastAction.getMessage(), lastActionAuthor.getEffectiveName()), lastActionAuthor.getEffectiveAvatarUrl());


        eb.setThumbnail("https://assets.letemps.ch/sites/default/files/styles/article_detail_mobile/public/media/2020/12/11/file7dl1zw2zdso10nejy3iz.jpg?itok=JUndPsxW");
        eb.addBlankField(false);

        for (Member m : result.keySet()) {
            int nb = result.get(m);
            if (nb > 0) {
                MessageEmbed.Field field = new MessageEmbed.Field(m.getEffectiveName(), "Réserve "+result.get(m)+" place"+(nb>1?"s":""), false);
                eb.addField(field);
            }
        }
        if (eb.getFields().isEmpty()) {
            MessageEmbed.Field field = new MessageEmbed.Field("Aucun joueur", "", false);
            eb.addField(field);
        }
        eb.addBlankField(false);

        MessageEmbed.Field field = new MessageEmbed.Field("Nombre de places", nbReserved+"/"+Gondran.MAX_RESERVATION+" (Il reste "+(15-nbReserved)+" place"+((Gondran.MAX_RESERVATION-nbReserved)>1?"s":"")+")\n\n**Dernière action :**", false);
        eb.addField(field);
        return eb;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
