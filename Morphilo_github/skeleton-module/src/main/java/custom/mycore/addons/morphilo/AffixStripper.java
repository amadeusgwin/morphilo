
package custom.mycore.addons.morphilo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AffixStripper {
	
	private static Logger LOGGER = LogManager.getLogger();
	
	private int prefixNumber = 0;
	private int suffixNumber = 0;
	private int stemNumber = 0;
	private String inflection = "";
	private String lemma = "";
	private String compoundType = "";
	private String wordclass = "";
	private String corpusName = "";
	private String corpusBegin = "";
	private String corpusEnd = "";
	private String wordtoken = "";
	private ArrayList<String> suffix = new ArrayList<String>();
	private ArrayList<String> prefix = new ArrayList<String>();
	private ArrayList<String> wordroot = new ArrayList<String>();
	private ArrayList<String> wordbase = new ArrayList<String>();
	private ArrayList<String> wordclassStem = new ArrayList<String>();
	private ArrayList<String> prefixAllomorph = new ArrayList<String>();
	private ArrayList<String> suffixAllomorph = new ArrayList<String>();
	private Map<String, Integer> prefixMorpheme = new HashMap<String,Integer>();
	private Map<String, Integer> suffixMorpheme = new HashMap<String,Integer>();
	private Map<String, Integer> wordstem = new HashMap<String,Integer>();
	
	/*
	 * Constructor calls analyze-method which initiates
	 */
	public AffixStripper(String w)
	{
		this.wordtoken = w;
		analyzeWord();
	}
	
	/*
	 * 
	 */
	public ArrayList<String> getSuffix()
	{
		return suffix;
	}
	
	/*
	 * 
	 */
	private int getPrefixNumber()
	{
		return prefixNumber;
	}
	
	/*
	 * 
	 */
	private int getSuffixNumber()
	{
		return suffixNumber;
	}
	
	/*
	 * 
	 */
	public ArrayList<String> getPrefix()
	{
		return prefix;
	}
	
	/*
	 * 
	 */
	public ArrayList<String> getBase()
	{
		return wordbase;
	}
	
	/*
	 * 
	 */
	public ArrayList<String> getRoot()
	{
		return wordroot;
	}
	
	/*
	 * a word with several stems is a compound
	 */
	private int getStemNumber()
	{
		//TODO: code for guessing stemNumber
		stemNumber = 1;
		return stemNumber;
	}
	
	/*
	 * 
	 */
	private Map<String,Integer> getStem()
	{
		return wordstem;
	}
	
	/*
	 * 
	 */
	public String getLemma()
	{
		//TODO: Einbinden der OED API ?
		// bis dahin gilt Vereinfachung lemma = word - flexion
		return lemma;
	}
	
	/*
	 * 
	 */
	public String getCompositumType()
	{
		//TODO: derzeit noch ungeloestes Problem in NLP
		return compoundType;
	}
	
	/*
	 * 
	 */
	public String getWordClass()
	{
		//TODO: Einbindung eines Taggers
		return wordclass;
	}
	
	/*
	 * 
	 */
	public String getCorpusName()
	{
		// Wird aus Effizienzgruenden in JDOMorphilo eingefuegt
		return corpusName;
	}
	
	/*
	 * 
	 */
	public String getCorpusBegin()
	{
		// Wird aus Effizienzgruenden in JDOMorphilo eingefuegt
		return corpusBegin;
	}
	
	/*
	 * 
	 */
	public String getCorpusEnd()
	{
		// Wird aus Effizienzgruenden in JDOMorphilo eingefuegt
		return corpusEnd;
	}
	
	/*
	 * 
	 */
	public ArrayList<String> getWordClassStem()
	{
		return wordclassStem;
	}
	
	/*
	 * 
	 */
	public ArrayList<String> getPrefixAllomorph()
	{
		return prefixAllomorph;
	}
	
	/*
	 * 
	 */
	public Map<String, Integer> getPrefixMorphem()
	{
		return prefixMorpheme;
	}
	
	/*
	 * 
	 */
	public ArrayList<String> getSuffixAllomorph()
	{
		return suffixAllomorph;
	}
	
	/*
	 * 
	 */
	public Map<String, Integer> getSuffixMorphem()
	{
		return suffixMorpheme;
	}
	
	/*
	 * 
	 */
	public String getInflection()
	{
		return inflection;
	}
	
	/*
	 * 
	 */
	private void analyzeWord()
	{
		//analyze inflection first because it always occurs at the end of a word
		inflection = analyzeInflection(wordtoken);
		lemma = analyzeLemma(wordtoken, inflection);
		analyzePrefix(lemma);
		analyzeSuffix(lemma);
		getAffixPosition(sortOutAffixes(prefixMorpheme), lemma, 0, "prefix");
		getAffixPosition(sortOutAffixes(suffixMorpheme), lemma, 0, "suffix");
		prefixNumber = prefixMorpheme.size();
		suffixNumber = suffixMorpheme.size();
		wordroot = analyzeRoot(prefixMorpheme, suffixMorpheme, getStemNumber());
	}
	
	/* 
	 * 
	 */
	private String analyzeInflection(String wrd)
	{
		String infl = "";
		for (InflectionEnum inflEnum : InflectionEnum.values()) 
		{
            if (wrd.endsWith(inflEnum.toString())) {
                infl = inflEnum.toString();
                //wordtoken = wordtoken.substring(0, wordtoken.length() - inflEnum.toString().length());
            }
        }
		return infl;
	}
	
	/*
	 * Simplification: lemma = wordtoken - inflection
	 */
	private String analyzeLemma(String wrd, String infl)
	{
		wrd = wrd.substring(0, wrd.length() - infl.length());
		return wrd;
	}
	
	/*
	 * 
	 */
	private ArrayList<String> analyzeRoot(Map<String, Integer> pref, Map<String, Integer> suf, int stemNumber)
	{
		ArrayList<String> root = new ArrayList<String>();
		int j = 1; //one root always exists
		// if word is a compound several roots exist
		while (j <= stemNumber)
		{
			j++;
			String rest = lemma;
			
			for (int i=0;i<pref.size();i++)
			{
				for (String s : pref.keySet())
				{
					//if (i == pref.get(s))
					if (rest.length() > s.length() && s.equals(rest.substring(0, s.length())))
					{
						rest = rest.substring(s.length(),rest.length());

					}
				}
			}
			
			for (int i=0;i<suf.size();i++)
			{
				for (String s : suf.keySet())
				{
					//if (i == suf.get(s))
					if (s.length() < rest.length() && (s.equals(rest.substring(rest.length() - s.length(), rest.length()))))
					{
						rest = rest.substring(0, rest.length() - s.length());
					}
				}
			}
			root.add(rest);
		}
		return root;
	}
	
	/*
	 * makes reasonable assumptions on affix tokens based on length
	 * i.e. if a substring is contained in any other substring,
	 * it will be deleted from the hashmap
	 */
	private Map<String, Integer> sortOutAffixes(Map<String, Integer> affix)
	{
		Map<String,Integer> sortedByLengthMap = new TreeMap<String, Integer>(
                new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        int cmp = Integer.compare(s1.length(), s2.length());
                        return cmp != 0 ? cmp : s1.compareTo(s2);
                    }
                }
        );
		sortedByLengthMap.putAll(affix);
		ArrayList<String> al1 = new ArrayList<String>(sortedByLengthMap.keySet());
		ArrayList<String> al2 = al1;
		Collections.reverse(al2);
		for (String s2 : al1)
		{
			for (String s1 : al2)
			if (s1.contains(s2) && s1.length() > s2.length())
			{
				affix.remove(s2);
			}
		}
		return affix;
	}
	
	/*
	 * 
	 */
	private void getAffixPosition(Map<String, Integer> affix, String restword, int pos, String affixtype)
	{
		if (!restword.isEmpty()) //Abbruchbedingung fuer Rekursion
		{
			for (String s : affix.keySet())
			{
				if (restword.startsWith(s) && affixtype.equals("prefix"))
				{
					pos++;
					prefixMorpheme.put(s, pos);
					//prefixAllomorph.add(pos-1, restword.substring(s.length()));
					getAffixPosition(affix, restword.substring(s.length()), pos, affixtype);
				}
				else if (restword.endsWith(s) && affixtype.equals("suffix"))
				{
					pos++;
					suffixMorpheme.put(s, pos);
					//suffixAllomorph.add(pos-1, restword.substring(s.length()));
					getAffixPosition(affix, restword.substring(0, restword.length() - s.length()), pos, affixtype);
				}	
				else
				{
					getAffixPosition(affix, "", pos, affixtype);
				}
			}
		}
	}
	
	/*
	 * 
	 */
	private void analyzePrefix(String restword)
	{
		if (!restword.isEmpty()) //Abbruchbedingung fuer Rekursion
		{
			for (PrefixEnum prefEnum : PrefixEnum.values())
			{
				String s = prefEnum.toString();
				if (restword.startsWith(s))
				{
					prefixMorpheme.put(s, prefixMorpheme.size() + 1);
					//cut off the prefix that is added to the list
					analyzePrefix(restword.substring(s.length()));
				}
				else
				{
					analyzePrefix("");
				}
			}
		}
	}
	
	/*
	 * 
	 */
	private void analyzeSuffix(String restword)
	{
		if (!restword.isEmpty()) //Abbruchbedingung fuer Rekursion
		{
			
			for (SuffixEnum sufEnum : SuffixEnum.values())
			{
				String s = sufEnum.toString();
				if (restword.endsWith(s))
				{
					suffixMorpheme.put(s, suffixMorpheme.size() + 1);
					//suffixAllomorph.add(0, restword.substring(sufEnum.toString().length()));
					//cut off the suffix that is added to the list
					analyzeSuffix(restword.substring(0, restword.length() - s.length()));
				}
				else
				{
					analyzeSuffix("");
				}
			}
		}	
	}
	
}
