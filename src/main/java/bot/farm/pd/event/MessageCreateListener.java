package bot.farm.pd.event;

import discord4j.core.event.domain.message.MessageCreateEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MessageCreateListener extends MessageListener implements EventListener<MessageCreateEvent> {

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        //       System.out.println(eventMessage.getAuthor().get().getUsername());
//        System.out.println(eventMessage.getAuthorAsMember().block().getDisplayName());
//        System.out.println(eventMessage.getAuthor().get().getId().asLong());

        return processCommand(event.getMessage());
    }
}