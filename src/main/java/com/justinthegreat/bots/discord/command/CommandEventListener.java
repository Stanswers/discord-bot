package com.justinthegreat.bots.discord.command;

import com.justinthegreat.bots.discord.listeners.EventListener;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class CommandEventListener extends EventListener {
    public static final String PREFIX = "-";

    private Map<String, CommandEventHandler> commandEventHandlers = new HashMap<>();
    private String help;

    public CommandEventListener(CommandEventHandler... eventHandler) {
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
        if (!message.startsWith(PREFIX)) {
            return;
        }
        String argsStr = message.substring(PREFIX.length());
        if (StringUtils.isBlank(argsStr)) {
            // Probably just the prefix or the prefix followed by ws
            return;
        }
        String[] args = argsStr.split(" ");
        if ("help".equals(args[0])) {
            event.getChannel().sendMessage(help).queue();
            return;
        }
        CommandEventHandler command = commandEventHandlers.get(args[0]);
        if (command == null) {
            event.getChannel().sendMessage("Unknown command try `" + PREFIX + "help` for more info").queue();
            return;
        }
        if (args.length > 1 && command.handleHelpEvent(event, args)) {
            return;
        }
        command.handleEvent(event, args);
    }
}
