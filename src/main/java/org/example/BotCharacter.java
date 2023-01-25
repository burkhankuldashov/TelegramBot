package org.example;

import com.google.zxing.EncodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class BotCharacter extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        return null;
    }

    @Override
    public String getBotToken() {
        return null;
    }

    static List<User> users = new ArrayList<>();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            User user = getUser(chatId.toString());
            SendMessage sendMessage = new SendMessage();
            if (message.hasText()) {
                if (message.getText().equals("/start")) {
                    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                    replyKeyboardMarkup.setResizeKeyboard(true);
                    replyKeyboardMarkup.setOneTimeKeyboard(true);
                    replyKeyboardMarkup.setSelective(true);

                    List<KeyboardRow> rows = new ArrayList<>();
                    KeyboardRow row = new KeyboardRow();
                    row.add(new KeyboardButton("Menu"));
                    rows.add(row);
                    replyKeyboardMarkup.setKeyboard(rows);

                    sendMessage.setText("""
                            Hi!
                                                    
                            I can create  new QR Code!
                            and I can read other QR code
                                                    
                            Click Menu to keyboard
                            """);
                    sendMessage.setChatId(update.getMessage().getChatId());
                    sendMessage.setReplyMarkup(replyKeyboardMarkup);
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    user.setBotStates(BotStates.ENTERING);
                } else if (user.getBotStates().equals(BotStates.ENTERING)) {
                    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                    replyKeyboardMarkup.setOneTimeKeyboard(true);
                    replyKeyboardMarkup.setResizeKeyboard(true);
                    replyKeyboardMarkup.setOneTimeKeyboard(true);

                    List<KeyboardRow> rows = new ArrayList<>();
                    KeyboardRow keyboardRow = new KeyboardRow();
                    keyboardRow.add(new KeyboardButton("Read QR code"));
                    keyboardRow.add(new KeyboardButton("Create QR code"));

                    KeyboardRow keyboardButtons = new KeyboardRow();
                    keyboardButtons.add(new KeyboardButton("About bot"));

                    rows.add(keyboardRow);
                    rows.add(keyboardButtons);

                    replyKeyboardMarkup.setKeyboard(rows);

                    sendMessage.setText("""
                            What do you do?
                                                    
                            Create QR code or Read QR code ?
                                                    
                            """);
                    sendMessage.setChatId(update.getMessage().getChatId());
                    sendMessage.setReplyMarkup(replyKeyboardMarkup);


                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    user.setBotStates(BotStates.SEND_PHOTO_OR_TEXT);
                } else if (user.getBotStates().equals(BotStates.SEND_PHOTO_OR_TEXT) && update.getMessage().getText().equals("Read QR code")) {
                    sendMessage.setText("Send QR code photo");
                    sendMessage.setChatId(update.getMessage().getChatId());




                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    user.setBotStates(BotStates.SEND_PHOTO);
                } else if (user.getBotStates().equals(BotStates.SEND_PHOTO_OR_TEXT) && update.getMessage().getText().equals("Create QR code")) {
                    sendMessage.setText("Enter the word or sentence");
                    sendMessage.setChatId(update.getMessage().getChatId());

                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    user.setBotStates(BotStates.SEND_TEXT);
                } else if (user.getBotStates().equals(BotStates.SEND_PHOTO_OR_TEXT) && update.getMessage().getText().equals("About bot")) {
                    sendMessage.setText("""
                            Bot owner: Kuldashov Burkhan Bakhtiyarovich.
                            Phone number: +998932256060
                            email: burkhankuldashov01@gmail.com
                            Bot's goal: create QR code or Read QR code
                            
                            Thank you!
                            """);
                    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                    replyKeyboardMarkup.setOneTimeKeyboard(true);
                    replyKeyboardMarkup.setResizeKeyboard(true);
                    replyKeyboardMarkup.setSelective(true);

                    List<KeyboardRow> rows = new ArrayList<>();
                    KeyboardRow row = new KeyboardRow();
                    row.add(new KeyboardButton("Back"));
                    rows.add(row);
                    replyKeyboardMarkup.setKeyboard(rows);

                    sendMessage.setReplyMarkup(replyKeyboardMarkup);
                    sendMessage.setChatId(update.getMessage().getChatId());
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    user.setBotStates(BotStates.ENTERING);
                } else if (user.getBotStates().equals(BotStates.SEND_TEXT) && update.hasMessage()) {
                    String data = update.getMessage().getText();
                    String parth = "src\\main\\resources\\qrcode.png";
                    String charset = "UTF-8";
                    Map<EncodeHintType, ErrorCorrectionLevel> map = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
                    map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
                    SendPhoto sendPhoto = new SendPhoto();
                    try {
                        GenerateQrCodeImpl.getInstance().generateQRcode(data, parth, charset, map, 200, 200);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (WriterException e) {
                        throw new RuntimeException(e);
                    }
                    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                    replyKeyboardMarkup.setOneTimeKeyboard(true);
                    replyKeyboardMarkup.setResizeKeyboard(true);
                    replyKeyboardMarkup.setSelective(true);

                    List<KeyboardRow> rows = new ArrayList<>();
                    KeyboardRow row = new KeyboardRow();
                    row.add(new KeyboardButton("Back"));
                    rows.add(row);
                    replyKeyboardMarkup.setKeyboard(rows);

                    sendPhoto.setChatId(update.getMessage().getChatId());
                    sendPhoto.setPhoto(new InputFile(new File("src\\main\\resources\\qrcode.png"), "photo"));
                    sendPhoto.setReplyMarkup(replyKeyboardMarkup);
                    try {
                        execute(sendPhoto);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                    user.setBotStates(BotStates.ENTERING);
                }
            } else if (update.getMessage().hasPhoto()) {
                if (user.getBotStates().equals(BotStates.SEND_PHOTO)) {
                    List<PhotoSize> photo = update.getMessage().getPhoto();
                    if (photo != null && !photo.isEmpty()) {
                        photo.sort(Comparator.comparing(PhotoSize::getFileSize).reversed());
                        PhotoSize photoSize = photo.get(0);
                        GetFile getFile = new GetFile(photoSize.getFileId());
                        try {
                            org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
                            java.io.File file1 = new java.io.File("src/main/resources/Text.png");
                            downloadFile(file, file1);
                            String charset = "UTF-8";
                            Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
                            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
                            sendMessage.setChatId(update.getMessage().getChatId());
                            sendMessage.setText(GenerateQrCodeImpl.getInstance().readQRcode(file1.getPath(), charset, hintMap));
                            user.setBotStates(BotStates.ENTERING);

                            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                            replyKeyboardMarkup.setOneTimeKeyboard(true);
                            replyKeyboardMarkup.setResizeKeyboard(true);
                            replyKeyboardMarkup.setSelective(true);

                            List<KeyboardRow> rows = new ArrayList<>();
                            KeyboardRow row = new KeyboardRow();
                            row.add(new KeyboardButton("Back"));
                            rows.add(row);
                            replyKeyboardMarkup.setKeyboard(rows);
                            sendMessage.setReplyMarkup(replyKeyboardMarkup);

                            execute(sendMessage);
                        } catch (TelegramApiException | IOException | NotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        user.setBotStates(BotStates.ENTERING);
                    }
                }


            }

        }

    }

    public static User getUser(String chatid) {
        for (User user : users) {
            if (user.getId().equals(chatid)) {
                return user;
            }
        }
        User user = new User();
        user.setId(chatid);
        users.add(user);
        return user;
    }
}
