package com.justinthegreat.bots.discord;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
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
                    keywordToSfxPath.put(keyword, file.getAbsolutePath());
                }
            }
        }
        if (!keywordToSfxPath.isEmpty()) {
            Set<String> keywords = keywordToSfxPath.keySet();
            availableSounds = Arrays.toString(keywords.toArray());
            pattern = Pattern.compile(".*(" + String.join("|", keywords) + ").*");
        }
    }

    public String availableSounds() {
        return availableSounds;
    }

    public String sfxPath(String keyword) {
        return keywordToSfxPath.get(keyword);
    }

    public String fuck(String message) {
        Matcher matcher = pattern.matcher(message);
        if (matcher.matches()) {
            return keywordToSfxPath.get(matcher.group(0));
        }
        return null;
    }
}
