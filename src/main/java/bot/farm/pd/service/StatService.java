package bot.farm.pd.service;

import bot.farm.pd.entity.Result;
import bot.farm.pd.repository.ResultRepository;
import bot.farm.pd.util.Help;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StatService {
    private final ResultRepository resultRepository;
    private final MessageService messageService;
    private final PlayerService playerService;


    public void printHelpMessage(Message message) {
        messageService.sendMessage(message.getChannel(), Help.HELP.value);
    }

    @Transactional
    public void saveRoundResult(Long idChannel, Long idWinner) {
        Result result = Result.builder()
                .idChannel(idChannel)
                .player(playerService.getPlayerById(idWinner).get())
                .roundTime(LocalDateTime.now())
                .build();

        resultRepository.save(result);
    }
}
