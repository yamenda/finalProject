package com.example.demo.classification;

import com.example.demo.model.Domain;
import com.example.demo.model.DomainTerm;
import com.example.demo.wordnet.POS;
import rita.RiWordNet;

import javax.validation.constraints.Min;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Disambiguator {

    WordnetUtil wordnetUtil;

    public Disambiguator() {
        this.wordnetUtil = new WordnetUtil();
    }

    public List<Domain> disambiguate(List<Domain> list) {
        for (Domain domain: list) {
            List<DomainTerm> DomainWords = domain.getWordwithdomain();
            for (DomainTerm domainTerm: DomainWords) {
                if(!isAmbiguous(domainTerm)) {
                    continue;
                }else {
                    //get all the synset word by synset offset of the ambiguse word
                    Map<String,String> synsetMap = new HashMap<>();
                    for (String offset: domainTerm.synsetsIds) {
                        String synsetWord = this.getSynsetWord(offset);
                        synsetMap.put(offset, synsetWord);
                    }
                    RiWordNet wordnet = new RiWordNet("C:\\wn3.1.dict\\dict\\");
                    // the algorithm of disambiguate
                    DomainTerm leftDomainTerm = this.getClosestNeighborOnLeft(domainTerm, DomainWords);
                    DomainTerm rightDomainTerm = this.getClosestNeighborOnRight(domainTerm, DomainWords);

                    //first situation
                    Map<String, Float> synsetDistanceMap = new HashMap<>();

                    if(leftDomainTerm.synsetsIds.size() != 0 && rightDomainTerm.synsetsIds.size() != 0) {
                        //comparing and get map containing the synset offset with the minimum distance
                        for (String synsetOffset: domainTerm.synsetsIds) {
                            String synsetWord = synsetMap.get(synsetOffset);
                            String pos = this.getPosFromSynsetOffset(synsetOffset);
                            float distanceWithTheLeft = wordnet.getDistance(synsetWord, leftDomainTerm.getWord(), pos);
                            float distanceWithTheRight = wordnet.getDistance(synsetWord, rightDomainTerm.getWord(), pos);
                            float minimumDistance = Math.min(distanceWithTheLeft, distanceWithTheRight);
                            synsetDistanceMap.put(synsetOffset, minimumDistance);
                        }
                    }else if(leftDomainTerm.synsetsIds.size() == 0) {
                        for (String synsetOffset: domainTerm.synsetsIds) {
                            String synsetWord = synsetMap.get(synsetOffset);
                            String pos = this.getPosFromSynsetOffset(synsetOffset);
                            float distanceWithTheRight = wordnet.getDistance(synsetWord, rightDomainTerm.getWord(), pos);
                            synsetDistanceMap.put(synsetOffset, distanceWithTheRight);
                        }
                    }else if(rightDomainTerm.synsetsIds.size() == 0) {
                        for (String synsetOffset: domainTerm.synsetsIds) {
                            String synsetWord = synsetMap.get(synsetOffset);
                            String pos = this.getPosFromSynsetOffset(synsetOffset);
                            float distanceWithTheLeft = wordnet.getDistance(synsetWord, leftDomainTerm.getWord(), pos);
                            synsetDistanceMap.put(synsetOffset, distanceWithTheLeft);
                        }
                    }

                    String res = "";
                    Float min =  Float.valueOf(Float.POSITIVE_INFINITY);
                    //comparing and get the synset offset with the minimum distance
                    for(Map.Entry<String, Float> item : synsetDistanceMap.entrySet()) {
                        if(min.compareTo(item.getValue()) > 0) {
                            res = item.getKey();
                            min = item.getValue();
                        }
                    }
                    //add the right synset to the domainTerm
                    domainTerm.setRightSynsetOffset(res);
                    domainTerm.setSynsetWord(synsetMap.get(res));

                }
            }
            domain.setWordwithdomain(DomainWords);
        }

        return list;
    }

    public boolean isAmbiguous(DomainTerm domainTerm) {
        return (domainTerm.synsetsIds.size() > 1);
    }

    public DomainTerm getClosestNeighborOnLeft(DomainTerm domainTerm, List<DomainTerm> list) {
        DomainTerm result = new DomainTerm();
        for (int i = 0 ; i < list.size() ; i++) {
            DomainTerm temp = list.get(i);
            if(temp.getWord().equalsIgnoreCase(domainTerm.getWord())) {
                continue;
            }else{
                if(result.synsetsIds.size() == 0 && temp.getWordPosition() < domainTerm.getWordPosition() && !isAmbiguous(temp)) {
                    result = temp;
                }else {
                    if(temp.getWordPosition() > result.getWordPosition() && temp.getWordPosition() < domainTerm.getWordPosition() && !isAmbiguous(temp)) {
                        result = temp;
                    }
                }
            }
        }

        return result;
    }

    public DomainTerm getClosestNeighborOnRight(DomainTerm domainTerm, List<DomainTerm> list) {
        DomainTerm result = new DomainTerm();
        for (int i = 0 ; i < list.size() ; i++) {
            DomainTerm temp = list.get(i);
            if(temp.getWord().equalsIgnoreCase(domainTerm.getWord())) {
                continue;
            }else{
                if(result.synsetsIds.size() == 0 && temp.getWordPosition() > domainTerm.getWordPosition() && !isAmbiguous(temp)) {
                    result = temp;
                }else {
                    if(temp.getWordPosition() < result.getWordPosition() && temp.getWordPosition() > domainTerm.getWordPosition() && !isAmbiguous(temp)) {
                        result = temp;
                    }
                }
            }
        }
        return result;
    }

    public String getSynsetWord(String offset) {
        // determine the POS
        POS pos = POS.NOUN;
        if(offset.indexOf(0) == 'v') {
            pos = POS.VERB;
        }else if(offset.indexOf(0) == 'r') {
            pos = POS.ADV;
        }else if(offset.indexOf(0) == 'a') {
            pos = POS.ADJ;
        }
        String synsetWord = wordnetUtil.getSynsetWord(offset, pos);
        return synsetWord;
    }

    public String getPosFromSynsetOffset(String offset) {
        String pos = RiWordNet.NOUN;
        if(offset.indexOf(0) == 'a') {
            pos = RiWordNet.ADJ;
        }else if (offset.indexOf(0) == 'r') {
            pos = RiWordNet.ADV;
        }else if(offset.indexOf(0) == 'v') {
            pos = RiWordNet.VERB;
        }
        return pos;
    }

}


