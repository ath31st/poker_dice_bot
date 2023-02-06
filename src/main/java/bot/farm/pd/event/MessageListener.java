package bot.farm.pd.event;

import bot.farm.pd.util.Command;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static bot.farm.pd.util.Command.*;

public abstract class MessageListener {

    public Mono<Void> processCommand(Message eventMessage) {
//        System.out.println(eventMessage.getAuthor().get().getUsername());
//        System.out.println(eventMessage.getAuthorAsMember().block().getDisplayName());
//        System.out.println(eventMessage.getAuthor().get().getId().asLong());

        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(message -> checkOccurrence(message.getContent()))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage(executeCommand(eventMessage)))
                .then();
    }

    private boolean checkOccurrence(String command) {
        if (!command.startsWith("/")) return false;

        Pattern pattern = null;
        if (command.startsWith(START.value)) {
            pattern = Pattern.compile(START.value + " (<@[0-9]{18}>){2,}");
        } else if (command.startsWith(REROLL.value)) {
            pattern = Pattern.compile(REROLL.value + "( [0-9]){1,5}$");
        }
        if (pattern != null) {
            Matcher matcher = pattern.matcher(command);
            if (matcher.matches()) return true;
        }

        return Arrays.stream(Command.values()).anyMatch(c -> c.value.equals(command));
    }

    private String executeCommand(Message message) {
        System.out.println(message.getContent());
        return message.getAuthorAsMember().block().getDisplayName() + " !";
    }
}
