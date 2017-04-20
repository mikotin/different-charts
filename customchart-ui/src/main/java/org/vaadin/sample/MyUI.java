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
import java.util.*;
import java.util.stream.Collectors;

/**
 * Playing with charts with Vaadin
 *
 * Uses super-simplified "backend" to fetch fixed data
 * Then draws the same data with 4 different approach:
 * 1. Vaadin Chart
 * 2. HighChart
 * 3. Google Chart (Google Visualization)
 * 4. JFreeChart
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
        String subTitle = "of Random population";

        HashMap<String, HashMap<String, Number>> dataMap = backend.getDataSet(Backend.groupType.SPECIE, Backend.valueType.POPULATION);

        // read the lower level groups to string set
        Set<String> lowlevelGroups = dataMap.values().stream().map(Map::keySet).flatMap(Collection::stream).distinct().collect(Collectors.toSet());

        // Initialize charts

        // Vaadin chart
        layout.addComponent(getChart(dataMap, lowlevelGroups, "Vaadin Chart", subTitle, xTtitle, yTitle));

        //HighChart
        layout.addComponent(getHighChart(dataMap, lowlevelGroups, "Highchart", subTitle, xTtitle, yTitle));

        // Google visualization
        layout.addComponent(getGChart(dataMap, lowlevelGroups, "Google Chart", subTitle, xTtitle, yTitle));

        // JFreeChart
        layout.addComponent(getJChart(dataMap, lowlevelGroups, "JFreeChart", subTitle, xTtitle, yTitle));

        setContent(layout);

    }

    protected HighChart getHighChart(HashMap<String, HashMap<String, Number>> data, Set<String> categories, String Title, String subTitle, String xTtitle, String yTitle) {
        HighChart chart = new HighChart();
        chart.setHeight("500px");
        chart.setSizeFull();

        // a single entity array to enable use of string within lambda functions
        // And really: should use a proper way to create json from Java entities, so don't use this horrific aproach in real life
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
        // google dataset model is just "the other way round" then the others
        // so let's reverse the hashmap
        HashMap<String, HashMap<String, Number>> reverseData = new HashMap<>();
        data.forEach((highKey, subMap) -> {
            subMap.forEach((lowKey, value) -> {
                if (!reverseData.containsKey(lowKey)) {
                    reverseData.put(lowKey, new HashMap<>());
                }
                reverseData.get(lowKey).put(highKey, value);
            } );
        });
        // Make custom-made google bar chart
        BarChart chart = new BarChart(Title, subTitle);

        // Add headers
        ArrayList<String> barHeaders = new ArrayList<>();
        barHeaders.add(xTtitle);
        // luckily the old data set now has the categories in keyset
        for (String c : data.keySet()) {
            barHeaders.add(c);
        }

        chart.addHeaders(barHeaders);

        // add data
        reverseData.forEach((s, stringNumberHashMap) -> {
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
