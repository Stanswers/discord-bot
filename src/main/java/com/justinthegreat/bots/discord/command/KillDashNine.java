package com.justinthegreat.bots.discord.command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class KillDashNine extends PlayAudioEventHandler {
    @Override
    protected String getUrl(MessageReceivedEvent event, String[] args) {
        return "http://graphics.stanford.edu/~monzy/KillDashNine.mp3";
    }

    @Override
    protected boolean handleNonAudioEvent(MessageReceivedEvent event, String[] args) {
        if (args.length >= 2 && "help".equals(args[0])) {
            event.getChannel().sendMessage("```killdashnine|kill -9```").queue();
            return true;
        }
        return false;
    }

    @Override
    public String getLongName() {
        return "killdashnine";
    }

    @Override
    public String getShortName() {
        return "killdashnine";
    }
}
