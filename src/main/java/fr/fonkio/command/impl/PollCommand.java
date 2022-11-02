package fr.fonkio.command.impl;

import fr.fonkio.Gondran;
import fr.fonkio.Poll;
import fr.fonkio.command.AbstractCommand;
import fr.fonkio.utils.IdEnum;
import fr.fonkiomessage.StringsConst;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class PollCommand extends AbstractCommand {
    @Override
    public void run(SlashCommandInteractionEvent eventSlash) {
        Guild guild = eventSlash.getGuild();
        Poll poll = new Poll(eventSlash.getMember());
        Gondran.POLL_DATA.put(guild, poll);
        Button btnReserver = Button.success(IdEnum.BUTTON_REGISTER.getId(), StringsConst.REGISTER_BUTTON).withEmoji(StringsConst.PLUS_EMOJI);
        Button btnAnnuler = Button.danger(IdEnum.BUTTON_UNREGISTER.getId(),StringsConst.UNREGISTER_BUTTON).withEmoji(StringsConst.MINUS_EMOJI);
        InteractionHook interactionHook = eventSlash.replyEmbeds(poll.getPoolEmbed().build()).addActionRow(btnReserver, btnAnnuler).complete();
        Message message = interactionHook.retrieveOriginal().complete();
        poll.setMessageId(message.getId());
    }
}
