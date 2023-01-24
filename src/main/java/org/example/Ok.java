package org.example;

import com.google.zxing.EncodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Ok {
    public static void main(String[] args) throws NotFoundException, IOException {
        Map<EncodeHintType, ErrorCorrectionLevel> map = new HashMap<EncodeHintType,ErrorCorrectionLevel>();
        map.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.L);
        String s = GenerateQrCodeImpl.getInstance().readQRcode("src\\main\\resources\\qrcode.png", "UTF-8", map);
        System.out.println(s);
    }
}
