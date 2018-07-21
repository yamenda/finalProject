package com.example.demo.classification;

import com.example.demo.wordnet.POS;
import rita.RiTa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PosTagger {

    Tokenizer tokenizer;

    public PosTagger() {
        tokenizer = new Tokenizer();
    }

    public Map<String,POS> pos(String text) {
        String[] posString = RiTa.getPosTags(text);
        String[] wordString = tokenizer.tokenize(text);

        List<POS> posArray = this.convertPos(posString);

        Map<String,POS> map = new HashMap<>();

        for (int i = 0 ; i < wordString.length ; i++) {
            map.put(wordString[i],posArray.get(i));
        }
        return map;
    }

    public List<POS> convertPos(String[] pos) {
        List<POS> res = new ArrayList<>();
        POS temp = new POS("temp" , "t");
        for (int i = 0; i< pos.length ; i++) {
            if(pos[i].equals("nn") || pos[i].equals("nns") || pos[i].equals("nnp") || pos[i].equals("nnps")) {
                res.add(POS.NOUN);
            }else if(pos[i].equals("vb") || pos[i].equals("vbd") || pos[i].equals("vbg") || pos[i].equals("vbn") || pos[i].equals("vbp") || pos[i].equals("vbz")) {
                res.add(POS.VERB);
            }else if(pos[i].equals("jj") || pos[i].equals("jjr")  || pos[i].equals("jjs") ) {
                res.add(POS.ADJ);
            }else if(pos[i].equals("rb") || pos[i].equals("rbr")  || pos[i].equals("rbs") ) {
                res.add(POS.ADV);
            }else {
                res.add(temp);
            }
        }
        return res;
    }


}
