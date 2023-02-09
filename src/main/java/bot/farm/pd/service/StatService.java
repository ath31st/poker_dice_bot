package bot.farm.pd.service;

import bot.farm.pd.entity.Result;
import bot.farm.pd.util.Help;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatService {
    private final MessageService messageService;
    private final PlayerService playerService;


    public void printHelpMessage(Message message) {
        messageService.sendMessage(message.getChannel(), Help.HELP.value);
    }

    public void saveRoundResult(Long idChannel, Long idWinner) {
        Result result = new Result();
        result.setPlayer(playerService.getPlayerById(idWinner).get());
        result.setIdChannel(idChannel);
    }
}
