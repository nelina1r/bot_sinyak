package ru.dedov.botsinyak.utils;

public class MessagesHolder {

    public static final String START_COMMAND_TEXT = ", привет! С моей помощью ты можешь пройти небольшой опрос и получить рекомендацию.";

    public static final String HELP_COMMAND_TEXT = "Отправь мне команду /survey, или выбери соответствующий пункт из меню, чтобы начать опрос.";

    public static final String INVALID_COMMAND = "Я не понимаю ничего, кроме команд из меню :)";

    public static final String START_SURVEY_MESSAGE = "Перед прохождением опроса убедитесь что вы не беременны";

    public static final String[] QUESTIONS = {
            "Планы на вечер",
            "Компания собутыльников",
            "Место",
            "Закуски",
            "Возраст",
            "Количество денег"
    };

    public static final String RESULT_TEXT_MESSAGE = "Ваш результат - ";
}
