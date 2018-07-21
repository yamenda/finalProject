package com.example.demo.regrammar;
import edu.stanford.nlp.trees.Tree;

public interface DeafGrammar {
    String getGrammar();
    String convert(Tree tree);
}
