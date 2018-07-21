package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

public class DomainTerm {

    String word;
    String domain;
    public List<String> synsetsIds;


    public DomainTerm() {
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
