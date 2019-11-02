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

    private TextView numberDisplay, operationsDisplay;
    private String lastToken = "";
    private static final List<String> operators = Arrays.asList("+", "-", "/", "*", "^");
    private static final List<String> functions = Arrays.asList("sin", "cos", "tan", "ln", "log", "sqrt", "exp");
    private ClipboardManager clipboard;
    private ClipData clip;
    private boolean calculationDone = false;
    private static final String ERROR = "Error";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        numberDisplay = findViewById(R.id.displayNumber);
        operationsDisplay = findViewById(R.id.displayOperationNumber);
        if (savedInstanceState != null) {
            numberDisplay.setText(savedInstanceState.getString("numbers"));
            operationsDisplay.setText(savedInstanceState.getString("operations"));
            lastToken = savedInstanceState.getString("lastToken");
        }
        Button buttonCE = findViewById(R.id.buttonCE);
        buttonCE.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                clearDisplays();
                calculationDone = false;
                lastToken = "";
                return true;
            }
        });
        buttonCE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String odText = operationsDisplay.getText().toString();
                String ndText = numberDisplay.getText().toString();
                odText = StringUtils.chop(odText);
                ndText = StringUtils.chop(ndText);
                numberDisplay.setText(ndText);
                operationsDisplay.setText(odText);
                lastToken = StringUtils.chop(odText);
            }
        });
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        {
            savedInstanceState.putString("operations", operationsDisplay.getText().toString());
            savedInstanceState.putString("numbers", numberDisplay.getText().toString());
            savedInstanceState.putString("lastToken", lastToken);
        }
    }

    public void appendElement(View v) {
        Button buttonPressed = (Button) v;
        String operator = buttonPressed.getText().toString();
        String operations = operationsDisplay.getText().toString();
        String numbers = numberDisplay.getText().toString();

        if (calculationDone) {
            calculationDone = false;
            if (ERROR.equals(numbers)) {
                clearDisplays();
                numbers = "";
                operations = "";
                lastToken = "";
            } else {
                lastToken = String.valueOf(numbers.charAt(numbers.length() - 1));
                operations = numbers;
            }
        }
        if ("√".equals(operator)) {
            operator = "sqrt";
        }
        if (isOperator(operator)) {
            numberDisplay.setText(null);
            if (isOperator(lastToken)) {
                operationsDisplay.setText(operations.substring(0, operations.length() - 1) + operator);
                lastToken = operator;
            } else if (!"".equals(lastToken)) {
                operationsDisplay.setText(operations + operator);
                lastToken = operator;
            }
        } else if (".".equals(operator) && lastToken.equals(operator)) {
            operationsDisplay.setText(operations.substring(0, operations.length() - 1));
            numberDisplay.setText(numbers.substring(0, numbers.length() - 1));
            lastToken = String.valueOf(operationsDisplay.getText().charAt(operations.length() - 2));
        } else if (isFunction(operator)) {
            lastToken = operator;
            numberDisplay.append(operator + "(");
            operationsDisplay.append(operator + "(");
        } else {
            operationsDisplay.setText(operations + operator);
            numberDisplay.setText(numbers + operator);
            lastToken = operator;
        }
    }

    public void calculate(View v) {
        numberDisplay.startAnimation((Animation) AnimationUtils
                .loadAnimation(this, R.anim.line_animation_out));
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
            Expression expression = new ExpressionBuilder(operationsDisplay.getText().toString()).build();
            DecimalFormatSymbols separators = new DecimalFormatSymbols(Locale.GERMAN);
            separators.setDecimalSeparator('.');
            separators.setGroupingSeparator(' ');
            double value = expression.evaluate();
            numberDisplay.setText(new DecimalFormat("0.########", separators).format(value));
        } catch (Exception e) {
            numberDisplay.setText(ERROR);
        }
        calculationDone = true;
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

    private void clearDisplays() {
        numberDisplay.setText("");
        operationsDisplay.setText("");
    }
}
