package fr.fonkio.listener;

import fr.fonkio.button.AbstractButton;
import fr.fonkio.button.impl.RegisterButton;
import fr.fonkio.button.impl.UnregisterButton;
import fr.fonkio.utils.IdEnum;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class EventButtonInteraction extends ListenerAdapter {

    private final AbstractButton registerButton = new RegisterButton();
    private final AbstractButton unregisterButton = new UnregisterButton();
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        IdEnum id = IdEnum.parseString(event.getComponentId());
        if (id == null) {
            return;
        }
        switch (id) {
            case BUTTON_REGISTER:
                registerButton.run(event);
                break;
            case BUTTON_UNREGISTER:
                unregisterButton.run(event);
                break;
        }
    }
}
