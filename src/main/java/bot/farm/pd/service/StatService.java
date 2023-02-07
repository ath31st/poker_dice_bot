package bot.farm.pd.service;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatService {
    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        System.out.println(event.getAuthor().getName());
        System.out.println(event.getMember().getNickname());
        System.out.println(event.getAuthor().getId());
    }
}
