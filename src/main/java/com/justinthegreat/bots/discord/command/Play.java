package com.justinthegreat.bots.discord.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

public class Play extends PlayAudioEventHandler {
    @Override
    public boolean handleHelpEvent(MessageReceivedEvent event, String[] args) {
        if ("help".equals(args[1])) {
            event.getChannel().sendMessage("```" + CommandEventListener.PREFIX + "p [help|<song>]```").queue();
            return true;
        }
        return false;
    }

    @Override
    protected String getUrl(MessageReceivedEvent event, String[] args) {
        if (args.length < 2) {
            return null;
        }
        return String.join("", Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public String getLongName() {
        return "play";
    }

    @Override
    public String getShortName() {
        return "p";
    }
}
