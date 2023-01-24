package org.example;

import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;

import java.io.IOException;
import java.util.Map;

public interface GenerateQrCode {
     void generateQRcode(String data, String path, String charset, Map map, int h, int w) throws IOException, WriterException;
String readQRcode(String path, String charset, Map map) throws IOException, NotFoundException;
}
