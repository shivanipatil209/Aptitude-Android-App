package com.example.aptitudeapp;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Objects;

public class Solution extends AppCompatActivity {


    TextView questionno,question,answer;
    Button previous,next,finish;

    ArrayList<Question> questionList;
    private int questionCounter=-1;
    private int questionCountTotal;
    private Question currentQuestion;

    private RadioGroup rbGroup;
    private RadioButton rb1, rb2,rb3,rb4;
    private String correctque,wrongque,skippedque,time;
    MediaPlayer mp;
    private boolean doubleBackToExistPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution);
        mp = MediaPlayer.create(getApplicationContext(), R.raw.click);
        questionno = findViewById(R.id.questionno);
        question = findViewById(R.id.question);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.prev);
        finish = findViewById(R.id.anothertest);

        rbGroup = findViewById(R.id.radiogroup);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);
        rb4 = findViewById(R.id.radio_button4);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        correctque = intent.getStringExtra("correctque");
        wrongque = intent.getStringExtra("wrongque");
        skippedque = intent.getStringExtra("skippedque");
        time  = intent.getStringExtra("time");
        questionList = (ArrayList<Question>) args.getSerializable("questionData");
        questionCountTotal = questionList.size();

        System.out.println("size: "+questionCountTotal);
        showNextQuestion();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                showNextQuestion();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
               showPreviousQuestion();
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent intent = new Intent(getApplicationContext(), score.class);
                intent.putExtra("BUNDLE",args);
                intent.putExtra("correctque",correctque);
                intent.putExtra("wrongque",wrongque);
                intent.putExtra("skippedque",skippedque);
                intent.putExtra("time_required",time);
                startActivity(intent);
            }
        });

        if(savedInstanceState!=null){
            question.setText( savedInstanceState.getString("question"));
            questionno.setText(savedInstanceState.getString("questionno"));
            rb1.setText(savedInstanceState.getString("rb1"));
            rb2.setText(savedInstanceState.getString("rb2"));
            rb3.setText(savedInstanceState.getString("rb3"));
            rb4.setText(savedInstanceState.getString("rb4"));
            questionCountTotal = savedInstanceState.getInt("questionCountTotal");
            questionCounter = savedInstanceState.getInt("questionCounter");
        }
    }
    private void updateRadioButtonColor(){
        rb1.setTextColor(Color.parseColor("#FAF7F7"));
        rb2.setTextColor(Color.parseColor("#FAF7F7"));
        rb3.setTextColor(Color.parseColor("#FAF7F7"));
        rb4.setTextColor(Color.parseColor("#FAF7F7"));
    }


    private void displayQuestionData(int questionCounter){

        for (int i = 0; i < rbGroup.getChildCount(); i++) {
            rbGroup.getChildAt(i).setEnabled(false);
        }
        updateRadioButtonColor();
        currentQuestion = questionList.get(questionCounter);
        Integer answer =  currentQuestion.getAnswerNr();
        System.out.println("Value: "+answer+ " "+Objects.equals(answer, 1)+" "+Objects.equals(answer, 2)+" "+Objects.equals(answer, 3)+" "+Objects.equals(answer, 4));
        if(Objects.equals(answer, 1)) rb1.setTextColor(Color.parseColor("#00FF00"));
        else if(Objects.equals(answer, 2)) rb2.setTextColor(Color.parseColor("#00FF00"));
        else if(Objects.equals(answer, 3)) rb3.setTextColor(Color.parseColor("#00FF00"));
        else if(Objects.equals(answer, 4)) rb4.setTextColor(Color.parseColor("#00FF00"));
        question.setText(currentQuestion.getQuestion());
        rb1.setText(currentQuestion.getOption1());
        rb2.setText(currentQuestion.getOption2());
        rb3.setText(currentQuestion.getOption3());
        rb4.setText(currentQuestion.getOption4());

        questionno.setText("Question: " +(questionCounter+1) + "/" + questionCountTotal);
    }
    private void showNextQuestion() {
            if (questionCounter<questionCountTotal-1) {
                questionCounter++;
                displayQuestionData(questionCounter);
            } else {
                Toast.makeText(Solution.this, "No more questions!!", Toast.LENGTH_SHORT).show();
            }
        System.out.println("Counter: "+questionCounter);
    }

    private void showPreviousQuestion() {

        if (questionCounter>0) {
            questionCounter--;
            displayQuestionData(questionCounter);
        } else {
            Toast.makeText(Solution.this, "No more questions!!", Toast.LENGTH_SHORT).show();
        }
        System.out.println("Counter: "+questionCounter);
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("question", (String)question.getText() );
        outState.putString("questionno",(String) questionno.getText());
        outState.putString("rb1",(String) rb1.getText());
        outState.putString("rb2",(String) rb2.getText());
        outState.putString("rb3",(String) rb3.getText());
        outState.putString("rb4",(String) rb4.getText());
        outState.putInt("questionCounter",questionCounter);
        outState.putInt("questionCountTotal",questionCountTotal);
    }
    @Override
    public void onBackPressed() {
        if(doubleBackToExistPressedOnce) {
            finishAffinity();
            mp.stop();
            super.onBackPressed();
            return;
        }

        this.doubleBackToExistPressedOnce = true;
        Toast.makeText(this,"Please click BACK again to exist" , Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExistPressedOnce = false;
            }
        },2000);
    }
}