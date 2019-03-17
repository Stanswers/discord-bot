package com.justinthegreat.bots.discord.command;

import com.justinthegreat.bots.discord.BotRuntime;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Pause implements CommandEventHandler {
    @Override
    public String getLongName() {
        return "pause";
    }

    @Override
    public String getShortName() {
        return "pa";
    }

    @Override
    public void handleEvent(MessageReceivedEvent event, String[] args) {
        Guild guild = event.getGuild();
        if (guild == null) {
            return;
        }
        BotRuntime.getInstance().getAudioPlayer(guild).setPaused(true);
    }
}
