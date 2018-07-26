package com.example.demo.classification;

import com.example.demo.model.Domain;
import com.example.demo.model.DomainTerm;
import com.example.demo.model.Term;
import org.apache.xalan.xsltc.DOM;
import rita.RiTa;
import rita.RiWordNet;
import rita.wordnet.jwnl.JWNLException;
import rita.wordnet.jwnl.wndata.IndexWord;
import rita.wordnet.jwnl.wndata.POS;
import rita.wordnet.jwnl.wndata.Synset;

import java.util.*;

public class Classifier {

    Tokenizer tokenizer;
    WordnetUtil wordnetUtil;
    Disambiguator disambiguater;

    public Classifier() {
        this.tokenizer = new Tokenizer();
        this.wordnetUtil = new WordnetUtil();
        this.disambiguater = new Disambiguator();
    }

    public List<Domain> classify(String text) throws JWNLException {
        List<Term> list = wordnetUtil.getSynsets(text);
        List<DomainTerm> listWordDomain = wordnetUtil.getWordsWithDomain(list);
        List<Domain> domainList = wordnetUtil.getDomains(listWordDomain);
        List<Domain> domainListAfterDisambiguate = disambiguater.disambiguate(domainList);
        List<Domain> domainListWithWeith = calculateWeight(domainListAfterDisambiguate,text);
//      Domain bestWeightDomain = this.getBestWeightDomain(domainListWithWeith);
        return domainList;
    }

    public List<DomainTerm> getListWordDomain(String text) {
        List<Term> list = wordnetUtil.getSynsets(text);
        List<DomainTerm> listWordDomain = wordnetUtil.getWordsWithDomain(list);
        return listWordDomain;
    }

    //if we need to separate the text by ,
    public List<Domain> calculateWeight(List<Domain> list, String text) {

//        List<Domain> newList = list;
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

        //disambiguse process here : TODO

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

    public Domain getBestWeightDomain(List<Domain> list) {
        Domain res = new Domain();
        res.setDomainName("general");
        if(!list.isEmpty()) {
            res = list.get(0);
        }

        for (Domain domain : list) {
            if(res.getWeith() < domain.getWeith()) {
                res = domain;
            }
        }


//        //under test : it is cause a problem
//        if(this.isGeneralDomain(list)){
//            res = new Domain();
//            res.setDomainName("General");
//            List<DomainTerm> generalWords = new ArrayList<>();
//            for (Domain domain: list) {
//                generalWords.add(domain.getWordwithdomain().get(0));
//            }
//            res.setWordwithdomain(generalWords);
//            res.setWeith(list.size());
//        }

        return res;
    }

    public List<DomainTerm> getAllTermInSpicificDomain(List<DomainTerm> domainTerms, Domain domain) {
        List<DomainTerm> resultsList = new ArrayList<>();
        for (DomainTerm domainTerm: domainTerms) {
            if(domainTerm.getDomain().equalsIgnoreCase(domain.getDomainName())) {
                resultsList.add(domainTerm);
            }
        }
        return resultsList;
    }

    public boolean isGeneralDomain(List<Domain> list) {
        boolean check = true;
        for (Domain domain: list) {
            if(domain.getWeith() > 1) {
                check = false;
                break;
            }
        }
        return check;
    }

    public boolean containsDomainTermWord(Collection<? extends DomainTerm> collection, String word)
    {
        return collection.stream().anyMatch(a -> a.getWord().equals(word));
    }

    private boolean isExistInMultiDomain(List<Domain> list , String word) {
        int counter = 0;
        for (Domain domain:list) {
            if(this.containsDomainTermWord(domain.getWordwithdomain(), word)) {
                counter++;
            }
        }
        if (counter > 1 ) {
            return true;
        }
        return false;
    }

    private DomainTerm getDomainTermByWord(Domain domain, String word) {
        for (DomainTerm domainTerm: domain.getWordwithdomain()) {
            if(domainTerm.getWord().equalsIgnoreCase(word)) {
                return domainTerm;
            }
        }
        return null;
    }

    private String getWordSynsetOffset(List<Domain> list, String word) {

        String res = "";
        Domain bestDomain = this.getBestWeightDomain(list);
        if(!this.isExistInMultiDomain(list,word)) {
            for (Domain domain: list) {
                if(this.containsDomainTermWord(domain.getWordwithdomain(), word)) {
                    DomainTerm domainTerm = this.getDomainTermByWord(domain, word);
                    res = domainTerm.synsetsIds.get(0);
                    break;
                }
            }
        }
        else {
            if(this.containsDomainTermWord(bestDomain.getWordwithdomain(), word)) {
                DomainTerm domainTerm = this.getDomainTermByWord(bestDomain, word);
                res = domainTerm.synsetsIds.get(0);
            }
        }
        return res;
    }

    public Map<String, String> getWordsSynset(String text, List<Domain> list) {
        Map<String, String> mapRes = new HashMap<>();

        String[] tokenz = tokenizer.tokenize(text);

        for (String item: tokenz) {
            String synsetOffset = this.getWordSynsetOffset(list,item);
            mapRes.put(item, synsetOffset);
        }
        return mapRes;
    }

}
