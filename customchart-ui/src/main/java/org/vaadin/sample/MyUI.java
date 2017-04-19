package org.vaadin.sample;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.annotations.JavaScript;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addon.JFreeChartWrapper;
import org.vaadin.sample.gchart.BarChart;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Playing with charts with Vaadin
 *
 * Uses super-simplified "backend" to fetch fixed data
 * Then draws the same data with 3 different approach:
 * 1. Vaadin Chart
 * 2. Google Chart (Google Visualization)
 * 3. JFreeChart
 */
@SpringUI
public class MyUI extends UI {
    @Autowired
    private DataSource dataSource;

    private Backend backend;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        // Initialize "backend"
        backend = new Backend(dataSource);

        // Create UI
        final VerticalLayout layout = new VerticalLayout();

        HashMap<String, HashMap<String, Number>> data;

        String xTtitle = "Countries";
        String yTitle = "Population";

        Set<String> headers = backend.getDataSet(Backend.groupType.COUNTRY, Backend.valueType.POPULATION).keySet();

        // Actually same data, but grouping changed
        // Using both, for data-model of all 3 charts is a bit different (gchart
        // Could be using the same, but to avoid extra lines of code and confusion let's just create a "better" looking data
        HashMap<String, HashMap<String, Number>> dataSpecie = backend.getDataSet(Backend.groupType.SPECIE, Backend.valueType.POPULATION);
        HashMap<String, HashMap<String, Number>> dataCountry = backend.getDataSet(Backend.groupType.COUNTRY, Backend.valueType.POPULATION);

        // Initialize charts

        // Vaadin chart
        layout.addComponent(getChart(dataSpecie, headers, "Vaadin Chart", "of Random population", xTtitle, yTitle));

        //HighChart
        layout.addComponent(getHighChart(dataSpecie, headers, "Highchart", "of Random population", xTtitle, yTitle));

        // Google visualization
        layout.addComponent(getGChart(dataCountry, headers, "Google Chart", "of Random population", xTtitle, yTitle));

        // JChart
        layout.addComponent(getJChart(dataSpecie, headers, "JChart", "of Random population", xTtitle, yTitle));

        setContent(layout);

        // Draw the google chart
        // gChart.drawChart();
    }

    protected HighChart getHighChart(HashMap<String, HashMap<String, Number>> data, Set<String> categories, String Title, String subTitle, String xTtitle, String yTitle) {
        HighChart chart = new HighChart();
        chart.setHeight("500px");
        chart.setSizeFull();

        // a single entity array to enable use of string within lambda functions
        final String[] options = {"{\n" +
                "    chart: {\n" +
                "        type: 'column'\n" +
                "    },\n" +
                "    title: {\n" +
                "        text: '" + Title + "'\n" +
                "    },\n" +
                "    subtitle: {\n" +
                "        text: '" + subTitle + "'\n" +
                "    },\n" +
                "    xAxis: {\n" +
                "        categories: [\n"};
        categories.forEach(s -> options[0] += "        '" + s + "',\n");

        options[0] +=
                "        ],\n" +
                "        title: {\n" +
                "            text: '" + xTtitle + "'\n" +
                "        },\n" +
                "        crosshair: true\n" +
                "    },\n" +
                "    yAxis: {\n" +
                "        min: 0,\n" +
                "        title: {\n" +
                "            text: '" + yTitle + "'\n" +
                "        }\n" +
                "    },\n" +
                "    series: [";
        data.forEach((s, stringNumberHashMap) -> {
            options[0] += "    {\n";
            options[0] += "        name: '" + s + "',\n";
            options[0] += "        data: [" + stringNumberHashMap.entrySet().stream().map(stringNumberEntry -> stringNumberEntry.getValue().toString()).collect(Collectors.joining(", ")) + "]\n";
            options[0] += "    },\n";
        });
        options[0] +=
        "    ]\n" +
        "}";

        chart.setHcjs("var options = " + options[0] + ";");

        return chart;
    }

    protected JFreeChartWrapper getJChart(HashMap<String, HashMap<String, Number>> data, Set<String> categories, String Title, String subTitle, String xTtitle, String yTitle) {
        // Create dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        data.forEach((key, stringNumberHashMap) -> {
            stringNumberHashMap.forEach((subkey, number) -> {
                dataset.addValue(number, key, subkey);
            });
        });
        // Make basic bar chart
        JFreeChart chart = ChartFactory.createBarChart(
                Title,
                xTtitle,
                yTitle,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // wrap it
        JFreeChartWrapper ret = new JFreeChartWrapper(chart);
        ret.setSizeFull();
        return ret;
    }

    protected BarChart getGChart(HashMap<String, HashMap<String, Number>> data, Set<String> categories, String Title, String subTitle, String xTtitle, String yTitle) {
        // Make custom-made google bar chart
        BarChart chart = new BarChart(Title, subTitle);

        // Add headers
        ArrayList<String> barHeaders = new ArrayList<>();
        barHeaders.add(xTtitle);
        for (String c : categories) {
            barHeaders.add(c);
        }
        chart.addHeaders(barHeaders);

        // add data
        data.forEach((s, stringNumberHashMap) -> {
            ArrayList<String> valueData = new ArrayList<>();
            valueData.add(s);
            stringNumberHashMap.forEach((s1, number) -> {
                valueData.add(number.toString());
            });
            chart.addValues(valueData);
        });

        chart.drawChart();

        return chart;
    }

    protected Component getChart(HashMap<String, HashMap<String, Number>> data, Set<String> categories, String Title, String subTitle, String xTtitle, String yTitle) {
        Chart chart = new Chart(ChartType.COLUMN);
        Configuration conf = chart.getConfiguration();

        conf.setTitle(Title);
        conf.setSubTitle(subTitle);
        // Add x-axis groups
        XAxis xAxis = conf.getxAxis();
        xAxis.setTitle(xTtitle);
        xAxis.setCategories();
        categories.forEach(s -> xAxis.addCategory(s));

        // Set title
        YAxis yAxis = conf.getyAxis();
        yAxis.setTitle(yTitle);

        // Add data
        data.forEach((s, stringNumberHashMap) -> {
            ListSeries series = new ListSeries(s);
            stringNumberHashMap.forEach((s1, number) -> {
                series.addData(number);
            });
            conf.addSeries(series);
        });
        return chart;
    }

}
