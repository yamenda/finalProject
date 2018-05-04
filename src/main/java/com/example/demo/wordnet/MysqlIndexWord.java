package com.example.demo.wordnet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

/**
 * <p>Title: JMWN</p>
 * <p>Description: MultiWordNet API</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ITC-Irst</p>
 * @author Christian Girardi
 * @version 1.0
 */

public class MysqlIndexWord {
  protected MysqlDictionary dictionary;
  protected POS pos;
  protected String lemma;
  protected String language;
  protected int taggedSenseCount;
  // senses are initially stored as offsets, and paged in on demand.
  protected String[] synsetOffsets;
  /** This is null until getSenses has been called. */
  protected Synset[] synsets;

  protected PointerType[] ptrTypes = null;
  //
  // Initialization
  //
  MysqlIndexWord(MysqlDictionary dictionary) {
    this.dictionary = dictionary;
  }

  MysqlIndexWord initializeFrom(MysqlDictionary dictionary, POS pos, String lemma, String language) {
    this.lemma = lemma.replace(' ', '_');
    this.pos = pos;
    this.language = language;
    String field;

    Statement stmt = null;
    ResultSet rs = null;

    try {
      //stmt = ((Connection) dictionary.getConnection()).createStatement();
      String sql = "SELECT search_" + pos.getKey() + " as search, id_" + pos.getKey() + " as id FROM " + language + "_index WHERE lemma='" + lemma + "'";
      rs = stmt.executeQuery(sql);
      System.err.println("MysqlIndexWord initializeFrom (QUERY: " + sql + ")");
      if (rs.next()) {
          //PointerType
          StringTokenizer tokenizer = new StringTokenizer((String) rs.getString("search"), " ");
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

          // Synset ids
          tokenizer = new StringTokenizer((String) rs.getString("id"), " ");
          int senseCount = tokenizer.countTokens();
          this.taggedSenseCount = senseCount;
          this.synsetOffsets = new String[senseCount];
          for (int i = 0; i < senseCount; i++) {
            synsetOffsets[i] = (String) tokenizer.nextElement();
          }

        }
        rs.close();
        stmt.close();
    } catch (SQLException E) {
      System.out.println("MysqlIndexWord.java - initializeFrom()");
      System.out.println("Connection - query problems with MySql");
      E.printStackTrace(System.out);
  }
    return this;
  }

  static MysqlIndexWord parseIndexWord(MysqlDictionary dictionary, POS pos, String lemma, String language) {
    try {
      return new MysqlIndexWord(dictionary).initializeFrom(dictionary,pos,lemma,language);
    } catch (RuntimeException e) {
      System.err.println("while parsing parseIndexWord()");
      throw e;
    }
  }


  //
  // Object methods
  //
  public boolean equals(Object object) {
    return (object instanceof MysqlIndexWord)
        && ((MysqlIndexWord) object).pos.equals(pos)
        && ((MysqlIndexWord) object).lemma.equals(lemma)
        && ((MysqlIndexWord) object).language.equals(language)
        ;
  }


  public String toString() {
    return "[MysqlIndexWord " + lemma + "@" + pos.getLabel() + ": \"" + language + "\"]";
  }

  //
  // Accessors
  //
  public POS getPOS() {
    return pos;
  }

  /**
  The pointer types available for this indexed word.  May not apply to all senses of the word.
*/
  public PointerType[] getPointerTypes() {
    return ptrTypes;
  }

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
    if (synsets == null) {
      System.err.println("getSenses(): " + synsetOffsets.toString());
      synsets = new Synset[synsetOffsets.length];
      for (int i = 0; i < synsetOffsets.length; ++i) {
        synsets[i] = dictionary.getSynsetAt(pos, synsetOffsets[i],language);
      }
    }
    return synsets;
  }

}
