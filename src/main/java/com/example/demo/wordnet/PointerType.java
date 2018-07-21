package com.example.demo.wordnet;

/**
 * <p>Title: JMWN</p>
 * <p>Description: MultiWordNet API</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ITC-Irst</p>
 * @author Christian Girardi
 * @version 1.0
 */


import java.util.NoSuchElementException;

/** Instances of this class enumerate the possible Multiwordnet pointer types, and are used to label <code>PointerType</code>s.
 * Each <code>PointerType</code> carries additional information:
 * a human-readable label, an optional reflexive type that labels links pointing the opposite direction,
 * an encoding of parts-of-speech that it applies to, and a short string that represents it in the dictionary files.
 *
 * @see Pointer
 * @see POS
 * <p>Original class edu.gwu.wordnet.PointerType (JWordnet 1.0 - author: Oliver Steele, steele@cs.brandeis.edu)
 */


public class PointerType {

  // All categories
    public static final PointerType ANTONYM = new PointerType("antonym", "!", "N>N | V>V | A>A | R>R | LEXICAL", true);
    public static final PointerType NEAREST = new PointerType("nearest", "|", "N>N | V>V | A>A | R>R" , true);
    public static final PointerType COMPOSED_OF = new PointerType("composed of", "+c", "N>N | N>V | N>A | N>R | V>V | V>N | V>A | V>R | A>A | A>N | A>V | A>R | R>R | R>N | R>V | R>A | LEXICAL", true);
    public static final PointerType COMPOSES = new PointerType("composes", "-c", "N>N | N>V | N>A | N>R | V>V | V>N | V>A | V>R | A>A | A>N | A>V | A>R | R>R | R>N | R>V | R>A | LEXICAL", true);


    // Nouns and Verbs
    public static final PointerType HYPERNYM = new PointerType("hypernym", "@", "N>N | V>V", false);
    public static final PointerType HYPONYM = new PointerType("hyponym", "~", "N>N | V>V", false);
    //new
    public static final PointerType INVOLVES = new PointerType("involves", "%i", "N>N | N>V | V>N | V>V", false);
    public static final PointerType ROLE = new PointerType("role", "#r", "N>N | N>V | V>N | V>V", false);

    // Nouns and Adjectives
    public static final PointerType ATTRIBUTE = new PointerType("attribute", "=", "N>A", false);
    public static final PointerType IS_VALUE_OF = new PointerType("is value of", "=", "A>N", false);
    public static final PointerType SEE_ALSO = new PointerType("also see", "^", "N>N | A>A | LEXICAL", true);

    // Verbs
    public static final PointerType ENTAILMENT = new PointerType("entailment", "*", "V>V", false);
    public static final PointerType CAUSE = new PointerType("cause", ">", "V>V", false);
    public static final PointerType VERB_GROUP = new PointerType("verb group", "$", "V>V | LEXICAL", true);

    // Nouns
    public static final PointerType MEMBER_MERONYM = new PointerType("member meronym", "#m", "N>N", false);
    public static final PointerType SUBSTANCE_MERONYM = new PointerType("substance meronym", "#s", "N>N", false);
    public static final PointerType PART_MERONYM = new PointerType("part meronym", "#p", "N>N", false);
    public static final PointerType MEMBER_HOLONYM = new PointerType("member holonym", "%m", "N>N", false);
    public static final PointerType SUBSTANCE_HOLONYM = new PointerType("substance holonym", "%s", "N>N", false);
    public static final PointerType PART_HOLONYM = new PointerType("part holonym", "%p", "N>N", false);
    //new
    public static final PointerType HAS_ROLE = new PointerType("has role", "+r", "N>N", false);
    public static final PointerType IS_ROLE_OF = new PointerType("is role of", "-r", "N>N", false);
    public static final PointerType HAS_FORM = new PointerType("has form", "+f", "N>N", false);
    public static final PointerType IS_FORM_OF = new PointerType("is form of", "-f", "N>N", false);
    public static final PointerType HAS_FUNCTION = new PointerType("has function", "+:", "N>N", false);
    public static final PointerType IS_FUNCTION_OF = new PointerType("is function of", "-:", "N>N", false);

