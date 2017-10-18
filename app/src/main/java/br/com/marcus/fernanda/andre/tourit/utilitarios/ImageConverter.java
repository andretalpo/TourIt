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
        if(fotoBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            fotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        }
        return null;
    }

    public static Bitmap convertByteToBitmap(byte[] fotoByte){
        if(fotoByte.length > 0) {
            return BitmapFactory.decodeByteArray(fotoByte, 0, fotoByte.length);
        }
        return null;
    }
}
