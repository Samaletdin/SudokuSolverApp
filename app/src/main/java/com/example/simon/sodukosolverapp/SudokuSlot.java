package com.example.simon.sodukosolverapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

public class SudokuSlot extends android.support.v7.widget.AppCompatTextView {
    private String sValue;
    private final String[] values = getResources().getStringArray(R.array.inputs);
    private final int dim = 40;
    private int row;
    private int column;


    public SudokuSlot(final Context context, int row, int column){
        this(context, "", row, column);
    }

    public SudokuSlot(final Context context, String value, int row, int column){
        super(context);
        System.out.println("SUDOKUSLOT MADE");
        this.setHeight(dim);
        this.setWidth(dim);
        this.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        this.setTextSize(24);
        this.setFontFeatureSettings("@font/noto_sans");
        this.setText(value);
        this.sValue = value;
        this.row = row;
        this.column = column;


        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Log.d("SUDOKUSLOT", "PRESSED A SLOT!");
                    System.out.println("PRESSED A SLOT!");
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Choose input");
                    builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            updateText(values[i]);
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog mDialog = builder.create();
                    mDialog.show();
                }
                return false;
            }
        });
    }


    public void setsValue(String val){
        System.out.println(val);
        sValue = val;
        if(val.equals("") || val.equals("0")){
            //DoNothing
        }else{
            this.setText(val);
        }
        //this.setBackgroundColor(Color.rgb((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255))); //For debugging  and fun
    }

    public int getSValue(){
        if(sValue.equals("")){
            return 0;
        }
        return Integer.valueOf(sValue);
    }

    private void updateText(String string){
        System.out.println(string);
        if(string.equals("Remove")){
            this.setText("");
            sValue = "0";
            MainActivity.mboard[this.row][this.column] = 0;
        }else{
            this.setText(string);
            sValue = string;
            MainActivity.mboard[this.row][this.column] = Integer.parseInt(string);
        }
    }


}


