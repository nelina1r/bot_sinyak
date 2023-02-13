package ru.dedov.botsinyak.service;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.dedov.botsinyak.config.BotConfig;
import ru.dedov.botsinyak.model.Answer;
import ru.dedov.botsinyak.model.User;
import ru.dedov.botsinyak.utils.MessagesHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class BotService extends TelegramLongPollingBot {

    @Autowired
    private UserService userService;

    @Autowired
    private AnswerService answerService;

    private final BotConfig config;

    public BotService(BotConfig config) {
        this.config = config;
        List<BotCommand> commandList = new ArrayList<>();
        commandList.add(new BotCommand("/start", "Начать работу"));
        commandList.add(new BotCommand("/survey", "Пройти опрос чтобы получить рекомендацию"));
        commandList.add(new BotCommand("/help", "Помощь"));
        try {
            this.execute(new SetMyCommands(commandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasCallbackQuery()) {
                doSurvey(update);
                return;
            }
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            if (!update.getMessage().hasText()) {
                sendMessage(chatId, MessagesHolder.INVALID_COMMAND);
                return;
            }
            if (update.hasMessage() && update.getMessage().hasText()) {
                switch (messageText) {
                    case "/start" ->
                            sendMessage(chatId, update.getMessage().getChat().getFirstName() + MessagesHolder.START_COMMAND_TEXT);

                    case "/survey" -> startSurvey(update);

                    case "/help" -> sendMessage(chatId, MessagesHolder.HELP_COMMAND_TEXT);

                    default -> sendMessage(chatId, MessagesHolder.INVALID_COMMAND);
                }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public void doSurvey(Update update) throws TelegramApiException {
        String userId = update.getCallbackQuery().getMessage().getChatId().toString();
        User user = userService.findUserById(Long.parseLong(userId));
        switch (userService.findUserById(Long.parseLong(userId)).getAnswers().size()) {
            case 0 -> {
                sendSurveyQuestionMessage(userId, MessagesHolder.QUESTIONS[0],
                        "Люблю алкоголь", "Отметить событие", "Хочу отдохнуть", "Меня уволили",
                        "0", "100", "500", "5000");
                saveUserAnswer(update, user);
            }
            case 1 -> {
                sendSurveyQuestionMessage(userId, MessagesHolder.QUESTIONS[1],
                        "Одинокий альфа", "Я и девушка", "Семейный круг", "С мужиками",
                        "5000", "100", "500", "0");
                saveUserAnswer(update, user);
            }
            case 2 -> {
                sendSurveyQuestionMessage(userId, MessagesHolder.QUESTIONS[2],
                        "Дома", "У друзей", "В бане", "Бар",
                        "5000", "100", "500", "0");
                saveUserAnswer(update, user);
            }
            case 3 -> {
                sendSurveyQuestionMessage(userId, MessagesHolder.QUESTIONS[3],
                        "Малосольный огурчик", "Раки", "Сырная нарезка", "Селедка",
                        "5000", "100", "500", "0");
                saveUserAnswer(update, user);
            }
            case 4 -> {
                sendSurveyQuestionMessage(userId, MessagesHolder.QUESTIONS[4],
                        "0-18", "19-22", "23-45", "46+",
                        "5000", "100", "500", "0");
                saveUserAnswer(update, user);
            }
            case 5 -> {
                sendSurveyQuestionMessage(userId, MessagesHolder.QUESTIONS[5],
                        "0", "до 1500", "1501-5000", "Я программист",
                        "5000", "100", "500", "0");
                saveUserAnswer(update, user);
            }
            default -> doResults(Long.parseLong(userId), update.getCallbackQuery().getMessage().getChatId());
        }
    }

    private void saveUserAnswer(Update update, User user) {
        Answer answer = new Answer();
        answer.setValue(Long.parseLong(update.getCallbackQuery().getData()));
        answerService.saveAnswer(answer);
        Set<Answer> answerSet = user.getAnswers();
        answerSet.add(answer);
        user.setAnswers(answerSet);
        userService.saveUser(user);
    }

    private void doResults(Long userId, Long chatId) throws TelegramApiException {
        Long result = userService.findUserById(userId).getAnswers()
                .stream()
                .mapToLong(Answer::getValue)
                .sum();
        sendMessage(chatId, MessagesHolder.RESULT_TEXT_MESSAGE + " " + result);
    }

    public void sendSurveyQuestionMessage(String chatId, String message,
                                          String buttonText1, String buttonText2, String buttonText3, String buttonText4,
                                          String buttonScore1, String buttonScore2, String buttonScore3, String buttonScore4) throws TelegramApiException {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText(buttonText1);
        inlineKeyboardButton1.setCallbackData(buttonScore1);
        inlineKeyboardButton2.setText(buttonText2);
        inlineKeyboardButton2.setCallbackData(buttonScore2);
        inlineKeyboardButton3.setText(buttonText3);
        inlineKeyboardButton3.setCallbackData(buttonScore3);
        inlineKeyboardButton4.setText(buttonText4);
        inlineKeyboardButton4.setCallbackData(buttonScore4);
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow3= new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow2.add(inlineKeyboardButton2);
        keyboardButtonsRow3.add(inlineKeyboardButton3);
        keyboardButtonsRow4.add(inlineKeyboardButton4);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
        rowList.add(keyboardButtonsRow4);
        inlineKeyboardMarkup.setKeyboard(rowList);
        execute(SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(message)
                .replyMarkup(inlineKeyboardMarkup)
                .build());
    }

    private void startSurvey(Update update) throws TelegramApiException {
        userService.createNewUserIfNotExists(update.getMessage().getChat().getId(), update.getMessage().getChat().getUserName());
        userService.deleteAllUserAnswers(update.getMessage().getChat().getId());
        execute(sendStartSurveyMessage(update.getMessage().getChatId().toString()));
    }

    public SendMessage sendStartSurveyMessage(String chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Принято!");
        inlineKeyboardButton1.setCallbackData("0");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(MessagesHolder.START_SURVEY_MESSAGE)
                .replyMarkup(inlineKeyboardMarkup).build();
    }

    private void sendMessage(Long chatId, String message) throws TelegramApiException {
        execute(new SendMessage(String.valueOf(chatId), message));
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

}
