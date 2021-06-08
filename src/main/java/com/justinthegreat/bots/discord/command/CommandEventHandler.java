package com.justinthegreat.bots.discord.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface CommandEventHandler {
    String getLongName();

    String getShortName();

    void handleEvent(MessageReceivedEvent event, String[] args);

    boolean handleHelpEvent(MessageReceivedEvent event, String[] args);
}
