package com.example.demo.wordnet;

/**
 * <p>Title: JMWN</p>
 * <p>Description: MultiWordNet API</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ITC-Irst</p>
 * @author Christian Girardi
 * @version 1.0
 */

import java.util.*;


/** The methods in this Interface must be propertly implemented to access the MultiWordNet
 * database.  
 * <p>Original class edu.gwu.wordnet.DictionaryDatabase (JWordnet 1.0 - author: Oliver Steele, steele@cs.brandeis.edu)
*/

public interface DictionaryDatabase {

        /** Look up a word in the database.  The search is case-independent,
         * and phrases are separated by spaces ("look up", not "look_up").
         * @param pos The part-of-speech.
         * @param lemma The orthographic representation of the word.
         * @param language could be "italian" or "english".
         * @return An IndexWord representing the word, or <code>null</code> if no such entry exists.
         */

    public IndexWord lookupIndexWord(POS pos, String lemma, String language);



    /** Return an array of IndexWord whose lemmas contain <var>substring</var>
     * as a substring.
     * @param pos The part-of-speech.
     * @param language could be "italian" or "english".
     * @return An array of <code>IndexWord</code>s, or <code>null</code> if no such entry exists.
     */
    
    public IndexWord[]  searchIndexWords(POS pos, String substring, String language);
    
    
    /** Return an array with all the Synsets in the database.
	
     * @param pos The part-of-speech.
     * @param language could be "italian" or "english".
     * @return An array of <code>Synset</code>s, or <code>null</code> if no such entry exists.
     */
    
    public Synset[] synsets(POS pos, String language);

}



