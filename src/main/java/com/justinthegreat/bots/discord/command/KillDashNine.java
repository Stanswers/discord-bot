package com.justinthegreat.bots.discord.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class KillDashNine extends PlayAudioEventHandler {
    @Override
    protected String getUrl(MessageReceivedEvent event, String[] args) {
        return "http://graphics.stanford.edu/~monzy/KillDashNine.mp3";
    }

    @Override
    public boolean handleHelpEvent(MessageReceivedEvent event, String[] args) {
        if ("help".equals(args[1])) {
            event.getChannel().sendMessage("```" + CommandEventListener.PREFIX + "killdashnine|kill -9```").queue();
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
        return "kill";
    }
}
