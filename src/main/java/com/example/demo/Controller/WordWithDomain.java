package com.example.demo.Controller;

import java.util.ArrayList;
import java.util.List;

public class WordWithDomain {

    String word;
    String domain;
    public List<String> synsetsIds;


    public WordWithDomain() {
        synsetsIds = new ArrayList<>();
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

}
