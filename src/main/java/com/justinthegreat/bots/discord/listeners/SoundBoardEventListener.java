package com.justinthegreat.bots.discord.listeners;

import com.justinthegreat.bots.discord.command.CommandEventListener;
import com.justinthegreat.bots.discord.player.GuildAudioPlayerManager;
import com.justinthegreat.bots.discord.player.SoundBoard;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

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
            try {
                GuildAudioPlayerManager.getInstance().loadItem(result, guild, voiceChannel, channel).get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Caught Exception While Attempting to load item [" + result + "]", e);
            }
        }
        return;
    }
}
