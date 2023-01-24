package org.example;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class GenerateQrCodeImpl implements GenerateQrCode {
private static GenerateQrCode generateQrCode = new GenerateQrCodeImpl();
public static GenerateQrCode getInstance(){
    return generateQrCode;
}
    @Override
    public void generateQRcode(String data, String path, String charset, Map map, int h, int w) throws IOException, WriterException {
        BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset),charset), BarcodeFormat.QR_CODE,w,h);
        MatrixToImageWriter.writeToFile(matrix,path.substring(path.lastIndexOf(".") +1),new File(path));
    }

    @Override
    public String readQRcode(String path, String charset, Map map) throws IOException, NotFoundException {
        BinaryBitmap binaryBitmap
                = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(
                        ImageIO.read(
                                new FileInputStream(path)))));

        Result result
                = new MultiFormatReader().decode(binaryBitmap);

        return result.getText();

    }
}
