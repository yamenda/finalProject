package com.example.demo.wordnet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * <p>Title: JMWN</p>
 * <p>Description: MultiWordNet API</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ITC-Irst</p>
 * @author Christian Girardi
 * @version 1.0
 */


/** A <code>Synset</code>, or <b>syn</b>onym <b>set</b>, represents a record table <i>language</i><code>_synset</code> of the MultiWordNet database.
 * A <code>Synset</code> represents a concept, and contains a set of <code>Word</code>s and <code>Phrase</code>s, each of which has a sense
 * that names that concept (and each of which is therefore synonymous with the other Term in the
 * <code>Synset</code>).
 *
 * <code>Synset</code>'s are linked by {@link Pointer}s into a network of related concepts; this is the <it>Net</it>
 * in MultiWordNet.  {@link Synset#getTargets getTargets} retrieves the targets of these links, and
 * {@link Synset#getPointers getPointers} retrieves the pointers themselves.
 *
 * @see Word
 * @see Pointer
 * <p>Original class edu.gwu.wordnet.Synset (JWordnet 1.0 - author: Oliver Steele, steele@cs.brandeis.edu)
*/


public class Synset implements PointerTarget {
  protected MysqlDictionary dictionary;
  protected String language;
  protected POS pos;
  protected String offset;
  protected boolean isAdjectiveCluster;
  protected String word;
  protected String phrase;
  protected Word[] words;
  protected Word[] phraset;
  protected Pointer[] pointers;                                 
  protected String gloss;
  protected String domain;

    //The SQL statements
    private static final String SQL_SYNSET = "SELECT word,phrase,gloss,english FROM ?_synset LEFT JOIN semfield on id=synset WHERE id = ?";
    private static final String SQL_SEMANTIC_REL = "SELECT type,id_source,id_target FROM common_relation WHERE id_source= ? || id_target= ?";
    private static final String SQL_LEXICAL_REL = "SELECT type,id_source,id_target,w_source,w_target FROM ?_relation WHERE id_source= \"?\" || id_target=\"?\"";


  public String getWord() {
    return word;
  }

  public void setWord(String word) {
    this.word = word;
  }

  //
  // Object initialization
  //
  Synset(MysqlDictionary dictionary) {
    this.dictionary = dictionary;
  }

  Synset initializeFrom(MysqlDictionary dictionary, POS pos, String offset, String language) {
    this.offset = offset;
    this.isAdjectiveCluster = false;
    this.pos = pos;
    this.language = language;

    ResultSet rs;

    try {
	rs = dictionary.stmt.executeQuery("SELECT word,phrase,gloss,english FROM " + language + "_synset LEFT JOIN semfield on id=synset WHERE id = \"" + offset + "\"");

	  
	//String sql = "SELECT word,phrase,gloss,english FROM " + language + "_synset LEFT JOIN semfield on id=synset WHERE id = \"" + offset +"\"";
	//rs = stmt.executeQuery(sql);

      //System.err.println("Synset initializeFrom(ROWS: " + rs.getRow() + " (QUERY: " + sql + ")");

      while (rs.next()) {
        StringTokenizer tokenizer;
        int wordCount;
        String mysqlword = MysqlDictionary.encode(rs.getString("word"));
        String mysqlphrase = MysqlDictionary.encode(rs.getString("phrase"));

        //Synset Term
        if (mysqlword != null) {
          tokenizer = new StringTokenizer(mysqlword.trim() , " ");
          wordCount = tokenizer.countTokens();
          words = new Word[wordCount];
          for (int i = 0; i < wordCount; i++) {
            String lemma = tokenizer.nextToken();
            words[i] = new Word(this, i, lemma.replace('_', ' '));
          }
        }
        //Phraset Term
        if (mysqlphrase != null) {
          tokenizer = new StringTokenizer(mysqlphrase.trim() , " ");
          wordCount = tokenizer.countTokens();
          phraset = new Word[wordCount];
          for (int i = 0; i < wordCount; i++) {
            String lemma = tokenizer.nextToken();
            phraset[i] = new Word(this, i, lemma.replace('_', ' '));
          }
        }

        //Gloss
        this.gloss = MysqlDictionary.encode(rs.getString("gloss"));
        //Domain
        this.domain = MysqlDictionary.encode(rs.getString("english"));

      }
      rs.close();

    } catch (SQLException E) {
      System.out.println("IndexWord.java - initializeFrom()");
      System.out.println("Connection - query problems with MySql");
      E.printStackTrace(System.out);
    }

    //this.pointers = new Pointer[ptrs.size()];
    //ptrs.copyInto(pointers);

    return this;
  }

