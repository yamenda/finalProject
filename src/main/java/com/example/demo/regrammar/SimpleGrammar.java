package com.example.demo.regrammar;

import edu.stanford.nlp.trees.*;
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


        List<TypedDependency> conjList = this.checkTreeDep(testDepList, "conj");

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


            if(tDep.reln().toString().equalsIgnoreCase("dobj")
                    || tDep.reln().toString().equalsIgnoreCase("amod")
                    || tDep.reln().getShortName().equalsIgnoreCase("nmod") ) {

                if(tDep.reln().toString().equalsIgnoreCase("dobj")) {
                    List<TypedDependency> rightConj = this.getRightConj(conjList, tDep.dep().value());

                    String res = tDep.dep().value() + " ";
                    if(!rightConj.isEmpty()) {
                        for (TypedDependency conj: rightConj) {
                            res += conj.reln().getSpecific() + " " + conj.dep().value() + " " ;
                        }
                    }
                    sentenceList.put("etc", res);
                }
//                else if(tDep.reln().getShortName().equalsIgnoreCase("nmod")) {
//                    sentenceList.put("etc", RiTa.stem(tDep.dep().value()));
//                }
                else {
                    sentenceList.put("etc", RiTa.stem(tDep.dep().value()));
                }
            }

            if(tDep.reln().toString().equalsIgnoreCase("neg")) {
                sentenceList.put("negative", tDep.dep().value());
            }

        }

        String res = "";
        if(sentenceList.containsKey("subject")) {
            res += sentenceList.get("subject") + " ";
        }
        if(sentenceList.containsKey("verb")) {
            res += sentenceList.get("verb") + " ";
        }
        if(sentenceList.containsKey("etc")) {
            res += sentenceList.get("etc") + " ";
        }
        if(sentenceList.containsKey("negative")) {
            res += sentenceList.get("negative") + " ";
        }

        return res;
    }


    private List<TypedDependency> checkTreeDep(Collection<TypedDependency> list, String dependency) {

        List<TypedDependency> listDep = new ArrayList<>();

        for(Iterator<TypedDependency> it = list.iterator(); it.hasNext(); ) {
            TypedDependency tDep = it.next();
            if(tDep.reln().getShortName().toString().equalsIgnoreCase(dependency)) {
                listDep.add(tDep);
            }
        }
        return listDep;
    }

    private List<TypedDependency> getRightConj(List<TypedDependency> list, String dobjSec) {

        List<TypedDependency> conjRes = new ArrayList<>();

        for (TypedDependency tDep: list) {
            if(tDep.gov().value().equalsIgnoreCase(dobjSec)) {
                conjRes.add(tDep);
            }
        }
        return conjRes;
    }

}
