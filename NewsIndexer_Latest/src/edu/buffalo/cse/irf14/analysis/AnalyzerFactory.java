/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;

import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo
 * This factory class is responsible for instantiating "chained" {@link Analyzer} instances
 */
public class AnalyzerFactory {

	private static AnalyzerFactory instance;	

	private AnalyzerFactory(){}

	/**
	 * Static method to return an instance of the factory class.
	 * Usually factory classes are defined as singletons, i.e. 
	 * only one instance of the class exists at any instance.
	 * This is usually achieved by defining a private static instance
	 * that is initialized by the "private" constructor.
	 * On the method being called, you return the static instance.
	 * This allows you to reuse expensive objects that you may create
	 * during instantiation
	 * @return An instance of the factory
	 */
	public static AnalyzerFactory getInstance() {
		if(instance == null)
			instance = new AnalyzerFactory();
		return instance;
	}

	/**
	 * Returns a fully constructed and chained {@link Analyzer} instance
	 * for a given {@link FieldNames} field
	 * Note again that the singleton factory instance allows you to reuse
	 * {@link TokenFilter} instances if need be
	 * @param name: The {@link FieldNames} for which the {@link Analyzer}
	 * is requested
	 * @param TokenStream : Stream for which the Analyzer is requested
	 * @return The built {@link Analyzer} instance for an indexable {@link FieldNames}
	 * null otherwise
	 */
	public Analyzer getAnalyzerForField(FieldNames name, TokenStream stream) {
		if(name == FieldNames.AUTHOR)
			return new FilterChain(stream, new ArrayList<TokenFilterType>(){{add(TokenFilterType.SPECIALCHARS);add(TokenFilterType.NUMERIC);}});
		if(name == FieldNames.AUTHORORG)
			return new FilterChain(stream, new ArrayList<TokenFilterType>(){{add(TokenFilterType.NUMERIC);add(TokenFilterType.SPECIALCHARS);}});
		if(name == FieldNames.CATEGORY)
			return new FilterChain(stream, new ArrayList<TokenFilterType>(){{add(TokenFilterType.NUMERIC);add(TokenFilterType.SPECIALCHARS);}});
		if(name == FieldNames.FILEID)
			return new FilterChain(stream, new ArrayList<TokenFilterType>(){{add(TokenFilterType.NUMERIC);add(TokenFilterType.SPECIALCHARS);}});
		if(name == FieldNames.NEWSDATE)
			return new FilterChain(stream, new ArrayList<TokenFilterType>(){{add(TokenFilterType.SPECIALCHARS);}});
		if(name == FieldNames.PLACE)
			return new FilterChain(stream, new ArrayList<TokenFilterType>(){{add(TokenFilterType.NUMERIC);add(TokenFilterType.SPECIALCHARS);}});
		if(name == FieldNames.TITLE)
			return new FilterChain(stream, new ArrayList<TokenFilterType>(){{add(TokenFilterType.NUMERIC);add(TokenFilterType.SPECIALCHARS);}});
		if(name == FieldNames.CONTENT)
			return new FilterChain(stream, new ArrayList<TokenFilterType>(){{
				add(TokenFilterType.STOPWORD); add(TokenFilterType.CAPITALIZATION); 
				add(TokenFilterType.SYMBOL); add(TokenFilterType.SPECIALCHARS);	
				add(TokenFilterType.ACCENT);
				add(TokenFilterType.DATE);		add(TokenFilterType.NUMERIC);
				add(TokenFilterType.STEMMER);
			}});		
		return null;
	}
}
