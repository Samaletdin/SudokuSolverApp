package com.example.simon.sodukosolverapp;


import android.annotation.SuppressLint;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import android.widget.Toast;
import com.example.simon.sodukosolverapp.backend.SodukoSolver;
import com.example.simon.sodukosolverapp.R.id;
import com.example.simon.sodukosolverapp.R.layout;

public class MainActivity extends AppCompatActivity {

    public static int[][] mboard = new int[9][9];
    public TableLayout boardSlots;
    public Button solverButton;
    public Button clearButton;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(layout.activity_main);
        boardSlots = findViewById(id.SlotHolder);
        solverButton = findViewById(id.button1);
        clearButton = findViewById(id.buttonClear);
        generateLayout(savedInstanceState);

        solverButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                solveBoard();
                return false;
            }
        });

       clearButton.setOnTouchListener(new OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               clearBoard();
               return false;
           }
       });

        if(savedInstanceState!=null) {
            System.out.println("SAVED STATE NOT NULL");
            if (savedInstanceState.containsKey("mBoard1")) {
                System.out.println("SAVED STATE CONTAINS KEY!");
                mboard[0] = savedInstanceState.getIntArray("mBoard1");
                mboard[1] = savedInstanceState.getIntArray("mBoard2");
                mboard[2] = savedInstanceState.getIntArray("mBoard3");
                mboard[3] = savedInstanceState.getIntArray("mBoard4");
                mboard[4] = savedInstanceState.getIntArray("mBoard5");
                mboard[5] = savedInstanceState.getIntArray("mBoard6");
                mboard[6] = savedInstanceState.getIntArray("mBoard7");
                mboard[7] = savedInstanceState.getIntArray("mBoard8");
                mboard[8] = savedInstanceState.getIntArray("mBoard9");
                updateFromMboard();
            }
        }
    }

    private void generateLayout(final Bundle savedInstanceState) {
        boardSlots.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                boardSlots.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                generateBoxes(boardSlots.getWidth(), boardSlots.getHeight());
                if(savedInstanceState!=null){
                    onRestoreInstanceState(savedInstanceState);
                }else{
                    mboard = new int[9][9];
                }
            }
        });
    }


    private void solveBoard(){
        if(moreThan17input()) {
            SodukoSolver sodukoSolver = new SodukoSolver(mboard);
            mboard = sodukoSolver.execute(mboard);
            updateFromMboard();
            showStatus(sodukoSolver);
        } else {
            Toast.makeText(this, "Need at least 17 inputs to solve", Toast.LENGTH_SHORT).show();
        }
    }

    private void showStatus(final SodukoSolver sodukoSolver) {
        switch (sodukoSolver.getStatus()){
            case COMPLETED:
                Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
                break;
            case ERROR:
                Toast.makeText(this, "There is no solution!", Toast.LENGTH_SHORT).show();
                break;
            case MULTIPLE_ANSWERS:
                Toast.makeText(this, "Non unique answer!", Toast.LENGTH_SHORT).show();
                break;
            default:
                throw new RuntimeException("Non handled SudokoSolverStatus for the toast: " + sodukoSolver.getStatus());
        }
    }

    private boolean moreThan17input(){
        int count = 0;
        for(int[] row : mboard){
            for(int i : row){
                if (i!=0){
                    count++;
                }
            }
        }
        if(count >= 17)
            return true;
        return false;
    }

    private void clearBoard(){
        mboard = new int[9][9];
        updateFromMboard();
    }

    @Override
    protected void onResume(){
        Log.d("tag", "Loading matrix with slots onResume");
        System.out.println("ONRESUME");
        super.onResume();

    }

    @Override
    public void onSaveInstanceState(Bundle bundle){
        updateMboard();
        bundle.putIntArray("mBoard1", getDeepCopy(0));
        bundle.putIntArray("mBoard2", getDeepCopy(1));
        bundle.putIntArray("mBoard3", getDeepCopy(2));
        bundle.putIntArray("mBoard4", getDeepCopy(3));
        bundle.putIntArray("mBoard5", getDeepCopy(4));
        bundle.putIntArray("mBoard6", getDeepCopy(5));
        bundle.putIntArray("mBoard7", getDeepCopy(6));
        bundle.putIntArray("mBoard8", getDeepCopy(7));
        bundle.putIntArray("mBoard9", getDeepCopy(8));
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle){
        System.out.println("INSIDE ON RESTORE INSTANCE STATE!");
        super.onRestoreInstanceState(bundle);
        mboard = new int[9][9];
        mboard[0] = bundle.getIntArray("mBoard1");
        mboard[1] = bundle.getIntArray("mBoard2");
        mboard[2] = bundle.getIntArray("mBoard3");
        mboard[3] = bundle.getIntArray("mBoard4");
        mboard[4] = bundle.getIntArray("mBoard5");
        mboard[5] = bundle.getIntArray("mBoard6");
        mboard[6] = bundle.getIntArray("mBoard7");
        mboard[7] = bundle.getIntArray("mBoard8");
        mboard[8] = bundle.getIntArray("mBoard9");
        updateFromMboard();
    }

    private int[] getDeepCopy(int index){
        int[] temp = new int[9];
        int count = 0;
        for(int i : mboard[index]){
            temp[count] = i;
            count++;
        }
        return temp;
    }

    public void generateBoxes(int width, int height){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        for(int i = 0, j = boardSlots.getChildCount(); i < j; i++) {
            View view = boardSlots.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                for(int x = 0; x < 9; x++){
                    row.addView(new SudokuSlot(this, i, x), width/9, height/9);
                }
            }
        }

    }

    private void updateFromMboard(){
        for(int i = 0, x = boardSlots.getChildCount(); i < x; i++) {
            View view = boardSlots.getChildAt(i);
            if (view instanceof TableRow) {
                final TableRow row = (TableRow) view;
                for (int j = 0; j < 9; j++) {
                    final View slot = row.getChildAt(j);
                    if(slot instanceof SudokuSlot) {
                         ((SudokuSlot) slot).setsValue(String.valueOf(mboard[i][j]));
                    }
                }
            }
        }
    }

    private void updateMboard(){
        for(int i = 0, x = boardSlots.getChildCount(); i < x; i++) {
            View view = boardSlots.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                for (int j = 0; j < 9; j++) {
                    View slot = row.getChildAt(j);
                    if(slot instanceof SudokuSlot) {
                        mboard[i][j] = ((SudokuSlot) slot).getSValue();
                    }
                }
            }
        }
    }

    /**@Override
    public boolean onTouch(View v, MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // do something here when the element is clicked
            ScreenManager.setCurrent(new YourNewPage());
        }


        return false;
    }*/
}
