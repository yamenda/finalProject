package com.example.demo.model;

public class DocumentTextReq {

    public String text;

    public DocumentTextReq() {}

    public DocumentTextReq(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

