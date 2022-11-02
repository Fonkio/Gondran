package fr.fonkio.button.impl;

import fr.fonkio.Gondran;
import fr.fonkio.Poll;
import fr.fonkio.ReservationException;
import fr.fonkio.button.AbstractButton;
import fr.fonkio.utils.EmbedGenerator;
import fr.fonkiomessage.StringsConst;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.Nullable;

public class UnregisterButton extends AbstractButton {
    @Override
    public void run(ButtonInteractionEvent event) {
        Poll poll = getPollIfValid(event);
        if (poll == null) return;
        try {
            poll.unregister(event.getMember());
            event.editMessageEmbeds(poll.getPoolEmbed().build()).queue();
        } catch (ReservationException e) {
            event.replyEmbeds(EmbedGenerator.generate(event.getUser(), StringsConst.ERROR, e.getMessage())).setEphemeral(true).queue();
        }
    }


}
