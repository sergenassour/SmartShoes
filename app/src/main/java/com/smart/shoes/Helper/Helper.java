package com.smart.shoes.Helper;

import  android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import com.smart.shoes.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class Helper {
    Context context;

    public Helper(Context context) {
        this.context = context;
    }

    public String currentDate(){
          return  new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
       }
    public String currentTime(){
        return  new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
    }
    public Dialog openNetLoaderDialog() {
       Dialog dialogP=new Dialog(context);
        dialogP.setContentView(R.layout.dialog_loading);
        dialogP.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogP.setCancelable(false);
        dialogP.show();
        return dialogP;
    }


}
