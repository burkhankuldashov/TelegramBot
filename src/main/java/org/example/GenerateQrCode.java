package org.example;

import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.IOException;
import java.util.Map;

public interface GenerateQrCode {
     InputFile generateQRcode(String data, String path, String charset, Map map, int h, int w) throws IOException, WriterException;
String readQRcode(String path, String charset, Map map) throws IOException, NotFoundException;
}
