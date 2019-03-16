package com.justinthegreat.bots.discord.listeners;

import com.justinthegreat.bots.discord.BotRuntime;
import com.justinthegreat.bots.discord.command.CommandEventHandler;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class CommandEventListener extends EventListener {
    private Map<String, CommandEventHandler> commandEventHandlers = new HashMap<>();
    private String help;

    public CommandEventListener(CommandEventHandler ... eventHandler) {
        // TODO: improve help formatting
        StringBuilder sb = new StringBuilder("Available Commands:\n `help`, ");
        for (CommandEventHandler handler : eventHandler) {
            sb.append('`').append(handler.getLongName()).append('`').append(", ");
            commandEventHandlers.putIfAbsent(handler.getLongName(), handler);
            commandEventHandlers.putIfAbsent(handler.getShortName(), handler);
        }
        sb.deleteCharAt(sb.length() - 2);
        help = sb.toString();
    }

    @Override
    protected void handleEvent(MessageReceivedEvent event) {
        String message = event.getMessage().getContentDisplay();
        String prefix = BotRuntime.getInstance().getPrefix();
        if (!message.startsWith(prefix)) {
            return;
        }
        String argsStr = message.substring(prefix.length());
        if (StringUtils.isBlank(argsStr)) {
            // Probably just the prefix or the prefix followed by ws
            return;
        }
        String[] args = argsStr.split(" ");
        if (args.length >= 1 && "help".equals(args[0])) {
            event.getChannel().sendMessage(help).queue();
            return;
        }
        CommandEventHandler command = commandEventHandlers.get(args[0]);
        if (command == null) {
            event.getChannel().sendMessage("Unknown command try `" + BotRuntime.getInstance().getPrefix() + "help` for more info").queue();
            return;
        }
        command.handleEvent(event, args);
    }
}
