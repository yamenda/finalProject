package com.example.demo.controller;

import com.example.demo.classification.Classifier;
import com.example.demo.model.Domain;
import com.example.demo.model.DocumentTextReq;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rita.wordnet.jwnl.JWNLException;

import java.util.List;

@RestController
public class ClassifierController {

    Classifier classifier;
    @RequestMapping(value = "/classify", method = RequestMethod.POST)
    public List<Domain> results(@RequestBody DocumentTextReq text) throws JWNLException {
        String alltext = text.text;
        classifier = new Classifier();
        List<Domain> results = classifier.classify(alltext);
        return results;
    }

}
