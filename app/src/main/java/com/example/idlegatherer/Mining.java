package com.example.idlegatherer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Mining extends AppCompatActivity {

    String[] oreNames = {"Copper","Tin","Iron","Coal","Mithril"};
    float[] miningDuration = {2.f,4.f,8.f,16.f,32.f};
    double[] miningProgress = {0.0,0.0,0.0,0.0,0.0};
    int[] ores = {0,0,0,0,0};

    int stamina = 1;

    boolean isMining = false;

    Thread miningThread;

    TextView[] text_oreAmounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mining);

        Button[] oreButtons = new Button[oreNames.length];

        oreButtons[0] = findViewById(R.id.button_mining_copper);
        oreButtons[1] = findViewById(R.id.button_mining_tin);
        oreButtons[2] = findViewById(R.id.button_mining_iron);
        oreButtons[3] = findViewById(R.id.button_mining_coal);
        oreButtons[4] = findViewById(R.id.button_mining_mithril);

        ProgressBar[] oreProgressBars = new ProgressBar[oreNames.length];

        oreProgressBars[0] = findViewById(R.id.progressBar_mining_copper);
        oreProgressBars[1] = findViewById(R.id.progressBar_mining_tin);
        oreProgressBars[2] = findViewById(R.id.progressBar_mining_iron);
        oreProgressBars[3] = findViewById(R.id.progressBar_mining_coal);
        oreProgressBars[4] = findViewById(R.id.progressBar_mining_mithril);

        text_oreAmounts = new TextView[oreNames.length];

        text_oreAmounts[0] = findViewById(R.id.text_copper_ore_amount);
        text_oreAmounts[0].setText(String.valueOf(ores[0]));

        text_oreAmounts[1] = findViewById(R.id.text_tin_ore_amount);
        text_oreAmounts[1].setText(String.valueOf(ores[1]));

        text_oreAmounts[2] = findViewById(R.id.text_iron_ore_amount);
        text_oreAmounts[2].setText(String.valueOf(ores[2]));

        text_oreAmounts[3] = findViewById(R.id.text_coal_amount);
        text_oreAmounts[3].setText(String.valueOf(ores[3]));

        text_oreAmounts[4] = findViewById(R.id.text_mithril_ore_amount);
        text_oreAmounts[4].setText(String.valueOf(ores[4]));

        for(int i = 0; i < oreButtons.length; i++) {
            int finalI = i;
            oreButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!isMining) {
                        mine(finalI, stamina, oreProgressBars[finalI]);
                    }
                }
            });
        }
    }

    private void mine(int oreId, int duration, ProgressBar progressBar){
        new Thread(new Runnable() {
            public void run() {

                double targetProgress = ((miningProgress[oreId] + duration/miningDuration[oreId]*100));
                if(targetProgress % 100 == 0) {
                    targetProgress = 100;
                } else if (targetProgress > 100) {
                    targetProgress %= 100;
                }
                System.out.println("Target progress: " + targetProgress);

                double currentDuration = duration;
                long lastDelta = System.nanoTime();
                while (true) {
                    isMining = true;
                    long currentDelta = System.nanoTime();
                    double timeSinceLastDelta = currentDelta - lastDelta;
                    double deltaTime = timeSinceLastDelta / 1_000_000_000;

                    if((currentDuration - deltaTime) < 0) {
                        //System.out.println("Remaining duration " + currentDuration + " with delta " + deltaTime);
                        //double diff = currentDuration;
                        //System.out.println(miningProgress[oreId] + " with diff " + diff);
                        //miningProgress[oreId] += diff / miningDuration[oreId] * 100;
                        //miningProgress[oreId] = Math.round(miningProgress[oreId]);
                        miningProgress[oreId] = targetProgress;
                        //System.out.println(miningProgress[oreId]);
                        currentDuration = 0;
                        isMining = false;
                    } else {
                        miningProgress[oreId] += deltaTime / miningDuration[oreId] * 100;
                        currentDuration -= deltaTime;
                    }

                    if(miningProgress[oreId] >= 100) {
                        miningProgress[oreId] -= 100;
                        synchronized (this) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addOre(oreId);
                                }
                            });
                        }
                    }

                    progressBar.setProgress((int) miningProgress[oreId]);

                    if(currentDuration <= 0) {
                        return;
                    }
                    lastDelta = currentDelta;
                }
            }
        }).start();
    }

    private void addOre(int oreId) {
        ores[oreId] += 1;
        text_oreAmounts[oreId].setText(String.valueOf(ores[oreId]));
    }
}