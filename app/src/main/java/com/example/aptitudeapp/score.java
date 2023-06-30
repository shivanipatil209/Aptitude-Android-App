package com.example.aptitudeapp;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Serializable;
import java.util.ArrayList;


public class score extends AppCompatActivity {

    Button anothertest;
    Button solutions;
    TextView print_correctque;
    TextView print_skippedque;
    TextView print_wrongque;
    TextView time_required;
    boolean doubleBackToExistPressedOnce;

    String correctque,wrongque,skippedque,time;
    MediaPlayer mp;
    PieChart pieChart;
    PieData pieData;
    PieDataSet pieDataSet;
    ArrayList pieEntries;
    ArrayList PieEntryLabels;
    int correct;
    int skipped;
    int wrong;
    private ArrayList<Question> questionList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        mp=MediaPlayer.create(getApplicationContext(),R.raw.click);

        anothertest=(Button)findViewById(R.id.anothertest);
        solutions=(Button) findViewById(R.id.solutions);

        print_correctque=(TextView) findViewById(R.id.correct);
        print_skippedque = (TextView) findViewById(R.id.skipped);
        print_wrongque = (TextView) findViewById(R.id.wrong);
        time_required = (TextView) findViewById(R.id.time_required);

        Intent intent =getIntent();

        correctque = intent.getStringExtra("correctque");
        skippedque =  intent.getStringExtra("skippedque");
        wrongque = intent.getStringExtra("wrongque");
        time = intent.getStringExtra("time_required");
        questionList  = (ArrayList<Question>) intent.getBundleExtra("BUNDLE").getSerializable("questionData");
        correct= Integer.parseInt(intent.getStringExtra("correctque"));
        wrong= Integer.parseInt(intent.getStringExtra("wrongque"));
        skipped= Integer.parseInt(intent.getStringExtra("skippedque"));
        print_correctque.setText(correctque);
        print_skippedque.setText(skippedque);
        print_wrongque.setText(wrongque);
        time_required.setText(time);

        pieChart = findViewById(R.id.pieChart);
        getEntries();
        pieDataSet = new PieDataSet(pieEntries, " ");

        pieDataSet.setSliceSpace(2f);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(20f);
        pieDataSet.setSliceSpace(5f);
        pieDataSet.setColors(getResources().getColor(R.color.teal_700),getResources().getColor(R.color.blue),getResources().getColor(R.color.modified_red));
        pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setCenterText("SCORE ANALYSIS");
        pieChart.setCenterTextSize(20f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.BLACK);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(60f);
        pieChart.setCenterTextColor(Color.WHITE);
        pieChart.getLegend().setEnabled(false);
        anothertest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                Intent intent1 = new Intent(score.this, DomainOptions.class);
                startActivity(intent1);
            }
        });

        solutions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                Intent intent2 = new Intent(score.this,Solution.class);
                Bundle args = new Bundle();
                args.putSerializable("questionData",(Serializable)questionList);
                intent2.putExtra("BUNDLE",args);
                intent2.putExtra("correctque",correctque);
                intent2.putExtra("wrongque",wrongque);
                intent2.putExtra("skippedque",skippedque);
                intent2.putExtra("time",time);
                startActivity(intent2);
            }
        });

        if(savedInstanceState!=null){
            correctque = savedInstanceState.getString("correctque");
            wrongque = savedInstanceState.getString("wrongque");
            skippedque = savedInstanceState.getString("skippedque");
            time = savedInstanceState.getString("time_required");
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        savedInstanceState.putString("correctque", correctque);
        savedInstanceState.putString("wrongque", wrongque);
        savedInstanceState.putString("skippedque",skippedque);
        savedInstanceState.putString("time_required",time);
        savedInstanceState.putSerializable("questionList", questionList);
        // etc.
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
    private void getEntries() {
        pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(correct, "Correct"));
        pieEntries.add(new PieEntry(skipped, "Skipped"));
        pieEntries.add(new PieEntry(wrong, "Wrong"));

    }
    }
