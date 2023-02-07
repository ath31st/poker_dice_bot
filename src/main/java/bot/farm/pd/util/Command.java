package bot.farm.pd.util;

public enum Command {
    START("!poker"),
    ROLL("!roll"),
    REROLL("!reroll"),
    PASS("!pass"),
    FINISH("!finish"),
    HELP("!help"),
    RULES("!rules");

    public final String value;

    Command(String value) {
        this.value = value;
    }
}
