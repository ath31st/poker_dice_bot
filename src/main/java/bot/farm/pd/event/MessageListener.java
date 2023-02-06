package bot.farm.pd.event;

import bot.farm.pd.util.Command;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

import java.util.Arrays;

public abstract class MessageListener {

    public Mono<Void> processCommand(Message eventMessage) {
//        System.out.println(eventMessage.getAuthor().get().getUsername());
//        System.out.println(eventMessage.getAuthorAsMember().block().getDisplayName());
//        System.out.println(eventMessage.getAuthor().get().getId().asLong());

        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(message -> checkOccurrence(message.getContent()))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("Ready to play poker dice. Hooray!"))
                .then();
    }

    private boolean checkOccurrence(String command) {
        if (!command.startsWith("/")) return false;
        return Arrays.stream(Command.values()).anyMatch(c -> c.value.equals(command));
    }
}
