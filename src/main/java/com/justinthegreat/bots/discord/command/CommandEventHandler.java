package com.justinthegreat.bots.discord.command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface CommandEventHandler {
    String getLongName();

    String getShortName();

    void handleEvent(MessageReceivedEvent event, String[] args);
}
