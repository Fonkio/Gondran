package fr.fonkio.listener;

import fr.fonkio.command.AbstractCommand;
import fr.fonkio.command.impl.PollCommand;
import fr.fonkio.utils.IdEnum;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class EventSlashCommandInteraction extends ListenerAdapter {

    AbstractCommand pollCommand = new PollCommand();

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        IdEnum id = IdEnum.parseString(event.getName());
        if (id == null) {
            return;
        }
        switch (id) {
            case COMMAND_POLL:
                pollCommand.run(event);
                break;
        }
    }
}
