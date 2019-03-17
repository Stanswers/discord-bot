package com.justinthegreat.bots.discord.listeners;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;

public abstract class EventListener extends ListenerAdapter {

    public EventListener() {
    }

    protected void handleEvent(MessageReceivedEvent event) {
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentDisplay();
        User author = event.getAuthor();
        if (author == null || author.isBot() || message == null || StringUtils.isBlank(message)) {
            return;
        }
        handleEvent(event);
    }
}
