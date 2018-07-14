package com.example.demo.model;

import edu.stanford.nlp.trees.*;
import javafx.util.Pair;
import rita.RiTa;

import java.util.*;

public class SimpleGrammar implements DeafGrammar {
    @Override
    public String getGrammar() {
        return "Subject : verb : end of sentence";
    }

    @Override
    public String convert(Tree tree) {

        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
        Collection<TypedDependency> testDepList = gs.typedDependenciesCollapsed();

//        List<Pair<String, String>> sentenceList = new ArrayList<>();

        Map<String,String> sentenceList = new HashMap<>();

        for(Iterator<TypedDependency> it = testDepList.iterator(); it.hasNext(); ) {
            TypedDependency tDep = it.next();
            GrammaticalRelation grammaticalRelation = tDep.reln();

            //find the main verb
            if(tDep.reln().toString().equalsIgnoreCase("root")) {
                sentenceList.put("verb", RiTa.stem(tDep.dep().value()));
            }
            //find the subject of the main verb
            if(tDep.reln().toString().equalsIgnoreCase("nsubj")) {
                sentenceList.put("subject", RiTa.stem(tDep.dep().value()));
            }

            if(tDep.reln().toString().equalsIgnoreCase("dobj") || tDep.reln().getShortName().equalsIgnoreCase("nmod") ) {
                sentenceList.put("etc", RiTa.stem(tDep.dep().value()));
            }
        }

        String res = "";
        res += sentenceList.get("subject") + " ";
        res += sentenceList.get("verb") + " ";
        res += sentenceList.get("etc") + " ";

        return res;
    }
}
