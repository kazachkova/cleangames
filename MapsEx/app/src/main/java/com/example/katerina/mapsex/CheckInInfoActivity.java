package com.example.katerina.mapsex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.katerina.mapsex.R;


public class CheckInInfoActivity extends Activity{
    String a, b;
    EditText Text1;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        Text1 = (EditText) findViewById(R.id.EditText01);


        final Button button1 = (Button) findViewById(R.id.button1);
        final EditText garbage1=(EditText)findViewById(R.id.garbage1);
        final EditText garbage2=(EditText)findViewById(R.id.garbage2);
        final EditText garbage3=(EditText)findViewById(R.id.garbage3);

        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                    a = Text1.getText().toString()+" ";
                    b= getResources().getString(R.string.garbage1)+": "+garbage1.getText().toString()+"\n"+getResources().getString(R.string.garbage2)+": "+garbage2.getText()+"\n"+getResources().getString(R.string.garbage3)+": "+garbage3.getText();
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("comment",a);
                    returnIntent.putExtra("garbage",b);
                    setResult(RESULT_OK,returnIntent);
                    finish();







            }


        });

        final Button button = (Button) findViewById(R.id.button);
         button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               Integer temp= Integer.parseInt(garbage1.getText().toString());
               if (temp>0){
               temp--;
                garbage1.getText().clear();
                garbage1.getText().append(temp.toString());}
            }

            });

        final Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Integer temp= Integer.parseInt(garbage1.getText().toString());
                temp++;
                garbage1.getText().clear();
                garbage1.getText().append(temp.toString());
            }

        });

        final Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Integer temp= Integer.parseInt(garbage2.getText().toString());
                if (temp>0){
                    temp--;
                    garbage2.getText().clear();
                    garbage2.getText().append(temp.toString());}
            }

        });

        final Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Integer temp= Integer.parseInt(garbage2.getText().toString());
                temp++;
                garbage2.getText().clear();
                garbage2.getText().append(temp.toString());
            }

        });

        final Button button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Integer temp= Integer.parseInt(garbage3.getText().toString());
                if (temp>0){
                    temp--;
                    garbage3.getText().clear();
                    garbage3.getText().append(temp.toString());}
            }

        });

        final Button button6 = (Button) findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Integer temp= Integer.parseInt(garbage3.getText().toString());
                temp++;
                garbage3.getText().clear();
                garbage3.getText().append(temp.toString());
            }

        });

    }




}
