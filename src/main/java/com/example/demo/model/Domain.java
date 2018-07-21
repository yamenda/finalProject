package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

public class Domain {

    String domainName;
    List<DomainTerm> wordwithdomain;
    int synsetNumber = 0;
    int weightArray[][] = new int[synsetNumber][synsetNumber];
    int weith;


    public Domain() {
        wordwithdomain = new ArrayList<>();
    }

    public int[][] getWeightArray() {
        return weightArray;
    }

    public void setWeightArray(int[][] weightArray) {
        this.weightArray = weightArray;
    }

    public void initArray() {
        this.weightArray = new int[synsetNumber][synsetNumber];
        for (int i = 0 ; i< weightArray.length ; i++) {
            for (int j = 0 ; j< weightArray.length ; j++){
                this.weightArray[i][j] = 0;
            }
        }
    }

    public int getSynsetNumber() {
        return synsetNumber;
    }

    public void setSynsetNumber(int synsetNumber) {
        this.synsetNumber = synsetNumber;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public List<DomainTerm> getWordwithdomain() {
        return wordwithdomain;
    }

    public void setWordwithdomain(List<DomainTerm> wordwithdomain) {
        this.wordwithdomain = wordwithdomain;
    }

    public int getWeith() {
        return weith;
    }

    public void setWeith(int weith) {
        this.weith = weith;
    }
}
