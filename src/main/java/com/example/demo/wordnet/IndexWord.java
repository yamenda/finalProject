package com.example.demo.wordnet;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.StringTokenizer;
import java.io.UnsupportedEncodingException;

//import org.itc.mwn.*;

/**
 * <p>Title: JMWN</p>
 * <p>Description: MultiWordNet API</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ITC-Irst</p>
 * @author Christian Girardi
 * @version 1.0
 */

/**
 * An <code>IndexWord</code> represents a record of table <i>language</i><code>_index</code> of the MultiWordNet database.
 * An <code>IndexWord</code> is created retrieved or retrieved via {@link DictionaryDatabase#lookupIndexWord},
 * and has a <it>lemma</it>, a <it>pos</it>, and a set of <it>senses</it>, which are of type {@link Synset}.
 *
 * <p>Original class edu.gwu.wordnet.IndexWord (JWordnet 1.0 - author: Oliver Steele, steele@cs.brandeis.edu)
 */

public class IndexWord {
    protected MysqlDictionary dictionary;
    protected POS pos;
    protected String lemma;
    protected String language;
    protected int taggedSenseCount;
    // senses are initially stored as offsets, and paged in on demand.
    protected String[] synsetOffsets;
    /** This is null until getSenses has been called. */
    protected Synset[] synsets;

    public String[] getSynsetOffsets() {
        return synsetOffsets;
    }

    public void setSynsetOffsets(String[] synsetOffsets) {
        this.synsetOffsets = synsetOffsets;
    }

    //protected PointerType[] ptrTypes = null;

    //
    // Initialization
    //
    IndexWord(MysqlDictionary dictionary) {
        this.dictionary = dictionary;
    }

    IndexWord initializeFrom(MysqlDictionary dictionary, POS pos, String lemma, String language) {
        this.lemma = MysqlDictionary.encode(lemma.replace(' ', '_'));
        this.pos = pos;
        this.language = language;
        String field;

        ResultSet rs = null;
        StringTokenizer tokenizer;

        try {
            rs = dictionary.stmt.executeQuery("SELECT id_" + pos.getKey() + " as ids FROM " + language + "_index WHERE lemma=\"" + lemma + "\"");

            //System.out.println("IndexWord initializeFrom (QUERY: SELECT id_" + pos.getKey() + " as ids FROM " + language + "_index WHERE lemma=\"" + lemma + "\"");
            if (rs.next()) {
                /** NON VENGONO SETTATI
                 //PointerType
                 if (field != null) {
                 tokenizer = new StringTokenizer(field, " ");
                 int p_cnt = tokenizer.countTokens();
                 ptrTypes = new PointerType[p_cnt];
                 for (int i = 0; i < p_cnt; i++) {
                 try {
                 ptrTypes[i] = PointerType.parseKey(tokenizer.nextToken());
                 }
                 catch (java.util.NoSuchElementException exc) {
                 exc.printStackTrace();
                 }
                 }
                 }
                 */

                // Synset ids
                field = rs.getString("ids");
                if (field != null) {
                    tokenizer = new StringTokenizer(field, " ");
                    this.taggedSenseCount = tokenizer.countTokens();
                    this.synsetOffsets = new String[this.taggedSenseCount];
                    for (int i = 0; i < this.taggedSenseCount; i++) {
                        synsetOffsets[i] = (String) tokenizer.nextElement();
                    }
                }

            }
            rs.close();

        }  catch (SQLException E) {
            System.out.println("IndexWord.java - initializeFrom()");
            System.out.println("Connection - query problems with MySql");
            E.printStackTrace(System.out);
        }
        return this;
    }

    static IndexWord parseIndexWord(MysqlDictionary dictionary, POS pos, String lemma, String language) {
        try {
            return new IndexWord(dictionary).initializeFrom(dictionary,pos,lemma,language);
        } catch (RuntimeException e) {
            System.err.println("while parsing IndexWord()");
            throw e;
        }
    }


    //
    // Object methods
    //
    public boolean equals(Object object) {
        return (object instanceof IndexWord)
                && ((IndexWord) object).pos.equals(pos)
                && ((IndexWord) object).lemma.equals(lemma)
                && ((IndexWord) object).language.equals(language)
                ;
    }


    public String toString() {
        return "[IndexWord " + lemma + "@" + pos.getLabel() + ": \"" + language + "\"]";
    }

    //
    // Accessors
    //
    public POS getPOS() {
        return pos;
    }

    /**
     The pointer types available for this indexed word.  May not apply to all senses of the word.
     public PointerType[] getPointerTypes() {
     return ptrTypes;
     }
     */

    /** Return the word's <it>lemma</it>.  Its lemma is its orthographic representation, for
     * example <code>"dog"</code> or <code>"get up"</code>.
     */
    public String getLemma() {
        return lemma;
    }

    public int getTaggedSenseCount() {
        return taggedSenseCount;
    }

    public Synset[] getSenses() {
        if (synsets == null && synsetOffsets != null) {
            synsets = new Synset[synsetOffsets.length];
            for (int i = 0; i < synsetOffsets.length; ++i) {
                synsets[i] = dictionary.getSynsetAt(pos, synsetOffsets[i],language);
            }
        }
        return synsets;
    }

}
