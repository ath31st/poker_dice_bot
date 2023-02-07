package bot.farm.pd.service;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    public void sendMessage(MessageChannel channel, String message) {
        channel.sendMessage(message).queue();
    }
}
