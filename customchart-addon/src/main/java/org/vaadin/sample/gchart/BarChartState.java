package org.vaadin.sample.gchart;

import com.vaadin.shared.ui.JavaScriptComponentState;

import java.util.ArrayList;
import java.util.List;

public class BarChartState extends JavaScriptComponentState {
    public String title;
    public String subTitle;
    public List<String> headers = new ArrayList<>();
    public List<List<String>> values = new ArrayList<>();
    public String myId;
}
