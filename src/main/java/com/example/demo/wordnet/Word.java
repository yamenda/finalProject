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

/** A <code>Word</code> represents the lexical information related to a specific sense of an <code>IndexWord</code>.
 *
 * <code>Word</code>'s are linked by {@link Pointer}s into a network of lexically related Term.
 * {@link Word#getTargets getTargets} retrieves the targets of these links, and
 * {@link Word#getPointers getPointers} retrieves the pointers themselves.
 *
 * @see Pointer
 * @see Synset
 * <p>Original class edu.gwu.wordnet.Word (JWordnet 1.0 - author: Oliver Steele, steele@cs.brandeis.edu)
 */
public class Word implements PointerTarget {
	
	//
	// Instance implementation
	//
	protected Synset synset;
	protected int index;
	protected String lemma;
	
	public Word(Synset synset, int index, String lemma) {
		this.synset = synset;
		this.index = index;
		this.lemma = lemma;
	}
	

	
	//
	// Object methods
	//
	public boolean equals(Object object) {
		return (object instanceof Word)
			&& ((Word) object).synset.equals(synset)
			&& ((Word) object).index == index;
	}

	public int hashCode() {
		return synset.hashCode() ^ index;
	}
	
	public String toString() {
		return "[Word " + synset.offset + "@" + synset.pos + "(" + index + ")"
			 + ": \"" + getLemma() + "\"]";
	}
	

	//
	// Accessors
	//
	public Synset getSynset() {
		return synset;
	}
	
	public POS getPOS() {
		return synset.getPOS();
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getLemma() {
		return lemma;
	}
	
    
        public String getDescription() {
		return lemma;
	}
	
	public String getLongDescription() {
		String description = getDescription();
		String gloss = synset.getGloss();
		if (gloss != null) {
			description += " -- (" + gloss + ")";
		}
		return description;
	}



	//
	// Pointers
	//
	protected Pointer[] restrictPointers(Pointer[] source) {
		Vector vector = new Vector(source.length);
		for (int i = 0; i < source.length; ++i) {
			Pointer pointer = source[i];
			if (pointer.getSource() == this) {
				vector.addElement(pointer);
			}
		}
		Pointer[] result = new Pointer[vector.size()];
		vector.copyInto(result);
		return result;
	}
	
	public Pointer[] getPointers() {
		return restrictPointers(synset.getPointers());
	}
	
	public Pointer[] getPointers(PointerType type) {
		return restrictPointers(synset.getPointers(type));
	}
	
	public PointerTarget[] getTargets() {
		return Synset.collectTargets(getPointers());
	}
	
	public PointerTarget[] getTargets(PointerType type) {
		return Synset.collectTargets(getPointers(type));
	}
}
