package bot.farm.pd.util;

public enum Command {
    START("/start"),
    ROLL("/roll"),
    REROLL("/reroll"),
    PASS("/pass"),
    FINISH("/finish");

    public final String value;

    Command(String value) {
        this.value = value;
    }
}
