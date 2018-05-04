package com.example.demo.wordnet;

/**
 * <p>Title: JMWN</p>
 * <p>Description: MultiWordNet API</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ITC-Irst</p>
 * @author Christian Girardi
 * @version 1.0
 */


import java.io.*;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import com.example.demo.util.*;


/** A <code>DictionaryDatabase</code> that retrieves objects from the MultiWordNet Mysql Database
 *
 * A <code>MysqlDictionary</code> has an <it>entity cache</it>.  The entity cache is used to resolve multiple
 * temporally contiguous lookups of the same entity to the same object -- for example, successive
 * calls to <code>lookupIndexWord</code> with the same parameters would return the same value
 * (<code>==</code> as well as <code>equals</code>), as would traversal of two <code>Pointer</code>s
 * that shared the same target.  The current implementation uses an LRU cache, so it's possible for
 * two different objects to represent the same entity, if their retrieval is separated by other
 * database operations.  The LRU cache will be replaced by a cache based on WeakHashMap, once
 * JDK 1.2 becomes more widely available.
 *
 * @see DictionaryDatabase
 * @see com.example.demo.util.Cache
 * @see com.example.demo.util.LRUCache
 * @author Oliver Steele, steele@cs.brandeis.edu
 * @version 1.0
 */

public class MysqlDictionary implements DictionaryDatabase {

    private static Connection db;
    private static Properties connectionParameters;
    public static Statement stmt;

    public static String encoding = "Latin1";

    //
    // Constructors
    //

    /** Construct a DictionaryDatabase that retrieves Mysql connection from <code>Properties</code> file conf/multiwordnet.properties.
     * @see DictionaryDatabase

     */

    public MysqlDictionary() {
        try {
            connectionParameters = new Properties();
            connectionParameters.load(new FileInputStream(new File("E:\\projects\\final\\src\\main\\java\\com\\example\\demo\\conf\\multiwordnet.properties")));
        } catch (IOException ioee) {
            System.err.println("Unable to load properties file for MultiWordNet");
        }

        /// connection drivers instance
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            //Class.forName("org.gjt.mm.mysql.Driver").newInstance();
        } catch(ClassNotFoundException E){
            System.err.println("Unable to load driver");
        } catch(IllegalAccessException E){
            System.err.println("Unable to load driver");
        } catch(InstantiationException E){
            System.err.println("Unable to load driver");
        }

        // MultiWordnet db connection
        String  host = connectionParameters.getProperty("MWN_HOSTNAME");
        String user = connectionParameters.getProperty("MWN_USER");
        String passwd = connectionParameters.getProperty("MWN_PASSWD");
        String dbname = connectionParameters.getProperty("MWN_DB");
        Integer cache = new Integer(connectionParameters.getProperty("CACHE_CAPACITY"));
        try {
            DEFAULT_CACHE_CAPACITY = cache.intValue();

            String conn = "jdbc:mysql://" + host + "/" + dbname;
            this.db = DriverManager.getConnection(conn,user,passwd);
            this.stmt = db.createStatement();


            System.err.println("Welcome to the MultiWordNet API\nConnection database ...OK\n");
        } catch (SQLException E) {
            System.out.println("Unable to establish multiwordnet Mysql DB connection on " + host + "(" + user + " - " + passwd + ")");
            E.printStackTrace(System.out);
        }



