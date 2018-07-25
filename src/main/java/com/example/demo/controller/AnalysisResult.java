package com.example.demo.controller;


import com.example.demo.classification.Classifier;
import com.example.demo.model.Domain;
import com.example.demo.model.DomainTerm;
import com.example.demo.regrammar.GrammarsFactory;
import com.example.demo.model.DocumentTextReq;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rita.wordnet.jwnl.JWNLException;

import java.util.*;

@RestController
public class AnalysisResult {

    Classifier classifier;
    GrammarsFactory gFactory;

    @RequestMapping(value = "/results", method = RequestMethod.POST)
    public List<Domain> results(@RequestBody DocumentTextReq text) throws JWNLException {
        String alltext = text.text;

        classifier = new Classifier();
        gFactory = new GrammarsFactory();

//        List<Domain> domainList = classifier.classify(alltext);
//
//        Domain bestDomainWieght = classifier.getBestWeightDomain(domainList);
//
//        List<DomainTerm> ListDomainTerm = classifier.getListWordDomain(alltext);
//
//        List<DomainTerm> termInBestDomainWieght = classifier.getAllTermInSpicificDomain(classifier.getListWordDomain(alltext), bestDomainWieght);
//
//        List<DomainTerm> afterDisambiguate = new ArrayList<>();
//
//        if(bestDomainWieght.getDomainName().equalsIgnoreCase("general")) {
//            afterDisambiguate = classifier.disambiguater.disambiguate(ListDomainTerm);
//        }else {
//            afterDisambiguate = classifier.disambiguater.disambiguate(termInBestDomainWieght);
//        }
//
//        //start regrammar
//        String textAfterReGrammar = gFactory.reGrammar(alltext);
//        Map<String, String> wordSynsetOffset = new HashMap<>();
//
//        List<DomainTerm> ListDomainTermAfterReGrammar = classifier.getListWordDomain(textAfterReGrammar);
//        for (DomainTerm domainTerm: ListDomainTerm) {
//
//        }
//
        return classifier.classify(alltext);
    }

}
