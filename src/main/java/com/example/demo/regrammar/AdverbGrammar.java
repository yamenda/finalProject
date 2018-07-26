package com.example.demo.regrammar;

import edu.stanford.nlp.trees.*;
import rita.RiTa;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AdverbGrammar implements DeafGrammar {
    @Override
    public String getGrammar() {
        return "Subject : Adverb of time : Verb : End of sentence : adverbs of place";
    }

    @Override
    public String convert(Tree tree) {

        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
        Collection<TypedDependency> testDepList = gs.typedDependenciesCollapsed();

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

            //find the adverbe of time
            if(tDep.reln().toString().equalsIgnoreCase("nmod:tmod")) {
                sentenceList.put("adverbTime", RiTa.stem(tDep.dep().value()));
            }

            //find the adverbe of place
            if(tDep.reln().toString().equalsIgnoreCase("nmod:in")) {
                sentenceList.put("adverbPlace",tDep.reln().getSpecific() + " " + RiTa.stem(tDep.dep().value()));
            }

            //TODO : EDIT LIKE SIMPLE GRAMMAR
            if(tDep.reln().toString().equalsIgnoreCase("dobj")
                    || tDep.reln().toString().equalsIgnoreCase("amod")
                    || tDep.reln().toString().equalsIgnoreCase("compound")
                    || (tDep.reln().getShortName().equalsIgnoreCase("nmod") && tDep.reln().getSpecific().equalsIgnoreCase("to")) ) {
                sentenceList.put("etc", tDep.dep().value());
            }

            if(tDep.reln().toString().equalsIgnoreCase("neg")) {
                sentenceList.put("negative", tDep.dep().value());
            }

        }


        String res = "";
        if(sentenceList.containsKey("subject")) {
            res += sentenceList.get("subject") + " ";
        }
        if(sentenceList.containsKey("adverbTime")) {
            res += sentenceList.get("adverbTime") + " ";
        }
        if(sentenceList.containsKey("verb")) {
            res += sentenceList.get("verb") + " ";
        }
        if(sentenceList.containsKey("etc")) {
            res += sentenceList.get("etc") + " ";
        }
        if(sentenceList.containsKey("adverbPlace")) {
            res += sentenceList.get("adverbPlace") + " ";
        }
        if(sentenceList.containsKey("negative")) {
            res += sentenceList.get("negative") + " ";
        }

        return res;
    }


    private TypedDependency checkTreeDep(Collection<TypedDependency> list, String dependency) {

        for(Iterator<TypedDependency> it = list.iterator(); it.hasNext(); ) {
            TypedDependency tDep = it.next();
            if(tDep.reln().getShortName().toString().equalsIgnoreCase(dependency)) {
                return tDep;
            }
        }
        return null;
    }



}
