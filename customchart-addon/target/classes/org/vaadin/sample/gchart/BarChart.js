function org_vaadin_sample_gchart_BarChart() {
    var connector;
    var loaded = false;

    function init() {
        google.charts.load('current', {'packages':['bar']});
        google.charts.setOnLoadCallback(this.ready);
    }

    this.ready = function() {
        connector.loaded = true;
        connector.draw();
    }

    this.doDraw = function() {
        // no direct draws:
        // need to check that charts are loaded before
        if (connector.loaded) {
            connector.draw();
        } else {
            google.charts.setOnLoadCallback(this.ready);
        }
    }

    this.draw = function () {
        // copy data for chart
        var result = new Array();
        var temp = this.getState().values;
        result[0] = this.getState().headers;

        for (index = 0; index < temp.length; index++ ) {
            var tempData = new Array();
            tempData[0] = temp[index][0];
            for (j = 1; j < temp[index].length; j++) {
                tempData[j] = parseInt(temp[index][j]);
            }
            result[index + 1] = tempData;
        }
        // debug data of state and the created chart-data
        console.log(this.getState());
        console.log(result);

        var chartData = google.visualization.arrayToDataTable(result);

        var options = {
          chart: {
            title: this.getState().title,
            subtitle: this.getState().subTitle,
          }
        };

        var chart = new google.charts.Bar(document.getElementById(this.getState().myId));

        chart.draw(chartData, google.charts.Bar.convertOptions(options));

    }

    connector = this;

    init();
}