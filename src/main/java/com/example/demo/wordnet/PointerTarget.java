package com.example.demo.wordnet;

/**
 * <p>Title: JMWN</p>
 * <p>Description: MultiWordNet API</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ITC-Irst</p>
 * @author Christian Girardi
 * @version 1.0
 */


/** A <code>PointerTarget</code> is the source or target of a <code>Pointer</code>.
 * The target of a semantic <code>PointerTarget</code> is a <code>Synset</code>;
 * the target of a lexical <code>PointerTarget</code> is a <code>Word</code>.
 *
 * @see Pointer
 * @see Synset
 * @see Word
 * <p>Original class edu.gwu.wordnet.PointerTarget (JWordnet 1.0 - author: Oliver Steele, steele@cs.brandeis.edu)
 */
public interface PointerTarget {
	public POS getPOS();

	/** Return a description of the target.  For a <code>Word</code>, this is it's lemma;
	 * for a <code>Synset</code>, it's the concatenated lemma's of its <code>Word</code>s.
	 */
	public String getDescription();

	/** Return the long description of the target.  This is its description, appended by,
	 * if it exists, a dash and it's gloss.
	 */
	public String getLongDescription();

	/** Return the outgoing <code>Pointer</code>s from the target -- those <code>Pointer</code>s
	 * that have this object as their source.
	 */
	public Pointer[] getPointers();

	/** Return the outgoing <code>Pointer</code>s of type <var>type</var>. */
	public Pointer[] getPointers(PointerType type);

	/** Return the targets of the outgoing <code>Pointer</code>s. */
	public PointerTarget[] getTargets();

	/** Return the targets of the outgoing <code>Pointer</code>s that have type <var>type</var>. */
	public PointerTarget[] getTargets(PointerType type);
}
