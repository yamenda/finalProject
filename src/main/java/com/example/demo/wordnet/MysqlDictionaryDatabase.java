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



/** The methods in this Interface must be propertly  implemented to access the Wordnet
 * database.  This method was chosen to allow interaction between a local file or remote
 * file system for database interaction.
 * @see FileBackedDictionary
 */

public interface MysqlDictionaryDatabase {

        /** Look up a word in the database.  The search is case-independent,
         * and phrases are separated by spaces ("look up", not "look_up").
         * @param pos The part-of-speech.
         * @param lemma The orthographic representation of the word.
         * @return An IndexWord representing the word, or <code>null</code> if no such entry exists.
         */

    public MysqlIndexWord lookupIndexWord(POS pos, String lemma, String language);



    /** Return an enumeration of all the IndexWords whose lemmas contain <var>substring</var>

     * as a substring.

         * @param pos The part-of-speech.

         * @return An enumeration of <code>IndexWord</code>s.

         */

        public Enumeration searchIndexWords(POS pos, String substring, String language);



        /** Return an enumeration over all the Synsets in the database.

         * @param pos The part-of-speech.

         * @return An enumeration of <code>Synset</code>s.

         */

        public Enumeration synsets(POS pos, String language);

}
