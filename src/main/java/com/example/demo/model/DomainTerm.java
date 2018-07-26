package com.example.demo.model;

import java.util.ArrayList;
import java.util.Collection;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DomainTerm that = (DomainTerm) o;

        if (wordPosition != that.wordPosition) return false;
        if (!word.equals(that.word)) return false;
        if (domain != null ? !domain.equals(that.domain) : that.domain != null) return false;
        if (synsetsIds != null ? !synsetsIds.equals(that.synsetsIds) : that.synsetsIds != null) return false;
        if (rightSynsetOffset != null ? !rightSynsetOffset.equals(that.rightSynsetOffset) : that.rightSynsetOffset != null)
            return false;
        return synsetWord != null ? synsetWord.equals(that.synsetWord) : that.synsetWord == null;
    }

    @Override
    public int hashCode() {
        return word.hashCode();
    }


}
