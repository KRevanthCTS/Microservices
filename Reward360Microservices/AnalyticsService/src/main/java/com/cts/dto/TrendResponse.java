package com.cts.dto;
import java.util.List;


public class TrendResponse {
    private List<String> labels;
    private List<Long> data;

    public TrendResponse(List<String> labels, List<Long> data) {
        this.labels = labels;
        this.data = data;
    }
    public List<String> getLabels() {
        return labels;
    }
    
    public List<Long> getData() {
        return data;
    }
    
    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
    
    public void setData(List<Long> data) {
        this.data = data;
    }
    
     @Override
     public String toString() {
         return "TrendResponse{" +
                 "labels=" + labels +
                 ", data=" + data +
                 '}';
     }
}