package com.example.demo.controller;

import com.example.demo.model.DocumentTextReq;
import com.example.demo.regrammar.GrammarsFactory;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Properties;


@RestController
public class ReGrammerController {
    GrammarsFactory gFactory;

    @RequestMapping(value = "/regrammer", method = RequestMethod.POST)
    public String grammarProcess(@RequestBody DocumentTextReq textObj){
        String text = textObj.text;
        gFactory = new GrammarsFactory();
        return gFactory.reGrammar(text);
    }

}
