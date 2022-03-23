package com.smart.shoes.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smart.shoes.Data.IncomingData;
import com.smart.shoes.R;


public class FatigueScoreFragment extends Fragment {
    View view;

    TextView textViewSpeed,textViewDistance
            ,textViewTime,textViewEmgSignal;
      IncomingData incomingData;
      TextView textViewRealTimeUpdate;
    Thread timerThread;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        incomingData=new IncomingData(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_fatigue_score, container, false);
        initUI();
        getData();
        threadedTheUpdates();
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void getData() {
        textViewSpeed.setText(incomingData.getSpeed()+" km/h");
        textViewDistance.setText(incomingData.getDistance()+" km");
        textViewEmgSignal.setText(incomingData.getTime()+"");

    }

    private void threadedTheUpdates() {

        timerThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(1000);// one second sleep
                        textViewRealTimeUpdate.post(new Runnable() {
                            public void run() {
                                if(!timerThread.isInterrupted()){
                                    try {
                                        getData();
                                        if(textViewRealTimeUpdate.getVisibility()==View.VISIBLE){
                                            textViewRealTimeUpdate.setVisibility(View.GONE);
                                        }else {
                                            textViewRealTimeUpdate.setVisibility(View.VISIBLE);
                                        }
                                    }catch (Exception e){
                                        // Toast.makeText(getApplicationContext(), "Crashed", Toast.LENGTH_SHORT).show();
                                    }


                                }

                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        timerThread.start();

    }

    private void initUI() {
        textViewSpeed=view.findViewById(R.id.textViewSpeed);
        textViewDistance=view.findViewById(R.id.textViewDistance);
        textViewTime=view.findViewById(R.id.textViewTime);
        textViewEmgSignal=view.findViewById(R.id.textViewEmgSignal);
        textViewRealTimeUpdate=view.findViewById(R.id.textViewRealTimeUpdate);
    }
}