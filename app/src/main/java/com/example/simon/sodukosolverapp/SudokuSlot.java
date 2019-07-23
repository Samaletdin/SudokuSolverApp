package com.example.simon.sodukosolverapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.simon.sodukosolverapp.MainActivity;
import com.example.simon.sodukosolverapp.R;

@SuppressLint("ViewConstructor")
public class SudokuSlot extends AppCompatTextView {
    private String sValue;
    private final String[] values = getResources().getStringArray(R.array.inputs);
    private final int dim = 40;
    private int row;
    private int column;


    public SudokuSlot(final Context context, int row, int column){
        this(context, "", row, column);
    }

    @SuppressLint("ClickableViewAccessibility")
    public SudokuSlot(final Context context, final String value, final int row, final int column){
        super(context);
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
                    final Builder builder = new Builder(context);
                    builder.setTitle("Choose input");
                    builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            updateText(values[i]);
                            dialogInterface.dismiss();
                        }
                    });
                    final AlertDialog mDialog = builder.create();
                    mDialog.show();
                }
                return false;
            }
        });
    }


    public void setsValue(final String val){
        sValue = val;
        if(!val.isEmpty() && !val.equals("0")) {
            this.setText(val);
        } else {
            this.setText("");
        }
        //this.setBackgroundColor(Color.rgb((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255))); //For debugging  and fun
    }

    public int getSValue(){
        if(sValue.equals("")){
            return 0;
        }
        return Integer.valueOf(sValue);
    }

    private void updateText(final String string){
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


