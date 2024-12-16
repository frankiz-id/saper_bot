package org.example;

public final class Constant {
    private Constant() {};

    public static final String ModeSelection = "Выбор режима:";
    public static final String Training = "Обучение";
    public static final String Easy = "Легкий";
    public static final String Standart = "Стандартный";
    public static final String Rules = "Цель игры\n" +
            "Открыть все клетки на поле, не наткнувшись на мины. Если откроете мину - проиграете.\n" +
            "\n" +
            "Игровое поле\n" +
            "Поле состоит из клеток, которые могут быть:\n" +
            "  - Закрытыми\n" +
            "  - Открытым\n" +
            "  - С флагом\n" +
            "  - С миной\n" +
            "\n" +
            "Когда вы открываете клетку, в ней может быть:\n" +
            "- пустая клетка: Это означает, что рядом с этой клеткой нет мин.\n" +
            "- 1, 2, 3...: Это число показывает, сколько мин находится в соседних клетках.\n" +
            "\n" +
            "### Основные действия\n" +
            "- Копать ⛏️: Кликните на клетку, чтобы открыть её.\n" +
            "- Установить \uD83D\uDEA9: Если вы подозреваете, что в клетке есть мина, вы можете пометить её флажком.\n" +
            "\n" +
            "### Правила игры\n" +
            "1. Чтобы начать игру, нажмите на клетку, с которой хотите начать.\n" +
            "2. Используйте числа, чтобы выяснить, где могут быть мины.\n" +
            "3. Если вы видите \"2\", это значит, что рядом с этой клеткой 2 мины.\n" +
            "4. Если вы уверены, что в клетке мина, установите флаг.\n" +
            "5. Если открыть клетку, вокруг которой число флажков равно числу на клетке, то откроются все соседние клетки, кроме флажков.\n" +
            "6. Чтобы выиграть откройте все клетки без мин.\n" +
            "\n" +
            "### Советы\n" +
            "- Думайте, прежде чем действовать.\n" +
            "- Используйте флаги, чтобы не забыть, где мины.\n" +
            "- Практикуйтесь, ибо нефиг!\n" +
            "\n" +
            "Предлагаем сыграть в упрощенный режим.";
    public static final String CountMines = "Количество оставшихся мин: ";

    public static final String ModeFlag = "Установить \uD83D\uDEA9";
    public static final String ModeDig = "Копать ⛏️";
}
