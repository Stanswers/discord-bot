package com.justinthegreat.bots.discord.listeners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageReceivedEventLogger extends EventListener {
    private final Logger logger = LoggerFactory.getLogger(MessageReceivedEvent.class);

    @Override
    protected void handleEvent(MessageReceivedEvent event) {
        if (!logger.isDebugEnabled()) {
            return;
        }
        User author = event.getAuthor();
        StringBuilder sb = new StringBuilder("MessageReceivedEvent-> user: " + author.getName() + "#" + author.getDiscriminator());
        if (event.getMember() != null) {
            sb.append(" ");
            sb.append(event.getMember().getEffectiveName());
            sb.append(" ");
        }
        sb.append(event.getMessage().getContentDisplay());
        logger.debug(sb.toString());
    }
}
