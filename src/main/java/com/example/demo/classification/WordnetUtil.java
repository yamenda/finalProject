package com.example.demo.classification;

import com.example.demo.model.Domain;
import com.example.demo.model.DomainTerm;
import com.example.demo.model.Term;
import com.example.demo.wordnet.IndexWord;
import com.example.demo.wordnet.MysqlDictionary;
import com.example.demo.wordnet.POS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WordnetUtil {

    Tokenizer tokenizer;
    PosTagger posTagger;
    MysqlDictionary mysqlDictionary;

    public WordnetUtil() {
        this.tokenizer = new Tokenizer();
        this.posTagger = new PosTagger();
        this.mysqlDictionary = new MysqlDictionary();
    }

    public List<Term> getSynsets(String alltext) {
        List<Term> list = new ArrayList<>();
        String[] wordsArray = tokenizer.tokenize(alltext);
        Map<String, POS> map = posTagger.pos(alltext);

        MysqlDictionary dictionary = new MysqlDictionary();

        for (String item: wordsArray) {
            POS pos = map.get(item);
            if(!pos.getLabel().equals("temp")) {
                //String itemsStem = RiTa.stem(item);
                IndexWord indexWords = dictionary.getIndexWordAt(pos, item, "english");
                Term word = new Term();
                word.setIndex("");
                word.setWord(indexWords.getLemma());
                word.setSynets(indexWords.getSynsetOffsets());
                list.add(word);
            }
        }
        return list;
    }

    public List<DomainTerm> getWordsWithDomain(List<Term> allWords) {

        List<DomainTerm> list = new ArrayList<>();
        mysqlDictionary = new MysqlDictionary();

        List<String> specificDomains = new ArrayList<>();
        specificDomains.add("Economy");
        specificDomains.add("Finance");
        specificDomains.add("Banking");

        for (Term word: allWords) {
            List<String> existDomain = new ArrayList<>();
            if(word.getSynets() !=  null) {
                for (String synset: word.getSynets()) {
                    String domain = mysqlDictionary.getDomain(synset);
//                    if(!specificDomains.contains(domain)){
//                        continue;
//                    }
                    if(!domain.equals("") && !existDomain.contains(domain)){
                        DomainTerm wordwithdomain = new DomainTerm();
                        wordwithdomain.setWord(word.getWord());
                        existDomain.add(domain);
                        wordwithdomain.setDomain(domain);
                        wordwithdomain.synsetsIds.add(synset);
                        list.add(wordwithdomain);
                    }else {
                        if(getByDomain(list, word.getWord(),domain) != null) {
                            getByDomain(list, word.getWord(),domain).synsetsIds.add(synset);
                        }
                    }
                }
            }

        }

        return list;
    }

    public DomainTerm getByDomain(List<DomainTerm> list, String wordString, String domain) {
        for (DomainTerm word: list) {
            if(word.getWord().equals(wordString) && word.getDomain().equals(domain)){
                return word;
            }
        }
        return null;
    }

    public List<Domain> getDomains(List<DomainTerm> allWordsWithDomain) {

        List<String> existDomain = new ArrayList<>();
        List<Domain> list = new ArrayList<>();

        for (DomainTerm word: allWordsWithDomain) {
            if(!existDomain.contains(word.getDomain())){
                existDomain.add(word.getDomain());
                Domain objDomain = new Domain();
                objDomain.setDomainName(word.getDomain());
                objDomain.getWordwithdomain().add(word);
                int synsetNumber = objDomain.getSynsetNumber();
                objDomain.setSynsetNumber(synsetNumber + word.synsetsIds.size());
                list.add(objDomain);
            }else{
                if(getDomainByName(list, word.getDomain()) != null) {
                    int synsetNumber = getDomainByName(list, word.getDomain()).getSynsetNumber();

                    List<String> wordsInDomain = new ArrayList<>();
                    for (DomainTerm temp: getDomainByName(list, word.getDomain()).getWordwithdomain()) {
                        wordsInDomain.add(temp.getWord());
                    }

                    if(!wordsInDomain.contains(word.getWord())) {
                        getDomainByName(list, word.getDomain()).setSynsetNumber(synsetNumber + word.synsetsIds.size());
                        getDomainByName(list, word.getDomain()).getWordwithdomain().add(word);
                    }
                }
            }
        }
        return list;
    }

    public Domain getDomainByName(List<Domain> list , String name) {
        for (Domain objDomain: list) {
            if(objDomain.getDomainName().equals(name)){
                return objDomain;
            }
        }
        return null;
    }

}
