package com.example.demo.Controller;


import com.example.demo.model.DeafGrammar;
import com.example.demo.model.GrammarsFactory;
import com.example.demo.model.StringText;
import com.example.demo.wordnet.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rita.RiTa;
import java.io.StringReader;
import java.util.*;

@RestController
public class api {

    @RequestMapping(value = "/justGet", method = RequestMethod.GET)
    public ResponseEntity<StringText> get() {

        StringText blue = new StringText("this is a get function.");

        return new ResponseEntity<StringText>(blue, HttpStatus.OK);
    }

    //getting all synsets ids related to every word in the text
    @RequestMapping(value = "/results", method = RequestMethod.POST)
    public List<Domain> results(@RequestBody StringText text) {
        String alltext = text.text;

        List<words> list = getSynsets(alltext);

        List<WordWithDomain> listWordDomain = getWordsWithDomain(list);

        List<Domain> domainList = getDomains(listWordDomain);

        List<Domain> domainListWithWeith = calculateWeight(domainList,alltext);

        return domainList;
    }

    @RequestMapping(value = "/openie", method = RequestMethod.POST)
    public String grammarProcess(@RequestBody StringText textObj){
        String text = textObj.text;
        Document doc = new Document(text);
        List<Sentence> sentences = doc.sentences();

        GrammarsFactory gFactory = new GrammarsFactory();

        String finalRes = "";
        for (Sentence item : doc.sentences()) {
            String afterConverting = gFactory.getGrammar(item.text());
            item.text().replace(item.text(), afterConverting);
            finalRes += afterConverting;
        }

        return finalRes;
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

    public Map<String,POS> pos(String text) {
        String[] posString = RiTa.getPosTags(text);
        String[] wordString = tokenize(text);

        List<POS> posArray = convertPos(posString);

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

    public List<words> getSynsets(String alltext) {

        List<words> list = new ArrayList<>();

        String[] wordsArray = tokenize(alltext);
        Map<String, POS> map = pos(alltext);

        MysqlDictionary dictionary = new MysqlDictionary();

        for (String item: wordsArray) {

            POS pos = map.get(item);
            if(!pos.getLabel().equals("temp")) {
                //String itemsStem = RiTa.stem(item);
                IndexWord indexWords = dictionary.getIndexWordAt(pos, item, "english");
                words word = new words();
                word.setIndex("");
                word.setWord(indexWords.getLemma());
                word.setSynets(indexWords.getSynsetOffsets());
                list.add(word);
            }

        }

        return list;
    }

    public List<WordWithDomain> getWordsWithDomain(List<words> allWords) {

        List<WordWithDomain> list = new ArrayList<>();

        MysqlDictionary dictionary = new MysqlDictionary();

        List<String> specificDomains = new ArrayList<>();
        specificDomains.add("Economy");
        specificDomains.add("Finance");
        specificDomains.add("Banking");



        for (words word: allWords) {
            List<String> existDomain = new ArrayList<>();
            if(word.getSynets() !=  null) {
                for (String synset: word.getSynets()) {
                    String domain = dictionary.getDomain(synset);
                    if(!specificDomains.contains(domain)){
                        continue;
                    }
                    if(!domain.equals("") && !existDomain.contains(domain)){
                        WordWithDomain wordwithdomain = new WordWithDomain();
                        wordwithdomain.word = word.getWord();
                        existDomain.add(domain);
                        wordwithdomain.domain = domain;
                        wordwithdomain.synsetsIds.add(synset);
                        list.add(wordwithdomain);
                    }else {
                        if(getByDomain(list, word.getWord(),domain) != null) {
                            getByDomain(list, word.getWord(),domain).synsetsIds.add(synset);
                        }
                    }
                }
            }

        }

        return list;
    }

    public WordWithDomain getByDomain(List<WordWithDomain> list, String wordString, String domain) {
        for (WordWithDomain word: list) {
            if(word.getWord().equals(wordString) && word.getDomain().equals(domain)){
                return word;
            }
        }
        return null;
    }

    public List<Domain> getDomains(List<WordWithDomain> allWordsWithDomain) {

        List<String> existDomain = new ArrayList<>();
        List<Domain> list = new ArrayList<>();

        for (WordWithDomain word: allWordsWithDomain) {
            if(!existDomain.contains(word.getDomain())){
                existDomain.add(word.getDomain());
                Domain objDomain = new Domain();
                objDomain.setDomainName(word.getDomain());
                objDomain.wordwithdomain.add(word);
                int synsetNumber = objDomain.getSynsetNumber();
                objDomain.setSynsetNumber(synsetNumber + word.synsetsIds.size());
                list.add(objDomain);
            }else{
                if(getDomainByName(list, word.getDomain()) != null) {
                    int synsetNumber = getDomainByName(list, word.getDomain()).getSynsetNumber();

                    List<String> wordsInDomain = new ArrayList<>();
                    for (WordWithDomain temp: getDomainByName(list, word.getDomain()).wordwithdomain) {
                        wordsInDomain.add(temp.getWord());
                    }

                    if(!wordsInDomain.contains(word.getWord())) {
                        getDomainByName(list, word.getDomain()).setSynsetNumber(synsetNumber + word.synsetsIds.size());
                        getDomainByName(list, word.getDomain()).wordwithdomain.add(word);
                    }
                }
            }
        }
        return list;
    }

    public Domain getDomainByName(List<Domain> list , String name) {
        for (Domain objDomain: list) {
            if(objDomain.getDomainName().equals(name)){
                return objDomain;
            }
        }
        return null;
    }

    //if we need to separate the text by ,
    public List<Domain> calculateWeight(List<Domain> list, String text) {

//        List<Domain> newList = list;
//
//        for (String smallText: separeatedtext) {
//        }
        for (Domain domain:list) {
            domain = calculateWeightDomain(domain, text);
            int weith = sumArray(domain.weightArray);
            domain.setWeith(weith);
        }

        return list;
    }

    //separating the text : NOTUSED
    public List<String> separateText(String text) {

        List<String> res = Arrays.asList(text.split("(?=[,.])"));

        return res;
    }

    public Domain calculateWeightDomain(Domain domain, String text) {

        Domain domainWithWeith = domain;
        domainWithWeith.initArray();

        String[] afterToknize = tokenize(text);

        List<String> textWords = new ArrayList<>();
        for (String s: afterToknize) {
            textWords.add(s);
        }

        String afterUntonize = RiTa.untokenize(afterToknize);
        Map map = RiTa.concordance(afterUntonize);

        int[][] temp = domainWithWeith.getWeightArray();

       // for (WordWithDomain word: domain.getWordwithdomain()) {
            //String synsetId = word.synsetsIds.get(0);
            //diambiguse process here : TODO

            for(int i = 0; i < temp.length; i++) {
                for(int j = 0; j < temp.length; j++) {
                    if( i == j) {
                        if(j < domain.getWordwithdomain().size()) {
                            WordWithDomain tempWord = domain.getWordwithdomain().get(j);
                            int wordCount = 0;
                            if(map.containsKey(tempWord.getWord())) {
                                wordCount = (int) map.get(tempWord.getWord());
                            }
                            temp[i][j] = wordCount;
                        }
                    }else {
                        //there is no disambiguse yet....... multi synset fot the item
                        if(i < domain.getWordwithdomain().size() && j < domain.getWordwithdomain().size()) {
                            WordWithDomain tempWord1 = domain.getWordwithdomain().get(i);
                            WordWithDomain tempWord2 = domain.getWordwithdomain().get(j);
                            if(textWords.contains(tempWord1.getWord()) && textWords.contains(tempWord2.getWord())){
                                temp[i][j]++;
                            }
                        }
                    }
                }
            }
        //}

        return domainWithWeith;
    }

    public int sumArray(int[][] array) {
        int res = 0;
        for(int i =0; i< array.length ; i++) {
            for(int j =0; j< array.length ; j++) {
                res+= array[i][j];
            }
        }
        return res;
    }

}
