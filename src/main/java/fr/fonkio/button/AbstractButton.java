package fr.fonkio.button;

import fr.fonkio.Gondran;
import fr.fonkio.Poll;
import fr.fonkio.utils.EmbedGenerator;
import fr.fonkiomessage.StringsConst;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public abstract class AbstractButton {

    public abstract void run(ButtonInteractionEvent event);


    protected static Poll getPollIfValid(ButtonInteractionEvent event) {
        Poll poll = Gondran.POLL_DATA.get(event.getGuild());
        if (poll == null) {
            return null;
        }
        if (!poll.getMessageId().equals(event.getMessage().getId())) {
            event.replyEmbeds(EmbedGenerator.generate(event.getUser(), StringsConst.ERROR, StringsConst.OLD_POLL)).setEphemeral(true).queue();
            return null;
        }
        return poll;
    }
}
