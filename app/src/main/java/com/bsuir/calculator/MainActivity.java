package com.bsuir.calculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity {

    TextView numberDisplay, operationsDisplay;
    Button buttonCE;
    String lastToken = "";
    Expression expression;
    String[] ops = {"+", "-", "/", "*", "^"};
    List<String> operators = Arrays.asList(ops);
    String funs[] = {"sin", "cos", "tan", "ln", "log", "sqrt", "exp"};
    List<String> functions = Arrays.asList(funs);
    ClipboardManager clipboard;
    ClipData clip;
    double value;
    private boolean calculationDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numberDisplay = findViewById(R.id.displayNumber);
        operationsDisplay = findViewById(R.id.displayOperationNumber);

        buttonCE = findViewById(R.id.buttonCE);
        buttonCE.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                numberDisplay.setText(null);
                operationsDisplay.setText(null);
                calculationDone = false;
                lastToken = "";
                buttonCE.setText("C/CE");
                return true;
            }
        });
        buttonCE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String odText = operationsDisplay.getText().toString();
                odText = StringUtils.chop(odText);
                String ndText = numberDisplay.getText().toString();
                ndText = StringUtils.chop(ndText);
                odText = StringUtils.chop(odText);
                numberDisplay.setText(ndText);
                operationsDisplay.setText(odText);
                lastToken = StringUtils.chop(odText);
            }
        });

    }

    public void appendElement(View v) {
        Button buttonPressed = (Button) v;
        String el = buttonPressed.getText().toString();
        String odText = operationsDisplay.getText().toString();
        String ndText = numberDisplay.getText().toString();

        if (calculationDone) {
            calculationDone = false;
            if ("Invalid".equals(ndText)) {
                numberDisplay.setText(null);
                ndText = "";
                operationsDisplay.setText(null);
                odText = "";
                lastToken = "";
            } else {
                lastToken = ndText.charAt(ndText.length() - 1) + "";
                odText = ndText;
            }
        }
        if ("√".equals(el)) {
            el = "sqrt";
        }
        if (isOperator(el)) {
            numberDisplay.setText(null);
            if (isOperator(lastToken)) {
                operationsDisplay.setText(odText.substring(0, odText.length() - 1) + el);//replace
                lastToken = el;
            } else if (!"".equals(lastToken)) {
                operationsDisplay.setText(odText + el);
                lastToken = el;
            }
        } else if (".".equals(el) && lastToken.equals(el)) {
            operationsDisplay.setText(odText.substring(0, odText.length() - 1));
            numberDisplay.setText(ndText.substring(0, ndText.length() - 1));
            lastToken = operationsDisplay.getText().charAt(odText.length() - 2) + "";
            return;
        } else if (isFunction(el)) {
            lastToken = el;
            numberDisplay.setText(numberDisplay.getText().toString() + el + "(");
            operationsDisplay.setText(operationsDisplay.getText().toString() + el + "(");
        } else {
            operationsDisplay.setText(odText + el);
            numberDisplay.setText(ndText + el);
            lastToken = el;
        }
    }

    public void calculate(View v) {
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
        } catch (Exception e) {
            numberDisplay.setText("Invalid");
        }
        calculationDone = true;
        return;
    }

    public void appendPI(View v) {
        numberDisplay.append("π");
        operationsDisplay.append("π");
    }

    public void numberDisplayClickHandler(View v) {
        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clip = ClipData.newPlainText("number", numberDisplay.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(MainActivity.this, "Copied result to clipboard", Toast.LENGTH_SHORT).show();
    }

    public void operationsDisplayClickHandler(View v) {
        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clip = ClipData.newPlainText("operations", operationsDisplay.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(MainActivity.this, "Copied operations to clipboard", Toast.LENGTH_SHORT).show();
    }

    public boolean isOperator(String s) {
        return operators.contains(s);
    }

    public boolean isFunction(String s) {
        if (s.endsWith("(")) {
            s = s.substring(s.length() - 1);
        }
        return functions.contains(s);
    }
}