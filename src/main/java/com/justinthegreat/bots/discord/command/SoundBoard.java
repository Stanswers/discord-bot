package com.justinthegreat.bots.discord.command;

import com.justinthegreat.bots.discord.BotRuntime;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class SoundBoard extends PlayAudioEventHandler {
    private static final String[] SUPPORTED_EXTENSIONS = {"mp3", "wav", "flac", "mp4", "mp4a"};

    private Map<String, String> keywordToSfxPath = new TreeMap<>();
    private String availableSounds;

    public SoundBoard(String path) {
        File sbDir = new File(path);
        if (sbDir.isDirectory()) {
            for (File file : sbDir.listFiles()) {
                addSfx(file);
            }
        }
        availableSounds = Arrays.toString(keywordToSfxPath.keySet().toArray());
    }

    private void addSfx(File file) {
        if (file.isFile() && FilenameUtils.isExtension(file.getName(), SUPPORTED_EXTENSIONS)) {
            String keyword = FilenameUtils.removeExtension(file.getName());
            keywordToSfxPath.put(keyword, file.getAbsolutePath());
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
    protected boolean handleNonAudioEvent(MessageReceivedEvent event, String[] args) {
        if (args.length > 1) {
            if ("help".equals(args[1])) {
                event.getChannel().sendMessage("```sb help|list|<sound>```").queue();
                return true;
            }
            if ("list".equals(args[1])) {
                event.getChannel().sendMessage("Available sounds: ```" + availableSounds + "```").queue();
                return true;
            }
        }
        return false;
    }

    @Override
    protected String getUrl(MessageReceivedEvent event, String[] args) {
        if (args.length < 2) {
            event.getChannel().sendMessage("No sound given, use `" + BotRuntime.getInstance().getPrefix() + "sb list` to view all available sounds").queue();
            return null;
        }
        String sound = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        String path = keywordToSfxPath.get(sound);
        if (path == null) {
            event.getChannel().sendMessage("Unknown sound " + sound + "\nUse `" + BotRuntime.getInstance().getPrefix() + "sb list` to view all available sounds").queue();
        }
        return path;
    }
}
