package com.justinthegreat.bots.discord.listeners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;

public abstract class EventListener extends ListenerAdapter {

    protected void handleEvent(MessageReceivedEvent event) {
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentDisplay();
        User author = event.getAuthor();
        if (author == null || author.isBot() || StringUtils.isBlank(message) || event.getGuild() == null || event.getMember() == null) {
            return;
        }
        handleEvent(event);
    }
}
