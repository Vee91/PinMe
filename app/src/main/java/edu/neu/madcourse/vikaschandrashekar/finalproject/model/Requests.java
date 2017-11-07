package edu.neu.madcourse.vikaschandrashekar.finalproject.model;

import java.util.Map;

/**
 * Created by cvikas on 4/18/2017.
 */

public class Requests {

    private String requestNo;
    private String requestType;
    private Map<String, String> from; // map of token and username
    private Map<String, String> to;
    private String status;

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public Map<String, String> getFrom() {
        return from;
    }

    public void setFrom(Map<String, String> from) {
        this.from = from;
    }

    public Map<String, String> getTo() {
        return to;
    }

    public void setTo(Map<String, String> to) {
        this.to = to;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
