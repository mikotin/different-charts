Playing with charts
===================

This is a test-project for making charts with Vaadin.

We have 4 different approaches to tackle the charts:

### Vaadin Charts
Vaadin have great build-in charts to use. These are flexible, easy to use and superbly beatiful.

### Highcharts
This is also the backbone of Vaadin Charts. So look and feel is the same as Vaadin Charts. Difference comes from the fact that Highcharts are plain js.

### Google Charts
Strong and free charts library from google. Downside is that it's javascript.

### JFreeChart
Old library, so end results are not that pretty. Plus side is that it's free and fully Java

# Running the project

To build the widgetsets: (in root directory, the parent module)
```
mvn clean install
```

To run the project: (the ui-module)
```
cd customchart-ui
mvn spring-boot:run
```

# License
The code itself is under Apache License 2.0, but used libraries are different scenario:

Vaadin Charts: commercial, you need to get a license: https://vaadin.com/charts#

Highchart: commercial or Creative Commons (CC) Attribution-NonCommercial licence for non-commercial usage https://shop.highsoft.com/highcharts

JFreeChart: LGPL

