package com.justinthegreat.bots.discord.player;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SoundBoard {
    private static final String[] SUPPORTED_EXTENSIONS = {"mp3", "wav", "flac", "mp4", "mp4a"};

    private final Map<String, String> keywordToSfxPath = new TreeMap<>();
    private String availableSounds = "";
    private Pattern pattern = Pattern.compile("$^");

    public SoundBoard(String path) {
        File sbDir = new File(path);
        if (sbDir.isDirectory()) {
            for (File file : sbDir.listFiles()) {
                if (file.isFile() && FilenameUtils.isExtension(file.getName(), SUPPORTED_EXTENSIONS)) {
                    String keyword = FilenameUtils.removeExtension(file.getName());
                    keywordToSfxPath.put(keyword.toLowerCase(), file.getAbsolutePath());
                }
            }
        }
        if (!keywordToSfxPath.isEmpty()) {
            List<String> keywords = new ArrayList<>(keywordToSfxPath.keySet());
            availableSounds = keywords.toString();
            Collections.reverse(keywords);
            String kwds = String.join("|", keywords);
            pattern = Pattern.compile("((" + kwds + ")(?!" + kwds + ")+)+", Pattern.CASE_INSENSITIVE);
        }
    }

    public String availableSounds() {
        return availableSounds;
    }

    public String sfxPath(String keyword) {
        return keywordToSfxPath.get(keyword);
    }

    public List<String> getSfxPathsFromMessage(String message) {
        Matcher matcher = pattern.matcher(message);
        List<String> results = new ArrayList<>();
        while (matcher.find()) {
            String path = keywordToSfxPath.get(matcher.group().toLowerCase());
            if (StringUtils.isNotBlank(path)) {
                results.add(path);
            }
        }
        return results;
    }
}
