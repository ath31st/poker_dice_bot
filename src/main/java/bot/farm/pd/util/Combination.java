package bot.farm.pd.util;

public enum Combination {
    SET("Сет"),
    SQUARE("Каре"),
    FULL_HOUSE("Фулл-хаус"),
    SMALL_STRAIGHT("Короткий стрит"),
    LARGE_STRAIGHT("Длинный стрит"),
    POKER("Покер"),
    CHANCE("Шанс");

    public final String value;

    Combination(String value) {
        this.value = value;
    }
}
