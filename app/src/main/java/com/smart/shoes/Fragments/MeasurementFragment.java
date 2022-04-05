package com.smart.shoes.Fragments;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


import com.smart.shoes.Database.Database;
import com.smart.shoes.Models.SensorModel;
import com.smart.shoes.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;


public class MeasurementFragment extends Fragment {
    List<SensorModel> sensorModels;
    Database database;
    View view;
    private LineChartView chart;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorModels = new ArrayList<>();
        database = new Database(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_measurement, container, false);
        initUI();
        buildSpeedChart();
        buildDistanceChart();
        buildEmgGraph();
        return view;
    }

    private void buildEmgGraph() {
        sensorModels.clear();
        sensorModels.addAll(database.readAllInstances());

        Toast.makeText(getContext(), String.valueOf(sensorModels.size()), Toast.LENGTH_SHORT).show();
        LineChartView lineChartView;

        List<String> axisData;
        List<Integer> yAxisData;
        axisData=new ArrayList<>();
        yAxisData=new ArrayList<>();
        for(int i=0;i<sensorModels.size();i++){
            axisData.add(String.valueOf(i));
            yAxisData.add(Integer.parseInt(sensorModels.get(i).getSpeed()));
        }
        lineChartView = view.findViewById(R.id.chart3);

        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();

        Line line = new Line(yAxisValues).setColor(Color.parseColor("#0B1E8A"));

        for (int i = 0; i < axisData.size(); i++) {
            axisValues.add(i, new AxisValue(i).setLabel(axisData.get(i)));
        }

        for (int i = 0; i < yAxisData.size(); i++) {
            yAxisValues.add(new PointValue(i, yAxisData.get(i)));
        }

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setValues(axisValues);
        axis.setTextSize(16);
        axis.setTextColor(Color.parseColor("#03A9F4"));
        data.setAxisXBottom(axis);

        Axis yAxis = new Axis();
        yAxis.setName("Emg Signal");
        yAxis.setTextColor(Color.parseColor("#03A9F4"));
        yAxis.setTextSize(16);
        data.setAxisYLeft(yAxis);

        lineChartView.setLineChartData(data);
        Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
        viewport.top = 100;
        lineChartView.setMaximumViewport(viewport);
        lineChartView.setCurrentViewport(viewport);
    }

    private void buildSpeedChart() {
        sensorModels.clear();
        sensorModels.addAll(database.readAllInstances());

        Toast.makeText(getContext(), String.valueOf(sensorModels.size()), Toast.LENGTH_SHORT).show();
        LineChartView lineChartView;

        List<String> axisData;
        List<Integer> yAxisData;
        axisData=new ArrayList<>();
        yAxisData=new ArrayList<>();
        for(int i=0;i<sensorModels.size();i++){
            axisData.add(String.valueOf(i));
            yAxisData.add(Integer.parseInt(sensorModels.get(i).getSpeed()));
        }
        lineChartView = view.findViewById(R.id.chart);

        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();

        Line line = new Line(yAxisValues).setColor(Color.parseColor("#0B1E8A"));

        for (int i = 0; i < axisData.size(); i++) {
            axisValues.add(i, new AxisValue(i).setLabel(axisData.get(i)));
        }

        for (int i = 0; i < yAxisData.size(); i++) {
            yAxisValues.add(new PointValue(i, yAxisData.get(i)));
        }

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setValues(axisValues);
        axis.setTextSize(16);
        axis.setTextColor(Color.parseColor("#03A9F4"));
        data.setAxisXBottom(axis);

        Axis yAxis = new Axis();
        yAxis.setName("ACC Graph");
        yAxis.setTextColor(Color.parseColor("#03A9F4"));
        yAxis.setTextSize(16);
        data.setAxisYLeft(yAxis);

        lineChartView.setLineChartData(data);
        Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
        viewport.top = 100;
        lineChartView.setMaximumViewport(viewport);
        lineChartView.setCurrentViewport(viewport);
   }

    private void buildDistanceChart() {
        sensorModels.clear();
        sensorModels.addAll(database.readAllInstances());

        Toast.makeText(getContext(), String.valueOf(sensorModels.size()), Toast.LENGTH_SHORT).show();
        LineChartView lineChartView;

        List<String> axisData;
        List<Integer> yAxisData;
        axisData=new ArrayList<>();
        yAxisData=new ArrayList<>();
        for(int i=0;i<sensorModels.size();i++){
            axisData.add(String.valueOf(i));
            yAxisData.add(Integer.parseInt(sensorModels.get(i).getDistance()));
        }
        lineChartView = view.findViewById(R.id.chart2);

        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();

        Line line = new Line(yAxisValues).setColor(Color.parseColor("#64DD17"));

        for (int i = 0; i < axisData.size(); i++) {
            axisValues.add(i, new AxisValue(i).setLabel(axisData.get(i)));
        }

        for (int i = 0; i < yAxisData.size(); i++) {
            yAxisValues.add(new PointValue(i, yAxisData.get(i)));
        }

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setValues(axisValues);
        axis.setTextSize(16);
        axis.setTextColor(Color.parseColor("#03A9F4"));
        data.setAxisXBottom(axis);

        Axis yAxis = new Axis();
        yAxis.setName("Gyro graph");
        yAxis.setTextColor(Color.parseColor("#03A9F4"));
        yAxis.setTextSize(16);
        data.setAxisYLeft(yAxis);

        lineChartView.setLineChartData(data);
        Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
        viewport.top = 100;
        lineChartView.setMaximumViewport(viewport);
        lineChartView.setCurrentViewport(viewport);
    }


    private void initUI() {
        chart=view.findViewById(R.id.chart);
    }


}