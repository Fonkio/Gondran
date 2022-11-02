package fr.fonkio.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class AbstractCommand {
    public abstract void run(SlashCommandInteractionEvent event);
}
