package com.example.demo.model;

public class Term {

    private String index;
    private String word;
    private String[] synets;

    public String[] getSynets() {
        return synets;
    }

    public void setSynets(String[] synets) {
        this.synets = synets;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
