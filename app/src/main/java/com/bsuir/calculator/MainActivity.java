package com.bsuir.calculator;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity{

    TextView numberDisplay, operationsDisplay;
    HorizontalScrollView scrollerDisplayNumber, scrollerDisplayOperations;
    Button button1, button2, button3, button4, button5, button6, button7, button8, button9, button0, buttonCE, buttonRoot, buttonLog, buttonExp, buttonSin, buttonCos, buttonBracketsOpen, buttonBracketsClose, buttonTan, buttonPI, buttonExponentation, buttonSum, buttonSubtraction, buttonMultiplication, buttonDivision, buttonEqual;
    String stringNumber, stringSpecial,lastToken="";
    Expression expression;
    String[] ops = {"+","-","/","*","^"};
    List<String> operators = Arrays.asList (ops);
    String funs[] = {"sin","cos","tan","ln","log","sqrt","exp"};
    List<String> functions = Arrays.asList (funs);
    ClipboardManager clipboard;
    ClipData clip;
    double value ;
    char bracketOpen='(';
    char bracketClose=')';
    private boolean calculationDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numberDisplay =  findViewById(R.id.displayNumber);
        operationsDisplay = findViewById(R.id.displayOperationNumber);
        scrollerDisplayNumber = findViewById(R.id.displayNumberScroller);
        scrollerDisplayOperations = findViewById(R.id.displayOperationNumberScroller);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 =  findViewById(R.id.button4);
        button5 =  findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);
        button8 = findViewById(R.id.button8);
        button9 = findViewById(R.id.button9);
        button0 = findViewById(R.id.button0);
        buttonCE =  findViewById(R.id.buttonCE);
        buttonRoot =  findViewById(R.id.buttonRoot);
        buttonSin = findViewById(R.id.buttonSin);
        buttonCos =  findViewById(R.id.buttonCos);
        buttonBracketsOpen = findViewById(R.id.buttonBracketsOpen);
        buttonBracketsClose = findViewById(R.id.buttonBracketsClose);
        buttonTan = findViewById(R.id.buttonTan);
        buttonPI = findViewById(R.id.buttonPI);
        buttonExponentation =  findViewById(R.id.buttonExponentation);
        buttonSum =  findViewById(R.id.buttonSum);
        buttonSubtraction =  findViewById(R.id.buttonSubtraction);
        buttonMultiplication = findViewById(R.id.buttonMultiplication);
        buttonDivision = findViewById(R.id.buttonDivision);
        buttonEqual =  findViewById(R.id.buttonEqaul);
        buttonLog = findViewById(R.id.buttonLog);
        buttonExp = findViewById(R.id.buttonExp);

        buttonCE.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                numberDisplay.setText(null);
                operationsDisplay.setText(null);
                calculationDone = false;
                lastToken="";
                buttonCE.setText("C/CE");
                return true;
            }
        });
        buttonCE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String odText =  operationsDisplay.getText().toString();
               odText = StringUtils.chop(odText);
                String ndText =  numberDisplay.getText().toString();
                ndText = StringUtils.chop(ndText);
                odText = StringUtils.chop(odText);
               numberDisplay.setText(ndText);
               operationsDisplay.setText(odText);
               lastToken = StringUtils.chop(odText);
            }
        });

        }


    public void appendElement(View v){
        Button buttonPressed = (Button) v;
        String el = buttonPressed.getText().toString();
        String odText = operationsDisplay.getText().toString();
        String ndText = numberDisplay.getText().toString();

        if(calculationDone) {
            calculationDone = false;
            if(ndText.equals("Invalid")){
                numberDisplay.setText(null);
                ndText="";
                operationsDisplay.setText(null);
                odText="";
                lastToken="";
            } else {
                lastToken=ndText.charAt(ndText.length()-1)+"";
                odText=ndText;
            }

        }


        if(el.equals("√")){
            el="sqrt";
        }
        if(isOperator(el)){

                numberDisplay.setText(null);
                if (isOperator(lastToken)) {
                    operationsDisplay.setText(odText.substring(0, odText.length() - 1) + el);//replace
                    lastToken=el;
                }
                else if(!lastToken.equals("")){
                    operationsDisplay.setText(odText + el);
                    lastToken=el;
                }




        }else if(el.equals(".") && lastToken.equals(el)){
            operationsDisplay.setText(odText.substring(0,odText.length()-1));
            numberDisplay.setText(ndText.substring(0,ndText.length()-1));
            lastToken=operationsDisplay.getText().charAt(odText.length()-2)+"";
            return;
        } else if(isFunction(el)){
            lastToken = el;
            numberDisplay.setText(numberDisplay.getText().toString()+el+"(");
            operationsDisplay.setText(operationsDisplay.getText().toString()+el+"(");
        }

        else {
            operationsDisplay.setText(odText+el);
            numberDisplay.setText(ndText+el);
            lastToken = el;
        }



    }

    public void onEqual(View v){
        numberDisplay.startAnimation((Animation) AnimationUtils.loadAnimation(this, R.anim.line_animation_out));

        Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.line_animation_in);
        slideUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        numberDisplay.startAnimation(slideUp);
        try {
            expression = new ExpressionBuilder(operationsDisplay.getText().toString()).build();
            DecimalFormatSymbols separators = new DecimalFormatSymbols(Locale.GERMAN);
            separators.setDecimalSeparator('.');
            separators.setGroupingSeparator(' ');
            value = expression.evaluate();
            numberDisplay.setText(new DecimalFormat("0.########", separators).format(value));
        } catch (Exception e){
            numberDisplay.setText("Invalid");
        }
        calculationDone = true;
        return ;

    }
    public void clickButtonPI(View v) {
        numberDisplay.setText(numberDisplay.getText() + "π");
        operationsDisplay.setText(operationsDisplay.getText() + "π");
    }



    public void clickNumberDisplay(View v) {

        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clip = ClipData.newPlainText("number", numberDisplay.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(MainActivity.this, "Copied result to clipboard", Toast.LENGTH_SHORT).show();
    }

    public void clickOperationsDisplay(View v) {

        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clip = ClipData.newPlainText("operations", operationsDisplay.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(MainActivity.this, "Copied operations to clipboard", Toast.LENGTH_SHORT).show();
    }


    public boolean isOperator(String s){

        return operators.contains(s);
    }

    public boolean isNumber(String s){
        try{
            Double.parseDouble(s);
            return true;
        } catch(Error e) {
            return false;
        }
    }

    public boolean isFunction(String s){
        if( s.endsWith("(") ){
            s=s.substring(s.length()-1);
        }
        return functions.contains(s);
    }


}
