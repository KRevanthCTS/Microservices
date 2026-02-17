
package com.cts.dto;

import java.util.List;




public class ReportResponse {
    private String metric;
    private String start;
    private String end;
    private List<String> labels;
    private List<Integer> values;

    public ReportResponse(String metric, String start, String end, List<String> labels, List<Integer> values) {
        this.metric = metric;
        this.start = start;
        this.end = end;
        this.labels = labels;
        this.values = values;

    }
    
    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<Integer> getValues() {
        return values;
    }

    public void setValues(List<Integer> values) {
        this.values = values;
    }
    
    
}