  static Synset parseSynset(MysqlDictionary dictionary, POS pos, String offset, String language) {
          try {
                  return new Synset(dictionary).initializeFrom(dictionary, pos, offset, language);
          } catch (RuntimeException e) {
                  System.err.println("while parsing parseSynset(...)");
                  throw e;
          }
  }

// static Synset parseSynsetWord(MysqlDictionary dictionary, String word, String language) {
//          try {
//                  return new Synset(dictionary).initializeFromWord(dictionary, word, language);
//          } catch (RuntimeException e) {
//                  System.err.println("while parsing parseSynset(...)");
//                  throw e;
//          }
//  }


//  Synset initializeFromWord(MysqlDictionary dictionary, String word, String language) {
//    //this.offset = offset;
//    this.isAdjectiveCluster = false;
//    //this.pos = pos;
//    this.language = language;
//
//    ResultSet rs;
//
//    try {
//      rs = dictionary.stmt.executeQuery("SELECT word,phrase,gloss,english FROM " + language + "_synset LEFT JOIN semfield on id=synset WHERE word = \"" + " " + word + "\"");
//
//
//      //String sql = "SELECT word,phrase,gloss,english FROM " + language + "_synset LEFT JOIN semfield on id=synset WHERE id = \"" + offset +"\"";
//      //rs = stmt.executeQuery(sql);
//
//      //System.err.println("Synset initializeFrom(ROWS: " + rs.getRow() + " (QUERY: " + sql + ")");
//
//      while (rs.next()) {
//        StringTokenizer tokenizer;
//        int wordCount;
//        String mysqlword = MysqlDictionary.encode(rs.getString("word"));
//        String mysqlphrase = MysqlDictionary.encode(rs.getString("phrase"));
//
//        //Synset Term
//        if (mysqlword != null) {
//          tokenizer = new StringTokenizer(mysqlword.trim() , " ");
//          wordCount = tokenizer.countTokens();
//          words = new Word[wordCount];
//          for (int i = 0; i < wordCount; i++) {
//            String lemma = tokenizer.nextToken();
//            words[i] = new Word(this, i, lemma.replace('_', ' '));
//          }
//        }
//        //Phraset Term
//        if (mysqlphrase != null) {
//          tokenizer = new StringTokenizer(mysqlphrase.trim() , " ");
//          wordCount = tokenizer.countTokens();
//          phraset = new Word[wordCount];
//          for (int i = 0; i < wordCount; i++) {
//            String lemma = tokenizer.nextToken();
//            phraset[i] = new Word(this, i, lemma.replace('_', ' '));
//          }
//        }
//
//        //Gloss
//        this.gloss = MysqlDictionary.encode(rs.getString("gloss"));
//        //Domain
//        this.domain = MysqlDictionary.encode(rs.getString("english"));
//
//      }
//      rs.close();
//
//    } catch (SQLException E) {
//      System.out.println("IndexWord.java - initializeFrom()");
//      System.out.println("Connection - query problems with MySql");
//      E.printStackTrace(System.out);
//    }
//
//    //this.pointers = new Pointer[ptrs.size()];
//    //ptrs.copyInto(pointers);
//
//    return this;
//  }




  //
  // Object methods
  //
  public boolean equals(Object object) {
    return (object instanceof Synset)
        && ((Synset) object).pos.equals(pos)
        && ((Synset) object).offset == offset
        && ((Synset) object).language.equals(language);
  }


  public String toString() {
        return "{" + offset + "} " + getDescription();
  }


  //
  // Accessors
  //
  public POS getPOS() {
    return pos;
  }

   public String getOffset() {
    return offset;
  }

  public String getGloss() {
    return gloss;
  }

  public String getDomain() {           
    return domain;
  }

  public Word[] getWords() {
    return words;
  }

  public Word getWord(int index) {
    return words[index];
  }

  //CG: new method
  public Word[] getPhraset() {
    return phraset;
  }

  public Word getPhrase(int index) {
    return phraset[index];
  }

 

  //
  // Description
  //
  public String getDescription() {
    StringBuffer buffer = new StringBuffer();
    if (words != null ) {
      for (int i = 0; i < words.length; ++i) {
        if (i > 0) {
          buffer.append(", ");
        }
        buffer.append(words[i].getLemma());
      }
    }
    if (phraset != null ) {
      for (int i = 0; i < phraset.length; ++i) {
        if (i > 0) {
          buffer.append(", ");
        }
        buffer.append(phraset[i].getLemma());
      }
    }
    return buffer.toString();
  }

