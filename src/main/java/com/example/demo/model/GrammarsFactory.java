package com.example.demo.model;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.*;
import javafx.util.Pair;

import java.io.StringReader;
import java.util.*;

public class GrammarsFactory {
    private static final HashMap<String, DeafGrammar> grammarList = new HashMap();

    public String getGrammar(String item){

        //TODO 1 : descover the sentance if it is a simple sentance or .....
        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        LexicalizedParser lp = LexicalizedParser.loadModel();
//        lp.setOptionFlags(new String[]{"-maxLength", "500", "-retainTmpSubcategories"});
        TokenizerFactory<CoreLabel> tokenizerFactory =
                PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
        List<CoreLabel> wordList = tokenizerFactory.getTokenizer(new StringReader(item)).tokenize();
        Tree tree = lp.apply(wordList);
        GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
        Collection<TypedDependency> depList = gs.typedDependenciesCollapsed();


        String result = "";

        for (Iterator<Tree> it = tree.iterator(); it.hasNext();) {

            Tree test = it.next();
            Tree parent = test.parent(tree);

            List<Tree> sib = null;
            if(parent != null) {
                sib = test.siblings(parent);
            }

            Label label = test.label();
            if(label.toString().equalsIgnoreCase("s") && !test.getChild(0).label().toString().equalsIgnoreCase("s")){

                gs = gsf.newGrammaticalStructure(test);
                Collection<TypedDependency> tempdepList = gs.typedDependenciesCollapsed();

                //the condition of simple Grammar
                if(!this.checkTreeDep(tempdepList, "nmod:tmod") && !this.checkTreeDep(tempdepList, "nmod:in")) {
                    DeafGrammar grammar = grammarList.get("simple");
                    if(grammar == null) {
                        grammar = new SimpleGrammar();
                        grammarList.put("simple", grammar);
                    }
                    // convert into spicific rules
                    result += grammar.convert(test) + " ";
                }

                //the condition of adverbs statement Grammar
                if(this.checkTreeDep(tempdepList, "nmod:tmod") || this.checkTreeDep(tempdepList, "nmod:in")) {
                    DeafGrammar grammar = grammarList.get("adverbs");
                    if(grammar == null) {
                        grammar = new AdverbGrammar();
                        grammarList.put("adverbs", grammar);
                    }
                    // convert into spicific rules
                    result += grammar.convert(test) + " ";
                }


            }

            if(sib != null && !label.toString().equalsIgnoreCase("s")) {
                if(this.checkTreeSbilings(sib, "s")){
                    result += test.getChild(0).value() + " ";
                }
            }

            boolean e = it.hasNext();
            boolean e1 = it.hasNext();
        }

        return result;
    }

    private boolean checkTreeSbilings(List<Tree> list , String label) {
        for(Tree tree: list){
            if(tree.label().toString().equalsIgnoreCase(label)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkTreeChildren(Tree tree , String label) {
        for (Iterator<Tree> it = tree.iterator(); it.hasNext();) {
            Tree temp = it.next();
            if(temp.label().toString().equalsIgnoreCase(label)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkTreeDep(Collection<TypedDependency> list, String dependency) {

        for(Iterator<TypedDependency> it = list.iterator(); it.hasNext(); ) {
            TypedDependency tDep = it.next();
            if(tDep.reln().toString().equalsIgnoreCase(dependency)) {
                return true;
            }
        }

        return false;
    }


}
