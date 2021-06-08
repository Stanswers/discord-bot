package com.justinthegreat.bots.discord.command;

import com.justinthegreat.bots.discord.player.SoundBoard;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

public class SoundBoardCommand extends PlayAudioEventHandler {
    private final SoundBoard soundBoard;

    public SoundBoardCommand(SoundBoard soundBoard) {
        this.soundBoard = soundBoard;
    }

    @Override
    public String getLongName() {
        return "soundboard";
    }

    @Override
    public String getShortName() {
        return "sb";
    }

    @Override
    public boolean handleHelpEvent(MessageReceivedEvent event, String[] args) {
        if ("help".equals(args[1])) {
            event.getChannel().sendMessage("```" + CommandEventListener.PREFIX + "sb help|list|<sound>```").queue();
            return true;
        }
        if ("list".equals(args[1])) {
            event.getChannel().sendMessage("Available sounds: ```" + soundBoard.availableSounds() + "```").queue();
            return true;
        }
        return false;
    }

    @Override
    protected String getUrl(MessageReceivedEvent event, String[] args) {
        if (args.length < 2) {
            event.getChannel().sendMessage("No sound given, use `" + CommandEventListener.PREFIX + "sb list` to view all available sounds").queue();
            return null;
        }
        String keyword = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        String path = soundBoard.sfxPath(keyword);
        if (path == null) {
            event.getChannel().sendMessage("Unknown sound " + keyword + "\nUse `" + CommandEventListener.PREFIX + "sb list` to view all available sounds").queue();
        }
        return path;
    }
}
