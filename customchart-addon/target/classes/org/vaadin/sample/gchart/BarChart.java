package org.vaadin.sample.gchart;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;

import java.util.ArrayList;
import java.util.List;

@JavaScript({ "https://www.gstatic.com/charts/loader.js", "BarChart.js" })
public class BarChart extends AbstractJavaScriptComponent {
    private static int elementId = 0;

    private String myId;

    public BarChart(String title, String subtitle) {
        myId = "gBarChartComponent" + (++elementId);
        callFunction("setId", myId);
        setSizeFull();
        setHeight("500px");
        setId(myId);
        getState().myId = myId;
        getState().title = title;
        getState().subTitle = subtitle;
    }

    @Override
    protected BarChartState getState() {
        return (BarChartState) super.getState();
    }


    /**
     * Sets headers of chart
     *
     * as: {'x-axis name', '1st type name', '2nd type name', '3rd type name'....}
     * ie: {"Year", "mice population", "mole population", "owl population"}
     * @param chartHeaders
     */
    public void addHeaders(List<String> chartHeaders) {
        getState().headers = chartHeaders;
    }

    /**
     * Adds a set of values to the chart
     *
     * as: {'group name', 'val1', 'val2', val3' ... }
     * ie: {"2016", 801000, 750345, 8435}
     * @param chartValues
     */
    public void addValues(List<String> chartValues) {
        getState().values.add(chartValues);
    }

    public void drawChart() {
        callFunction("doDraw");
    }
}
