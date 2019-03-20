package com.justinthegreat.bots.discord.listeners;

import com.justinthegreat.bots.discord.command.CommandEventListener;
import com.justinthegreat.bots.discord.player.GuildAudioPlayerManager;
import com.justinthegreat.bots.discord.player.SoundBoard;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoundBoardEventListener extends EventListener {
    private final Logger logger = LoggerFactory.getLogger(SoundBoardEventListener.class);
    private final SoundBoard soundBoard;

    public SoundBoardEventListener(SoundBoard soundBoard) {
        this.soundBoard = soundBoard;
    }

    @Override
    protected void handleEvent(MessageReceivedEvent event) {
        String msg = event.getMessage().getContentDisplay();
        Member member = event.getMember();
        if (msg.startsWith(CommandEventListener.PREFIX)) {
            return;
        }
        Guild guild = event.getGuild();
        AudioManager manager = guild.getAudioManager();
        if (manager == null) {
            return;
        }
        VoiceChannel voiceChannel = member.getVoiceState().getChannel();
        MessageChannel channel = event.getChannel();
        if (voiceChannel == null) {
            return;
        }
        for (String result : soundBoard.getSfxPathsFromMessage(msg)) {
            logger.debug(result);
            GuildAudioPlayerManager.getInstance().loadItem(result, guild, voiceChannel, channel);
        }
        return;
    }
}
