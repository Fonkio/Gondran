package fr.fonkio.command.impl;

import fr.fonkio.Gondran;
import fr.fonkio.Poll;
import fr.fonkio.command.AbstractCommand;
import fr.fonkio.utils.EmbedGenerator;
import fr.fonkio.utils.IdEnum;
import fr.fonkiomessage.StringsConst;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class PollCommand extends AbstractCommand {
    @Override
    public void run(SlashCommandInteractionEvent eventSlash) {
        Guild guild = eventSlash.getGuild();
        OptionMapping nbJoueursOption = eventSlash.getOption("nb-joueurs-max");
        long nbJoueurs;
        if (nbJoueursOption != null) {
            nbJoueurs = nbJoueursOption.getAsLong();
            if (nbJoueurs <= 0 || nbJoueurs > 25) {
                eventSlash.replyEmbeds(EmbedGenerator.generate(eventSlash.getUser(), StringsConst.ERROR, StringsConst.ILLEGALL_INTEGER_ARGUMENT)).setEphemeral(true).queue();
                return;
            }
        } else {
            return;
        }
        OptionMapping nomJeuOption = eventSlash.getOption("nom-jeu");
        String nomJeu;
        if (nomJeuOption != null) {
            nomJeu = nomJeuOption.getAsString();
            if (nomJeu.isBlank() || nomJeu.length() > 40) {
                eventSlash.replyEmbeds(EmbedGenerator.generate(eventSlash.getUser(), StringsConst.ERROR, StringsConst.ILLEGALL_STRING_ARGUMENT)).setEphemeral(true).queue();
                return;
            }
        } else {
            return;
        }
        Poll poll = new Poll(eventSlash.getMember(), nomJeu, nbJoueurs);
        Gondran.POLL_DATA.put(guild, poll);
        Button btnReserver = Button.success(IdEnum.BUTTON_REGISTER.getId(), StringsConst.REGISTER_BUTTON).withEmoji(StringsConst.PLUS_EMOJI);
        Button btnAnnuler = Button.danger(IdEnum.BUTTON_UNREGISTER.getId(),StringsConst.UNREGISTER_BUTTON).withEmoji(StringsConst.MINUS_EMOJI);
        InteractionHook interactionHook = eventSlash.replyEmbeds(poll.getPoolEmbed().build()).addActionRow(btnReserver, btnAnnuler).complete();
        Message message = interactionHook.retrieveOriginal().complete();
        poll.setMessageId(message.getId());
    }
}
