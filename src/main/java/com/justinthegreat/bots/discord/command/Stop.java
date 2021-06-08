package com.justinthegreat.bots.discord.command;

import com.justinthegreat.bots.discord.player.GuildAudioPlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Stop implements CommandEventHandler {

    @Override
    public String getLongName() {
        return "stop";
    }

    @Override
    public String getShortName() {
        return "s";
    }

    @Override
    public void handleEvent(MessageReceivedEvent event, String[] args) {
        GuildAudioPlayerManager.getInstance().getAudioPlayer(event.getGuild()).stopTrack();
    }

    @Override
    public boolean handleHelpEvent(MessageReceivedEvent event, String[] args) {
        if ("help".equals(args[1])) {
            event.getChannel().sendMessage("```" + CommandEventListener.PREFIX + "pa [help]```").queue();
            return true;
        }
        return false;
    }
}