  public String getLongDescription() {
    String description = this.getOffset() + " " + getDescription();
    String domainGloss = " --";
    if (this.domain != null) {
      domainGloss += " [" + domain + "]";
    }
    if (gloss != null) {
      domainGloss += " " + gloss;
    }
    if (!domainGloss.startsWith(" -- ")) {
      domainGloss = "";
    }
    return description + domainGloss;
  }


  //
  // Pointers
  //
  protected static PointerTarget[] collectTargets(Pointer[] pointers) {
    PointerTarget[] targets = new PointerTarget[pointers.length];
    for (int i = 0; i < pointers.length; ++i) {
      targets[i] = pointers[i].getTarget();
    }
    return targets;
  }

  public Pointer[] getPointers() {
      Vector ptrs = new Vector();

      ResultSet rs;
      
      try {
	  //Pointers
	  PointerType typeSQL;
	  Pointer pointer;
	  String target;
	  
	  ///SEMANTIC RELATION
	  
	  rs = dictionary.stmt.executeQuery("SELECT type,id_source,id_target FROM common_relation WHERE id_source= \"" + offset + "\" || id_target=\"" + offset + "\"");
	  while (rs.next()) {
	      typeSQL = PointerType.parseKey( (String) rs.getString("type"));
	      target = (String) rs.getString("id_target");
	      pointer = null;
	      
	      //System.out.println("INDEX = " + rs.getRow() + " " + (String) rs.getString("type") + target);
	      if (offset.equals( (String) rs.getString("id_source"))) {
		  pointer = Pointer.parsePointer(dictionary, this, "", typeSQL, target, "");
	      }
	      else if (typeSQL.getSymmetric() != null) {
		  typeSQL = typeSQL.getSymmetric();
		  pointer = Pointer.parsePointer(dictionary, this, "", typeSQL, (String) rs.getString("id_source"), "");
	      }
	      if (pointer != null) {
		  //System.out.println("ADD: " + rs.getRow() + " " + pointer.toString());
		  ptrs.addElement(pointer);
	      }
	      //if (pos == POS.VERB) {
	      //INFO sul frame di sottocategorizzazione dei verbi
	      //}
	  }
	  
	  ///LEXICAL RELATION
	  
	  rs = dictionary.stmt.executeQuery("SELECT type,id_source,id_target,w_source,w_target FROM " + language + "_relation WHERE id_source= \"" + offset + "\" || id_target=\"" + offset + "\"");
	  while (rs.next()) {
	      typeSQL = PointerType.parseKey((String) rs.getString("type"));
	      target = (String) rs.getString("id_target");
	      
	      //System.out.println("## INDEX = " + pointerCount + " " + (String) rs.getString("type") + target);
	      if (offset.equals(target) && typeSQL.getSymmetric() != null && !typeSQL.isLexical()) {
		  typeSQL = typeSQL.getSymmetric();
		  pointer = Pointer.parsePointer(dictionary, this, MysqlDictionary.encode(rs.getString("w_target")), typeSQL, (String) rs.getString("id_source"), (String) rs.getString("w_source"));
	      } else {
		  pointer = Pointer.parsePointer(dictionary, this, MysqlDictionary.encode(rs.getString("w_source")), typeSQL, target, (String) rs.getString("w_target"));
	      }
	      if (pointer != null) {
		  //System.out.println("## LEXICAL: " + offset + " " + pointer.toString());
		  ptrs.addElement(pointer);
	      }
	  }
	  rs.close();
	  
	  
      } catch (SQLException E) {
	  System.out.println("Connection - query problems with MySql");
	  E.printStackTrace(System.out);
      }
      
      this.pointers = new Pointer[ptrs.size()];
      ptrs.copyInto(pointers);
      
      return pointers;
  }
    
  public Pointer[] getPointers(PointerType type) {
    if (pointers == null) {
      getPointers() ;
    }
    Vector vector = new Vector();
    for (int i = 0; i < pointers.length; ++i) {
      Pointer pointer = pointers[i];
      if (pointer.getType().equals(type)) {
        vector.addElement(pointer);
      }
    }
    Pointer[] targets = new Pointer[vector.size()];
    vector.copyInto(targets);
    return targets;
  }

  public PointerTarget[] getTargets() {
    return collectTargets(getPointers());
  }

  public PointerTarget[] getTargets(PointerType type) {
    return collectTargets(getPointers(type));
  }

}

