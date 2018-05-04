package com.example.demo.wordnet;

import java.util.NoSuchElementException;

/**
 * <p>Title: JMWN</p>
 * <p>Description: MultiWordNet API</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ITC-Irst</p>
 *
 * @author Christian Girardi
 * @version 1.0
 */


/** Instances of this class enumerate the possible major syntactic categories, or
 * <b>p</b>art's <b>o</b>f <b>s</b>peech.  Each <code>POS</code> has
 * a human-readable label that can be used to print it, and a key by which it can be looked up.
 *
 * <p>Original class edu.gwu.wordnet.POS (JWordnet 1.0 - author: Oliver Steele, steele@cs.brandeis.edu)
 */
public class POS {
    //
    // Class variables
    //
    public static final POS NOUN = new POS("noun", "n");
    public static final POS VERB = new POS("verb", "v");
    public static final POS ADJ = new POS("adjective", "a");
    public static final POS ADV = new POS("adverb", "r");

    /** A list of all <code>POS</code>s. */
    public static final POS[] CATS = {NOUN, VERB, ADJ, ADV};

    protected String label;
    protected String key;

    public POS(String label, String key) {
        this.label = label;
        this.key = key;
    }

    public String toString() {
        return "[POS " + label + "]";
    }

    public boolean equals(Object object) {
        return (object instanceof POS) && key.equals(((POS) object).key);
    }

    public int hashCode() {
        return key.hashCode();
    }

    /** Return a label intended for textual presentation. */
    public String getLabel() {
        return label;
    }

    public String getKey() {
        if (this == POS.NOUN) {
            return "n";
        } else if (this == POS.VERB) {
            return "v";
        } else if (this == POS.ADJ) {
            return "a";
        } else if (this == POS.ADV) {
            return "r";
        }
        return "";
    }

    /** Return the <code>PointerType</code> whose key matches <var>key</var>.
     * @exception NoSuchElementException If <var>key</var> doesn't name any <code>POS</code>.
     */
    public static POS lookup(String key) {
        for (int i = 0; i < CATS.length; ++i) {
            if (key.equals(CATS[i].key)) {
                return CATS[i];
            }
        }
        throw new NoSuchElementException("unknown POS " + key);
    }
}
