package com.justinthegreat.bots.discord.command;

import com.justinthegreat.bots.discord.BotRuntime;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SoundBoardCommand implements CommandEventHandler {
    private Map<String, SoundBoard> longNameToSoundBoard = new HashMap<>();
    private Map<String, SoundBoard> shortNameToSoundBoard = new HashMap<>();

    public SoundBoardCommand(String path) {
        File sbDir = new File(path);
        if (sbDir.isDirectory()) {
            for (File dir : sbDir.listFiles()) {
                if (dir.isDirectory()) {
                    String longName = dir.getName();
                    String shortName = longName;
                    for (int i = 1; i < longName.length(); i++) {
                        shortName = longName.substring(0, i);
                        if (shortNameToSoundBoard.get(shortName) == null) {
                            break;
                        }
                    }
                    SoundBoard sb = SoundBoard.newInstance(longName, shortName, dir.listFiles());
                    longNameToSoundBoard.put(longName, sb);
                    if (!longName.equals(shortName)) {
                        shortNameToSoundBoard.put(shortName, sb);
                    }
                }
            }
        }
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
    public void handleEvent(MessageReceivedEvent event, String[] args) {
        if (args.length < 2) {
            event.getChannel().sendMessage("No soundboard specified, use `" + BotRuntime.getInstance().getPrefix() + "sb help`" + " for more information").queue();
            return;
        }
        if ("help".equals(args[1])) {
            event.getChannel().sendMessage("```sb [help|list|<soundboard>] list|<sound>```").queue();
            return;
        }
        if ("list".equals(args[1])) {
            event.getChannel().sendMessage("Available soundboards:\n```" + Arrays.toString(longNameToSoundBoard.keySet().toArray()) + "```").queue();
            return;
        }
        SoundBoard soundBoard = longNameToSoundBoard.get(args[1]);
        if (soundBoard != null) {
            soundBoard.handleEvent(event, Arrays.copyOfRange(args, 1, args.length));
            return;
        }
        soundBoard = shortNameToSoundBoard.get(args[1]);
        if (soundBoard != null) {
            soundBoard.handleEvent(event, Arrays.copyOfRange(args, 1, args.length));
            return;
        }
        event.getChannel().sendMessage("Unknown soundboard, use `" + BotRuntime.getInstance().getPrefix() + "sb list`" + " to see available sound boards").queue();
    }
}
