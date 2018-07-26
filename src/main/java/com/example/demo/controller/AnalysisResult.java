package com.example.demo.controller;


import com.example.demo.classification.Classifier;
import com.example.demo.classification.PosTagger;
import com.example.demo.classification.Tokenizer;
import com.example.demo.model.Domain;
import com.example.demo.model.DomainTerm;
import com.example.demo.regrammar.GrammarsFactory;
import com.example.demo.model.DocumentTextReq;
import com.example.demo.wordnet.POS;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rita.RiTa;
import rita.RiWordNet;
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

        //for classification
        List<Domain> domainList = classifier.classify(alltext);
        //for reGrammar
        String textAfterReGrammar = gFactory.reGrammar(alltext);

        Map<String, String> wordsSynset = classifier.getWordsSynset(textAfterReGrammar, domainList);

        return classifier.classify(alltext);
    }

}
