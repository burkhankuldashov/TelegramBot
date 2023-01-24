package org.example;

import com.google.zxing.EncodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import com.google.zxing.pdf417.decoder.ec.ErrorCorrection;
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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class BotCharacter extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        return "nonamekhan_bot";
    }

    @Override
    public String getBotToken() {
        return "5821670690:AAFL3PsE38nVQ-5aA4-QtLqwUOPQ0SjmzxA";
    }

    List<User> users = new ArrayList<>();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long id = message.getChatId();
            SendMessage sendMessage = new SendMessage();
            User user = getUser(id.toString());
            if (message.getText().equals("/start") || (message.getText().equals("Back") && user.getBotStates().equals(BotStates.ENTERING))) {
                sendMessage.setChatId(update.getMessage().getChatId());
                sendMessage.setText("""
                        Assalomu alaykum 
                        Botdagi kamchiliklar:
                        1.Back tugmasi ishlamaydi sababi ogikasi yozilmagan
                        2.read qr code qilsa javob qaytmaydi.
                        """);
                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                replyKeyboardMarkup.setResizeKeyboard(true);
                replyKeyboardMarkup.setOneTimeKeyboard(true);
                replyKeyboardMarkup.setSelective(true);

                List<KeyboardRow> rows = new ArrayList<>();
                KeyboardRow keyboardRow = new KeyboardRow();
                keyboardRow.add(new KeyboardButton("Read QR code"));
                keyboardRow.add(new KeyboardButton("Create QR code"));
                KeyboardRow keyboardButtons = new KeyboardRow();
                rows.add(keyboardRow);
                replyKeyboardMarkup.setKeyboard(rows);
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                user.setBotStates(BotStates.ENTERING);
            } else if (update.getMessage().getText().equals("Read QR code") && user.getBotStates().equals(BotStates.ENTERING)) {
                sendMessage.setText("Send QR code photo");
                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                List<KeyboardRow> rows = new ArrayList<>();
                KeyboardRow keyboardRow = new KeyboardRow();
                keyboardRow.add(new KeyboardButton("Back"));
                rows.add(keyboardRow);
                replyKeyboardMarkup.setOneTimeKeyboard(true);
                replyKeyboardMarkup.setSelective(true);
                replyKeyboardMarkup.setResizeKeyboard(true);
                replyKeyboardMarkup.setKeyboard(rows);
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
                sendMessage.setChatId(update.getMessage().getChatId());
                try {

                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                user.setBotStates(BotStates.SEND_PHOTO);
                } else if (update.getMessage().getText().equals("Create QR code") && user.getBotStates().equals(BotStates.ENTERING)) {
                    sendMessage.setText("Enter the word or sentence");
                    sendMessage.setChatId(update.getMessage().getChatId());
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    user.setBotStates(BotStates.SEND_TEXT);
                } else if (update.getMessage().hasText() && user.getBotStates().equals(BotStates.SEND_TEXT)) {
                    String text = update.getMessage().getText();
                    String path = "src\\main\\resources\\qrcode.png";
                    String charset = "UTF-8";
                    Map<EncodeHintType, ErrorCorrectionLevel> map = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
                    map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
                    try {
                        GenerateQrCodeImpl.getInstance().generateQRcode(text, path, charset, map, 200, 200);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (WriterException e) {
                        throw new RuntimeException(e);
                    }
                    SendPhoto sendPhoto = new SendPhoto();
                    sendPhoto.setPhoto(new InputFile(new File("src\\main\\resources\\qrcode.png"), "photo"));
                    sendPhoto.setChatId(update.getMessage().getChatId());
                    try {
                        execute(sendPhoto);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }else if(update.getMessage().getText().equals("Send QR code photo") && user.getBotStates().equals(BotStates.ENTERING)){
                sendMessage.setText("Send a photo");
                sendMessage.setChatId(update.getMessage().getChatId());
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                user.setBotStates(BotStates.SEND_PHOTO);
            } else if (user.getBotStates().equals(BotStates.SEND_PHOTO)) {
                List<PhotoSize> photo = update.getMessage().getPhoto();
                if(photo!=null && !photo.isEmpty()){
                    photo.sort(Comparator.comparing(PhotoSize::getFileId));
                    String fileId = photo.get(0).getFileId();
                    String fileUniqueId = photo.get(0).getFileUniqueId();
                    GetFile getFile = new GetFile();
                    getFile.setFileId(fileId);
                    UUID uuid = UUID.randomUUID();
                    try {
                        org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
                        File file1 = downloadFile(file, new File("src\\main\\resources\\" + uuid + file.getFilePath().split("\\.")[1]));
                        String path = file1.getPath();
                        String charset = "UTF-8";
                        Map<EncodeHintType, ErrorCorrectionLevel> map = new HashMap<EncodeHintType,ErrorCorrectionLevel>();
                        map.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.L);
                        String s = GenerateQrCodeImpl.getInstance().readQRcode(path, charset, map);
sendMessage.setText(s);
                        sendMessage.setChatId(update.getMessage().getChatId());
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            }

            }
        }




    private User getUser(String chatId) {
        for (User user : users) {
            if (user.getId().equals(chatId)) {
                return user;
            }
        }
        User user1 = new User();
        user1.setId(chatId);
        users.add(user1);
        return user1;
    }
}
