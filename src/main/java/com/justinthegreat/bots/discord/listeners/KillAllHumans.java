package com.justinthegreat.bots.discord.listeners;

import com.justinthegreat.bots.discord.BotRuntime;
import com.justinthegreat.bots.discord.SoundBoard;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

public class KillAllHumans extends EventListener {
    private final SoundBoard soundBoard;

    public KillAllHumans(SoundBoard soundBoard) {
        this.soundBoard = soundBoard;
    }

    @Override
    protected void handleEvent(MessageReceivedEvent event) {
        String msg = event.getMessage().getContentDisplay();
        if (msg.startsWith(BotRuntime.getInstance().getPrefix())) {
            return;
        }
        Member member = event.getMember();
        Guild guild = event.getGuild();
        if (member == null || guild == null) {
            return;
        }
        AudioManager manager = guild.getAudioManager();
        if (manager == null) {
            return;
        }
        VoiceChannel voiceChannel = member.getVoiceState().getChannel();
        MessageChannel channel = event.getChannel();
        if (voiceChannel == null) {
            return;
        }
        String url = soundBoard.fuck(event.getMessage().getContentDisplay());
        if (url == null) {
            return;
        }
        BotRuntime.getInstance().loadItem(url, guild, voiceChannel, channel);
        return;
    }
}
