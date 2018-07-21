package com.example.demo.classification;
import rita.RiTa;

public class Tokenizer {

    public Tokenizer() {
    }

    public String[] tokenize(String text) {
        String[] res;
        res = RiTa.tokenize(text);

        for (int i = 0 ; i<res.length ; i++) {
            String[] posCheck = RiTa.getPosTags(res[i]);
            if(posCheck[0].equals("nns") ||  posCheck[0].equals("nnps")) {
                res[i] = RiTa.stem(res[i]);
            }
        }
        return res;
    }

}
