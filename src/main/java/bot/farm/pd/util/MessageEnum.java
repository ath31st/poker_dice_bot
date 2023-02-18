package bot.farm.pd.util;

public enum MessageEnum {
    TABLE_BUSY("Извините, игровой стол сейчас занят"),
    START_ROUND("%s начинает новый раунд покера с костями!"),
    ACTIVITY_FOR_GAME("за игрой"),
    ACTIVITY_CLEANING_TABLE("уборку игрового стола"),
    FINISH_ROUND("%s досрочно завершает раунд, результаты будут аннулированы"),
    TIME_EXPIRED("Время раунда подошло к концу");

    public final String value;

    MessageEnum (String value) {
        this.value = value;
    }
}
