package com.example.idlegatherer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Smithing extends AppCompatActivity {

    String[] barNames = {"Bronze","Iron","Mithril"};
    float[] smithingDuration = {8.f,16.f,32.f};
    double[] smithingProgress = {0.0,0.0,0.0};
    int[] bars = {0,0,0};

    int stamina = 1;

    boolean isSmithing = false;

    TextView[] text_barAmounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smithing);

        Button[] barButtons = new Button[barNames.length];

        barButtons[0] = findViewById(R.id.button_smithing_bronze);
        barButtons[1] = findViewById(R.id.button_smithing_iron);
        barButtons[2] = findViewById(R.id.button_smithing_mithril);

        ProgressBar[] barProgressBars = new ProgressBar[barNames.length];

        barProgressBars[0] = findViewById(R.id.progressBar_smithing_bronze);
        barProgressBars[1] = findViewById(R.id.progressBar_smithing_iron);
        barProgressBars[2] = findViewById(R.id.progressBar_smithing_mithril);

        text_barAmounts = new TextView[barNames.length];

        text_barAmounts[0] = findViewById(R.id.text_bronze_bar_amount);
        text_barAmounts[0].setText(String.valueOf(bars[0]));

        text_barAmounts[1] = findViewById(R.id.text_iron_bar_amount);
        text_barAmounts[1].setText(String.valueOf(bars[1]));

        text_barAmounts[2] = findViewById(R.id.text_mithril_bar_amount);
        text_barAmounts[2].setText(String.valueOf(bars[2]));

        for(int i = 0; i < barButtons.length; i++) {
            int finalI = i;
            barButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!isSmithing) {
                        smith(finalI, stamina, barProgressBars[finalI]);
                    }
                }
            });
        }
    }

    private void smith(int barId, int duration, ProgressBar progressBar){
        new Thread(new Runnable() {
            public void run() {

                double targetProgress = ((smithingProgress[barId] + duration/ smithingDuration[barId]*100));
                if(targetProgress % 100 == 0) {
                    targetProgress = 100;
                } else if (targetProgress > 100) {
                    targetProgress %= 100;
                }
                System.out.println("Target progress: " + targetProgress);

                double currentDuration = duration;
                long lastDelta = System.nanoTime();
                while (true) {
                    isSmithing = true;
                    long currentDelta = System.nanoTime();
                    double timeSinceLastDelta = currentDelta - lastDelta;
                    double deltaTime = timeSinceLastDelta / 1_000_000_000;

                    if((currentDuration - deltaTime) < 0) {
                        //System.out.println("Remaining duration " + currentDuration + " with delta " + deltaTime);
                        //double diff = currentDuration;
                        //System.out.println(miningProgress[barId] + " with diff " + diff);
                        //miningProgress[barId] += diff / miningDuration[barId] * 100;
                        //miningProgress[barId] = Math.round(miningProgress[barId]);
                        smithingProgress[barId] = targetProgress;
                        //System.out.println(miningProgress[barId]);
                        currentDuration = 0;
                        isSmithing = false;
                    } else {
                        smithingProgress[barId] += deltaTime / smithingDuration[barId] * 100;
                        currentDuration -= deltaTime;
                    }

                    if(smithingProgress[barId] >= 100) {
                        smithingProgress[barId] -= 100;
                        synchronized (this) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addBar(barId);
                                }
                            });
                        }
                    }

                    progressBar.setProgress((int) smithingProgress[barId]);

                    if(currentDuration <= 0) {
                        return;
                    }
                    lastDelta = currentDelta;
                }
            }
        }).start();
    }

    private void addBar(int barId) {
        bars[barId] += 1;
        text_barAmounts[barId].setText(String.valueOf(bars[barId]));
    }
}