    // Adjectives
    public static final PointerType SIMILAR_TO = new PointerType("similar", "&", "A>A", false);
    public static final PointerType PARTICIPLE_OF = new PointerType("participle of", "<", "A>V | LEXICAL", true );
    public static final PointerType PERTAINYM = new PointerType("pertainym", "\\", "A>N | N>A | LEXICAL", true);

    // Adverbs
    public static final PointerType DERIVED = new PointerType("derived from", "\\", "R>A | LEXICAL", true);


    /** A list of all <code>PointerType</code>s.
	Added relation: IS-VALUE-OF (Princeton relation) and NEAREST, COMPOSED_OF, COMPOSES (introduced by Irst)
     */
    public static final PointerType[] TYPES = {ANTONYM, HYPERNYM, HYPONYM, ATTRIBUTE, SEE_ALSO,
					       ENTAILMENT, CAUSE, VERB_GROUP,
					       MEMBER_MERONYM, SUBSTANCE_MERONYM, PART_MERONYM,
					       MEMBER_HOLONYM, SUBSTANCE_HOLONYM, PART_HOLONYM,
					       SIMILAR_TO, PARTICIPLE_OF, PERTAINYM, DERIVED,
					       IS_VALUE_OF, COMPOSED_OF, COMPOSES, NEAREST,
                           HAS_ROLE,IS_ROLE_OF,HAS_FORM,IS_FORM_OF,HAS_FUNCTION,IS_FUNCTION_OF,INVOLVES,ROLE};

    static protected void setSymmetric(PointerType a, PointerType b) {
	a.symmetricType = b;
	b.symmetricType = a;
    }

	static {
	setSymmetric(ANTONYM, ANTONYM);
	setSymmetric(HYPERNYM, HYPONYM);
	setSymmetric(MEMBER_MERONYM, MEMBER_HOLONYM);
	setSymmetric(SUBSTANCE_MERONYM, SUBSTANCE_HOLONYM);
	setSymmetric(PART_MERONYM, PART_HOLONYM);
	setSymmetric(SIMILAR_TO, SIMILAR_TO);
	setSymmetric(NEAREST, NEAREST);
	setSymmetric(COMPOSED_OF, COMPOSES);
	setSymmetric(HAS_ROLE, IS_ROLE_OF);
	setSymmetric(HAS_FORM, IS_FORM_OF);
	setSymmetric(HAS_FUNCTION, IS_FUNCTION_OF);
	setSymmetric(INVOLVES, ROLE);
	}

    /** Return the <code>PointerType</code> whose key matches <var>key</var>.
     * @exception NoSuchElementException If <var>key</var> doesn't name any <code>PointerType</code>.
     */
    static PointerType parseKey(String key) {
	for (int i = 0; i < TYPES.length; ++i) {
	    PointerType type = TYPES[i];
	    if (type.key.equals(key)) {
		return type;
	    }
	}
	// Need to make exception for these symbols that will be used in wordnet 2.0
	if (key.equals("+")) {
	    return null;
	}
	throw new NoSuchElementException("unknown link type " + key);
    }

    /*
     * Instance Interface
     */
    protected String label;
    protected String key;
    protected String flags;
    protected boolean isLanguageDependent;

    protected PointerType symmetricType;

    protected PointerType(String label, String key, String flags, boolean isLanguageDependent) {
	this.label = label;
	this.key = key;
	this.flags = flags;
	this.isLanguageDependent = isLanguageDependent;
    }

    public String getLabel() {
	return label;
    }

    public String getKey() {
      return key;
    }


    public boolean appliesTo(POS pos) {
      int code = pos.hashCode();
      switch (code) {
        case 1: {if (flags.indexOf("N>") != 0) {return true;}}
        case 2: {if (flags.indexOf("V>") != 0) {return true;}}
        case 3: {if (flags.indexOf("A>") != 0) {return true;}}
        case 4: {if (flags.indexOf("R>") != 0) {return true;}}
      }
      return false;
    }

    public boolean symmetricTo(PointerType type) {
	return symmetricType != null && symmetricType.equals(type);
    }

    //CG add method
    public boolean isLexical() {
	if (flags.indexOf("LEXICAL") != 0) {
	    return true;
	}
	return false;
    }

    public PointerType getSymmetric() {
      return symmetricType;
    }

    public boolean isLanguageDependent(PointerType type) {
	return isLanguageDependent;
    }

}
