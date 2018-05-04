/*
 * WordNet-Java
 *
 * Copyright 1998 by Oliver Steele.  You can use this software freely so long as you preserve
 * the copyright notice and this restriction, and label your changes.
 */

package com.example.demo.wordnet;
/** A Pointer encodes a lexical or semantic relationship between MultiWordNet entities.  A lexical
 * relationship holds between Words; a semantic relationship holds between Synsets.  Relationships
 * are <it>directional</it>:  the two roles of a relationship are the <it>source</it> and <it>target</it>.
 * Relationships are <it>typed</it>: the type of a relationship is a {@link PointerType}, and can
 * be retrieved via {@link Pointer#getType getType}.
 *
 * @see PointerType
 * <p>Original class edu.gwu.wordnet.Pointer (JWordnet 1.0 - author: Oliver Steele, steele@cs.brandeis.edu)
 */
public class Pointer {
    /** This class is used to avoid paging in the target before it is required, and to prevent
     * keeping a large portion of the database resident once the target has been queried.
     */
    protected class TargetIndex {
        POS pos;
        String offset;
        String lemma;
                String language;
        TargetIndex(POS pos, String offset, String lemma, String language) {
            this.pos = pos;
            this.offset = offset;
            this.language = language;
                        this.lemma = lemma;
                }
    }

    //
    // Instance variables
    //
    protected MysqlDictionary dictionary;
    protected Synset synset;

    /** The index of this Pointer within the array of Pointer's in the source Synset.
     * Used by <code>equal</code>.
     */

    protected String lemma;
    protected PointerType pointerType;
    protected PointerTarget source;

    /** An index that can be used to retrieve the target.  The first time this is used, it acts as
     * an external key; subsequent uses, in conjunction with FileBackedDictionary's caching mechanism,
     * can be thought of as a weak reference.
     */
    protected TargetIndex targetIndex;

    //
    // Constructor and initialization
    //
    Pointer(MysqlDictionary dictionary, Synset synset, String lemma) {
        this.dictionary = dictionary;
        this.synset = synset;
                this.lemma = lemma;
    }

    Pointer initializeFrom(PointerType type, String id_target, String lemma_target, String language) {
        this.pointerType = type;
                POS pos = POS.lookup(id_target.substring(0,1));

                this.source = resolveTarget(synset, lemma);
                this.targetIndex = new TargetIndex(pos, id_target, lemma_target, language);
                //System.out.println("## Pointer initializeFrom = " + type.getLabel() + " to " + id_target + "(Wsource=" + lemma + ",Wtarget=" +lemma_target +")");

                return this;
             }

        static Pointer parsePointer(MysqlDictionary dictionary, Synset source, String lemma, PointerType type, String id_target, String lemma_target) {
          return new Pointer(dictionary, source, lemma).initializeFrom(type,id_target,lemma_target,source.language);
    }


    //
    // Object methods
    //
    public boolean equals(Object object) {
        return (object instanceof Pointer)
            && ((Pointer) object).source.equals(source)
            && ((Pointer) object).lemma.equals(lemma);
    }

          public String toString() {
        return "[Link #" + pointerType.getLabel() + " " + lemma + " from " + source + "]";
    }

    //
    // Accessors
    //
    public PointerType getType() {
        return pointerType;
    }

    public boolean isLexical() {
        return source instanceof Word;
    }

    //
    // Targets
    //
    protected PointerTarget resolveTarget(Synset synset, String lemma) {
          if (lemma.equals("")) {
            return synset;
          } else {
            return synset.getWord(0);
          }
    }

    public PointerTarget getSource() {
        return source;
    }

        public PointerTarget getTarget() {
          return resolveTarget(dictionary.getSynsetAt(targetIndex.pos, targetIndex.offset, targetIndex.language), targetIndex.lemma);
        }

}
