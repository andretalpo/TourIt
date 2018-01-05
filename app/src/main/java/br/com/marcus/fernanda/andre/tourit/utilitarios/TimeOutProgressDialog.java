package br.com.marcus.fernanda.andre.tourit.utilitarios;

import android.app.Dialog;
import android.os.Handler;

/**
 * Created by André on 05/01/2018.
 */

public class TimeOutProgressDialog {

    public static void timerDelayRemoveDialog(long time, final Dialog d){
        if(d != null){
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    d.dismiss();
                }
            }, time);
        }
    }

}
