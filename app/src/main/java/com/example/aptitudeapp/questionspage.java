package com.example.aptitudeapp;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class questionspage extends AppCompatActivity {


    private static long COUNT_DOWN = 600000;

    private TextView textViewQuestion;
    private TextView textViewQuestionCount;
    private TextView textViewCountDown;
    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private Button submit_next;
    private Button skip;
    private long backpressed;
    private  List<Question> questionList;

    private boolean answered;
    private int questionCounter;
    private int questionCountTotal;

    private int correctque=0;
    private int skippedque=0;
    private int wrongque=0;

    private ColorStateList textColorDefaultcd;
    private CountDownTimer countDownTimer;
    long timeleft;
    private Question currentQuestion;
    MediaPlayer mp;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference quizData = db.collection("quizData");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionspage);
        mp = MediaPlayer.create(getApplicationContext(), R.raw.click);

        textViewQuestion = findViewById(R.id.question);
        textViewQuestionCount = findViewById(R.id.questionno);
        textViewCountDown = findViewById(R.id.countdown);

        rbGroup = findViewById(R.id.radiogroup);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);
        rb4 = findViewById(R.id.radio_button4);

        submit_next = findViewById(R.id.submit_next);
        skip = findViewById(R.id.skip);
        textColorDefaultcd = textViewCountDown.getTextColors();
        timeleft = COUNT_DOWN;

        Intent intent = getIntent();
        final String Domain_name = intent.getStringExtra("Domain_name");
        final String level_type = intent.getStringExtra("level_type");

        submit_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()) {
                    checkAnswer();
                    showNextQuestion();
                } else {
                    Toast.makeText(questionspage.this, "Please answer the question", Toast.LENGTH_SHORT).show();
                }
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                skippedque++;
                showNextQuestion();

            }
        });

        questionList = new ArrayList<Question>();
        quizData.whereEqualTo("difficulty_level" ,level_type ).whereEqualTo("type",Domain_name).limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                System.out.println("document:"+document.get("question"));
                                Question question = document.toObject(Question.class);
                                questionList.add(question);
                            }
                            System.out.println("Size: "+questionList.size());
                            questionCountTotal = questionList.size();
                            Collections.shuffle(questionList);
                            showNextQuestion();
                            startCountDown();
                        } else {
                            System.out.println("Error getting documents");
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


        //To revive the variables that were set fro some value before changing the orientation  of the phone
        if(savedInstanceState!=null){
            textViewQuestion.setText( savedInstanceState.getString("textViewQuestion"));
            textViewQuestionCount.setText(savedInstanceState.getString("textViewQuestionCount"));
            correctque = savedInstanceState.getInt("correctque");
            skippedque = savedInstanceState.getInt("skippedque");
            rb1.setText(savedInstanceState.getString("rb1"));
            rb2.setText(savedInstanceState.getString("rb2"));
            rb3.setText(savedInstanceState.getString("rb3"));
            rb4.setText(savedInstanceState.getString("rb4"));
            answered = savedInstanceState.getBoolean("answered");
            questionCountTotal = savedInstanceState.getInt("questionCountTotal");
            questionCounter = savedInstanceState.getInt("questionCounter");
            timeleft = savedInstanceState.getLong("timeleft");
        }
    }


    private void showNextQuestion() {
        rbGroup.clearCheck();
        if (questionCounter < questionCountTotal) {
            currentQuestion = questionList.get(questionCounter);
            textViewQuestion.setText(currentQuestion.getQuestion());
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());
            rb4.setText(currentQuestion.getOption4());


            questionCounter++;
            textViewQuestionCount.setText("Question: " + questionCounter + "/" + questionCountTotal);
            answered = false;
        } else {
            finishQuiz();
        }
    }

    private void startCountDown() {
        countDownTimer = new CountDownTimer(timeleft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeleft = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                updateCountDownText();
                checkAnswer();

            }
        }.start();
    }

    private void updateCountDownText() {
        int min = (int) (timeleft / 1000) / 60;
        int sec = (int) (timeleft / 1000) % 60;

        String time = String.format(Locale.getDefault(), "%02d:%02d", min, sec);
        if (timeleft <= 2000) {
            finishQuiz();
        }
        textViewCountDown.setText(time);
        if (timeleft < 60000) {
            textViewCountDown.setTextColor(Color.RED);
        } else {
            textViewCountDown.setTextColor(Color.WHITE);
        }
    }

    private void checkAnswer() {
        answered = true;

        //countDownTimer.cancel();
        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        Number answerNr = rbGroup.indexOfChild(rbSelected) + 1;

        if (answerNr==currentQuestion.getAnswerNr()) {
            correctque++;
        }
        else{
            wrongque++;
        }
    }

    private void finishQuiz() {

        finish();
        int timerequired = (int) (COUNT_DOWN-timeleft);
        int min = (int) (timerequired/ 1000) / 60;
        int sec = (int) (timerequired / 1000) % 60;

        String time_required = String.format(Locale.getDefault(), "%02d:%02d", min, sec);
        Intent intent2 = new Intent(getApplicationContext(), score.class);
        intent2.putExtra("correctque",Integer.toString(correctque));
        intent2.putExtra("skippedque",Integer.toString(skippedque));
        intent2.putExtra("wrongque",Integer.toString(wrongque));
        intent2.putExtra("time_required",time_required);
        Bundle args = new Bundle();
        args.putSerializable("questionData",(Serializable)questionList);
        intent2.putExtra("BUNDLE",args);
        startActivity(intent2);
    }

    public void onBackPressed() {
        if (backpressed + 2000 > System.currentTimeMillis()) {
            skippedque = skippedque + questionCountTotal - questionCounter+1 ;
            finishQuiz();

        } else {
            Toast.makeText(this, "Press  back again to finish", Toast.LENGTH_SHORT).show();
        }
        backpressed = System.currentTimeMillis();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    //To store the values of variables before changing the orientation of the phone
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("textViewQuestion", (String)textViewQuestion.getText() );
        outState.putString("textViewQuestionCount",(String) textViewQuestionCount.getText());
        outState.putInt("correctque",correctque);
        outState.putInt("skippedque",skippedque);
        outState.putString("rb1",(String) rb1.getText());
        outState.putString("rb2",(String) rb2.getText());
        outState.putString("rb3",(String) rb3.getText());
        outState.putString("rb4",(String) rb4.getText());
        outState.putBoolean("answered",answered);
        outState.putInt("questionCounter",questionCounter);
        outState.putInt("questionCountTotal",questionCountTotal);
        outState.putLong("timeleft",timeleft);
    }


}


