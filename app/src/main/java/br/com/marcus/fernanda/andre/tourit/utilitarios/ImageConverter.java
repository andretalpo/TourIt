package br.com.marcus.fernanda.andre.tourit.utilitarios;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import java.io.ByteArrayOutputStream;

/**
 * Created by André on 23/09/2017.
 */

public class ImageConverter {

    public static byte[] convertBitmapToByte(Bitmap fotoBitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        fotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap convertByteToBitmap(byte[] fotoByte){
        byte[] arrayFoto = fotoByte;
        return BitmapFactory.decodeByteArray(arrayFoto, 0, arrayFoto.length);
    }
}
