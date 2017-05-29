package com.example.chartbuilder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button mButton;
    EditText mEdit;
    LineChart chart;
    MathsParser parser;
    List<Entry> entries;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (Button) findViewById(R.id.button);
        mEdit = (EditText) findViewById(R.id.editText);
        chart = (LineChart) findViewById(R.id.chart);
        parser = new MathsParser();
        entries = new ArrayList<Entry>();

        mButton.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        entries.clear();
                        String str = mEdit.getText().toString();
                        mEdit.getText().clear();
                        if(str.isEmpty())
                        {
                            chart.clear();
                        }
                        else
                        {
                            try
                            {
                                for (double i = -500; i < 500; i += 0.1)
                                {
                                    parser.setVariable("x", i);
                                    entries.add(new Entry((float) i, (float) parser.parse(str)));
                                }
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }

                            LineDataSet dataSet = new LineDataSet(entries, str);

                            LineData lineData = new LineData(dataSet);
                            chart.setData(lineData);
                            //chart.moveViewTo(0, 0, chart.getAxisLeft().getAxisDependency());
                            chart.zoom(45, 0, 0, 0, chart.getAxisLeft().getAxisDependency());
                        }
                        chart.invalidate(); // refresh

                    }
                });
    }
}
