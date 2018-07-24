package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

public class DomainTerm {

    String word;
    String domain;
    int wordPosition;
    public List<String> synsetsIds;
    String rightSynsetOffset;
    String synsetWord;

    public DomainTerm() {
        synsetsIds = new ArrayList<>();
    }

    public String getRightSynsetOffset() {
        return rightSynsetOffset;
    }

    public void setRightSynsetOffset(String rightSynsetOffset) {
        this.rightSynsetOffset = rightSynsetOffset;
    }

    public String getSynsetWord() {
        return synsetWord;
    }

    public void setSynsetWord(String synsetWord) {
        this.synsetWord = synsetWord;
    }

    public int getWordPosition() {
        return wordPosition;
    }

    public void setWordPosition(int wordPosition) {
        this.wordPosition = wordPosition;
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
