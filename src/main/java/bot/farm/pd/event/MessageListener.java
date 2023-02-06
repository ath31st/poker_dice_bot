package bot.farm.pd.event;

import bot.farm.pd.util.Command;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

import java.util.Arrays;

public abstract class MessageListener {

    public Mono<Void> processCommand(Message eventMessage) {

        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(message -> checkOccurrence(message.getContent()))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage(" !! "))
                .then();
    }

    private boolean checkOccurrence(String command) {
        if (!command.startsWith("/") || command.length() > 200) return false;

//        Pattern pattern = null;
//        if (command.startsWith(START.value)) {
//            pattern = Pattern.compile("^" + START.value + " (<@[0-9]{18}>){2,}$");
//        } else if (command.startsWith(REROLL.value)) {
//            pattern = Pattern.compile("^" + REROLL.value + "( [0-9]){1,5}$");
//        }
//        if (pattern != null) {
//            Matcher matcher = pattern.matcher(command);
//            if (matcher.matches()) return true;
//        }

        return Arrays.stream(Command.values()).anyMatch(c -> c.value.startsWith(command));
    }

    private String executeCommand(Message message) {
        String command = message.getContent();

        System.out.println(message.getContent());
        return message.getAuthorAsMember().block().getDisplayName() + " !";
    }
}
