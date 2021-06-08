package com.justinthegreat.bots.discord.command;

import com.justinthegreat.bots.discord.player.GuildAudioPlayer;
import com.justinthegreat.bots.discord.player.GuildAudioPlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Next implements CommandEventHandler {
    @Override
    public String getLongName() {
        return "next";
    }

    @Override
    public String getShortName() {
        return "n";
    }

    @Override
    public void handleEvent(MessageReceivedEvent event, String[] args) {
        GuildAudioPlayer player = GuildAudioPlayerManager.getInstance().getAudioPlayer(event.getGuild());
        if (player.next() && player.isPaused()) {
            player.setPaused(false, event.getMember().getVoiceState().getChannel());
        }
    }

    @Override
    public boolean handleHelpEvent(MessageReceivedEvent event, String[] args) {
        if ("help".equals(args[1])) {
            event.getChannel().sendMessage("```" + CommandEventListener.PREFIX + "n [help]```").queue();
            return true;
        }
        return false;
    }
}
