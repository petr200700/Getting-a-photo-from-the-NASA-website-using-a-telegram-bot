import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class MyTelegramBot extends TelegramLongPollingBot {
    private final String BOT_NAME;
    private final String BOT_TOKEN;
    private final String URL = "https://api.nasa.gov/planetary/apod" +
            "?api_key=";
    private Object msg;

    public MyTelegramBot(String BOT_NAME, String BOT_TOKEN) throws TelegramApiException {
        this.BOT_NAME = BOT_NAME;
        this.BOT_TOKEN = BOT_TOKEN;

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String answer = update.getMessage().getText();
            String[] separatedAnswer = answer.split(" ");
            String action = separatedAnswer[0];

            switch (action) {
                case "/help":
                case "Помощь":
                    sendMessage("Я бот космоса. Смотри картинку дня.\n" +
                            "Для получения картинки дня введите /image или /start.\n" +
                            "Для получения картинки за определенный день введите /date ГГГГ-ММ-ДД.", chatId);
                    break;
                case "/start":
                case "Старт":
                case "/image":
                    String image = Utils.getUrl(URL);
                    sendMessage(image, chatId);
                    break;
                case "/date":
                    image = Utils.getUrl(URL + "&date=" + separatedAnswer[1]);
                    sendMessage(image, chatId);
                    break;
                case "Введите_Дату":
                    sendMessage(
                            "Для получения картинки за определенный день введите /date ГГГГ-ММ-ДД.", chatId);
                    break;
                default:
                    sendMessage("Я не знаю такой команды.", chatId);
                    break;
            }
        }
    }

    void sendMessage(String text, long chatId) {
        SendMessage msg = new SendMessage();

        msg.setText(text);
        msg.setChatId(chatId);

        //Клавиатура
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(); // разметка для клавиатуры
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        List<KeyboardRow> keyboard = new ArrayList<>(); // список рядов нашей клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow(); // создаем первый ряд
        keyboardFirstRow.add("Старт"); // добавляем кнопку с описанием
        keyboard.add(keyboardFirstRow); //добавляем наш первый ряд в список рядов
        KeyboardRow keyboardSecondRow = new KeyboardRow(); // создаем второй ряд
        keyboardSecondRow.add("Помощь"); // добавляем кнопку во втором ряду с описанием
        keyboard.add(keyboardSecondRow); //добавляем наш второй ряд в список рядов
        KeyboardRow keyboardThirdRow = new KeyboardRow();
        keyboardThirdRow.add("Введите_Дату"); // добавляем кнопку в третьем ряду с описанием
        keyboard.add(keyboardThirdRow);// добавляем третий ряд в список рядов
        replyKeyboardMarkup.setKeyboard(keyboard); // доавляем все ряды
//        msg.setReplyMarkup((ReplyKeyboard) keyboard); // привязываем клавиатуру к сообщению
        msg.setReplyMarkup(replyKeyboardMarkup);
        try {
            execute(msg);
        } catch (TelegramApiException ex) {
            System.out.println("Бот не отправил сообщение.");
        }
    }

    @Override
    public String getBotUsername() {
        // TODO
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        // TODO
        return BOT_TOKEN;
    }
}
