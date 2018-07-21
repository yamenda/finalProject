package com.example.demo.classification;

import com.example.demo.model.Domain;
import com.example.demo.model.DomainTerm;
import com.example.demo.model.Term;
import rita.RiTa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Classifier {

    Tokenizer tokenizer;
    WordnetUtil wordnetUtil;
    Disambiguator disambiguater;

    public Classifier() {
        this.tokenizer = new Tokenizer();
        this.wordnetUtil = new WordnetUtil();
    }

    public List<Domain> classify(String text) {
        List<Term> list = wordnetUtil.getSynsets(text);

        List<DomainTerm> listWordDomain = wordnetUtil.getWordsWithDomain(list);

        List<Domain> domainList = wordnetUtil.getDomains(listWordDomain);

        List<Domain> domainListWithWeith = calculateWeight(domainList,text);

        return domainList;
    }

    //if we need to separate the text by ,
    public List<Domain> calculateWeight(List<Domain> list, String text) {

//        List<Domain> newList = list;
//
//        for (String smallText: separeatedtext) {
//        }
        for (Domain domain:list) {
            domain = calculateWeightDomain(domain, text);
            int weith = sumArray(domain.getWeightArray());
            domain.setWeith(weith);
        }

        return list;
    }

    //separating the text : NOTUSED
    public List<String> separateText(String text) {
        List<String> res = Arrays.asList(text.split("(?=[,.])"));
        return res;
    }

    public Domain calculateWeightDomain(Domain domain, String text) {

        Domain domainWithWeith = domain;
        domainWithWeith.initArray();

        String[] afterToknize =tokenizer.tokenize(text);

        List<String> textWords = new ArrayList<>();
        for (String s: afterToknize) {
            textWords.add(s);
        }

        String afterUntonize = RiTa.untokenize(afterToknize);
        Map map = RiTa.concordance(afterUntonize);

        int[][] temp = domainWithWeith.getWeightArray();

        // for (DomainTerm word: domain.getWordwithdomain()) {
        //String synsetId = word.synsetsIds.get(0);

        //diambiguse process here : TODO

        for(int i = 0; i < temp.length; i++) {
            for(int j = 0; j < temp.length; j++) {
                if( i == j) {
                    if(j < domain.getWordwithdomain().size()) {
                        DomainTerm tempWord = domain.getWordwithdomain().get(j);
                        int wordCount = 0;
                        if(map.containsKey(tempWord.getWord())) {
                            wordCount = (int) map.get(tempWord.getWord());
                        }
                        temp[i][j] = wordCount;
                    }
                }else {
                    //there is no disambiguse yet....... multi synset fot the item
                    if(i < domain.getWordwithdomain().size() && j < domain.getWordwithdomain().size()) {
                        DomainTerm tempWord1 = domain.getWordwithdomain().get(i);
                        DomainTerm tempWord2 = domain.getWordwithdomain().get(j);
                        if(textWords.contains(tempWord1.getWord()) && textWords.contains(tempWord2.getWord())){
                            temp[i][j]++;
                        }
                    }
                }
            }
        }
        //}

        return domainWithWeith;
    }

    public int sumArray(int[][] array) {
        int res = 0;
        for(int i =0; i< array.length ; i++) {
            for(int j =0; j< array.length ; j++) {
                res+= array[i][j];
            }
        }
        return res;
    }


}
