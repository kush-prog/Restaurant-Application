package com.kush.restuarantapp.models;

import java.util.List;

public class ApiResponse {
    private int response_code;
    private int outcome_code;
    private String response_message;
    private int page;
    private int count;
    private int total_pages;
    private int total_items;
    private List<Cuisine> cuisines;
    private String txn_ref_no;

    public ApiResponse() {}

    // Getters and Setters
    public int getResponse_code() { return response_code; }
    public void setResponse_code(int response_code) { this.response_code = response_code; }

    public int getOutcome_code() { return outcome_code; }
    public void setOutcome_code(int outcome_code) { this.outcome_code = outcome_code; }

    public String getResponse_message() { return response_message; }
    public void setResponse_message(String response_message) { this.response_message = response_message; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public int getTotal_pages() { return total_pages; }
    public void setTotal_pages(int total_pages) { this.total_pages = total_pages; }

    public int getTotal_items() { return total_items; }
    public void setTotal_items(int total_items) { this.total_items = total_items; }

    public List<Cuisine> getCuisines() { return cuisines; }
    public void setCuisines(List<Cuisine> cuisines) { this.cuisines = cuisines; }

    public String getTxn_ref_no() { return txn_ref_no; }
    public void setTxn_ref_no(String txn_ref_no) { this.txn_ref_no = txn_ref_no; }

    public boolean isSuccessful() {
        return response_code == 200 && outcome_code == 200;
    }
}