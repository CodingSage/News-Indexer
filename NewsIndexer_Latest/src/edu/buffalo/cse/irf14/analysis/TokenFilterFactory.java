/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import edu.buffalo.cse.irf14.analysis.test.SymbolRuleTest;


/**
 * Factory class for instantiating a given TokenFilter
 * @author nikhillo
 *
 */
public class TokenFilterFactory {
	
	private static TokenFilterFactory instance;
	
	private TokenFilterFactory(){}
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
	public static TokenFilterFactory getInstance() {
		if(instance == null)
			instance = new TokenFilterFactory();
		return instance;
	}
	
	/**
	 * Returns a fully constructed {@link TokenFilter} instance
	 * for a given {@link TokenFilterType} type
	 * @param type: The {@link TokenFilterType} for which the {@link TokenFilter}
	 * is requested
	 * @param stream: The TokenStream instance to be wrapped
	 * @return The built {@link TokenFilter} instance
	 */
	public TokenFilter getFilterByType(TokenFilterType type, TokenStream stream) {
		//TODO : YOU MUST IMPLEMENT THIS METHOD - factory
		if (type == TokenFilterType.SPECIALCHARS) {
			return new SpecialCharacterFilter(stream);
		}
		else if (type == TokenFilterType.SYMBOL) {
			return new SymbolFilter(stream);
		}
		else if (type == TokenFilterType.STOPWORD) {
			return new StopWordFilter(stream);
		}
		else if (type == TokenFilterType.ACCENT) {
			return new AccentFilter(stream);
		}
		else if (type == TokenFilterType.DATE) {
			return new DateFilter(stream);
		}
		else if (type == TokenFilterType.NUMERIC) {
			return new NumberFilter(stream);
		}
		else if (type == TokenFilterType.CAPITALIZATION) {
			return new CapitalizationFilter(stream);
		}
		else if (type == TokenFilterType.STEMMER) {
			return new StemmerFilter(stream);
		}
		return null;
	}
}
