package com.justinthegreat.bots.discord.listeners;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class TrollGeorge extends EventListener {

    @Override
    protected void handleEvent(MessageReceivedEvent event) {
        User author = event.getAuthor();
        if (author != null && 222948750941880323L == author.getIdLong()) {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("Suck a dick!").queue();
        }
    }
}
