package com.example.simon.sodukosolverapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.widget.Spinner;

public class InputAlertDialogue extends Activity {

    private String[] inputs = new String[]{"1","2","3","4","5","6","7","8","9","Remove"};
    private Spinner spinner;

    public InputAlertDialogue(Context context){
        super();
        spinner = new Spinner(context);
    }
}