        //System.err.println("MysqlDictionary(\"" +  fileManager.toString() + "\"");
        //this.db = fileManager;

    }

    //
    // Entity lookup caching
    //

    protected int DEFAULT_CACHE_CAPACITY = 1000;
    protected Cache entityCache = new LRUCache(DEFAULT_CACHE_CAPACITY);

    protected class DatabaseKey {
        POS pos;
        Object key;
        String language;

        DatabaseKey(POS pos, Object key, String language) {
            this.pos = pos;
            this.key = key;
            this.language = language;
        }

        public boolean equals(Object object) {
            return object instanceof DatabaseKey
                    && ((DatabaseKey) object).pos.equals(pos)
                    && ((DatabaseKey) object).key.equals(key)
                    && ((DatabaseKey) object).language.equals(language);
        }

        public int hashCode() {
            return pos.hashCode() ^ key.hashCode();
        }
    }

    /** Set the dictionary's entity cache.
     */
    public void setEntityCache(Cache cache) {
        if (entityCache != cache) {
            entityCache.clear();
            entityCache = cache;
        }
    }

    //
    // Entity retrieval
    //
    public IndexWord getIndexWordAt(POS pos, String lemma, String language) {
        DatabaseKey key = new DatabaseKey(pos, lemma, language);
        IndexWord word = (IndexWord) entityCache.get(key);

        if (word == null) {

            word = IndexWord.parseIndexWord(this, pos, lemma, language);
            entityCache.put(key, word);
        }
        return word;
    }


    protected Synset getSynsetAt(POS pos, String offset, String language) {
        DatabaseKey key = new DatabaseKey(pos, offset, language);
        Synset synset = (Synset) entityCache.get(key);
        if (synset == null) {
            synset = Synset.parseSynset(this, pos, offset,language);
            entityCache.put(key, synset);
        }
        return synset;
    }


    //
    // Lookup functions
    //
    public IndexWord lookupIndexWord(POS pos, String string, String language) {
        DatabaseKey key = new DatabaseKey(pos, string, language);
        IndexWord word = (IndexWord) entityCache.get(key);

        if (word == null) {
            word = getIndexWordAt(pos, string, language);

            if (word != null) {
                entityCache.put(key, word);
            }
        }
        return word;
    }

    public static String encode (String str) {
        if (str != null) {
            try {
                byte[] aa = str.getBytes(encoding);
                str = new String(aa);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            str = "";
        }
        return str;
    }

    public IndexWord[] searchIndexWords(POS pos, String substring, String language) {
        Statement stmt;
        ResultSet rs;
        IndexWord[] idx = null;
        int rows = 0;
        try {
            stmt = db.createStatement();

            rs = stmt.executeQuery("SELECT lemma from " + language + "_index WHERE lemma LIKE \"%" + substring + "%\" && id_" + pos.getKey() + " is not null");


            rs.last();
            rows = rs.getRow();
            //System.err.println(rows + " SELECT lemma from " + language + "_index WHERE lemma LIKE \"%" + substring + "%\" && id_" + pos.getKey() + " is not null");
            if (rows > 0) {
                idx = new IndexWord[rows];
                rs.beforeFirst();

                while (rs.next()) {
                    idx[rs.getRow() - 1] = this.lookupIndexWord(pos, rs.getString("lemma"),language);
                }
            }

        } catch (SQLException E) {
            System.out.println("Connection - query problems with MySql");
            E.printStackTrace(System.out);
        }
        rs = null;
        return idx;
    }


    public Synset[] synsets(POS pos, String language) {
        ResultSet rs;
        Synset[] syns = null;
        String[] ids = null;
        int rows = 0;
        try {
            rs = this.stmt.executeQuery("SELECT id from " + language + "_synset WHERE id LIKE \"" + pos.getKey() + "%\"");

            rs.last();
            rows = rs.getRow();
            if (rows > 0) {
                syns = new Synset[rows];
                ids = new String[rows];
                rs.beforeFirst();
                //rs.previous();
                while (rs.next()) {
                    ids[rs.getRow() -1] = rs.getString("id");
                }

                for (int i=0; i<rows; i++) {
                    syns[i] = this.getSynsetAt(pos, ids[i], language);
                }
            }

        } catch (SQLException E) {
            System.out.println("Connection - query problems with MySql");
            E.printStackTrace(System.out);
        }
        rs = null;

        return syns;
    }

    public Synset[] allSynsetIdsForWord(String word, String language) {
        ResultSet rs;
        Synset[] syns = null;
        String[] ids = null;
        String wordString = " " + word + " ";
        int rows = 0;
        try {

            String sql = "select * from " + language + "_synset where word = \" " + word + " \"";
            rs = this.stmt.executeQuery(sql);

            rs.last();
            rows = rs.getRow();
            if (rows > 0) {
                syns = new Synset[rows];
                ids = new String[rows];
                rs.beforeFirst();
                //rs.previous();
                while (rs.next()) {
                    ids[rs.getRow() -1] = rs.getString("id");
                }

                for (int i=0; i<rows; i++) {
                    char posString = ids[i].charAt(0);
                    POS pos = null;
                    if(posString == 'n') {
                        pos = POS.NOUN;
                    }else if(posString == 'v') {
                        pos = POS.VERB;
                    }else if(posString == 'a') {
                        pos = POS.ADJ;
                    }else if(posString == 'r') {
                        pos = POS.ADV;
                    }
                    syns[i] = this.getSynsetAt(pos, ids[i], language);
                }
            }

        } catch (SQLException E) {
            System.out.println("Connection - query problems with MySql");
            E.printStackTrace(System.out);
        }
        rs = null;

        return syns;
    }

    public String getDomain(String id) {
        ResultSet rs;
        int rows = 0;
        String domain = "";
        try {

            String sql = "select english from semfield where synset = \"" + id + "\"";
            rs = this.stmt.executeQuery(sql);

            rs.last();
            rows = rs.getRow();
            if (rows > 0) {
                rs.beforeFirst();
                //rs.previous();
                while (rs.next()) {
                    //ids[rs.getRow() -1] = rs.getString("id");
                    domain = rs.getString("english");
                }
            }

        }catch (SQLException e) {
            System.out.println("Connection - query problems with MySql");
            e.printStackTrace(System.out);
        }

        return domain;
    }

}
