package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FilterChain implements Analyzer {

	private TokenStream stream;
	private List<TokenFilter> filters;

	public FilterChain(TokenStream stream,
			Collection<TokenFilterType> filterTypes) {
		this.stream = stream;
		this.filters = new ArrayList<TokenFilter>();
		for (TokenFilterType filterType : filterTypes) {
			TokenFilter filter = TokenFilterFactory.getInstance()
					.getFilterByType(filterType, stream);
			filters.add(filter);
		}
	}

	@Override
	public boolean increment() throws TokenizerException {
		for (TokenFilter tokenFilter : filters) {
			//if (tokenFilter.getStream().hasNext()) {
				if (filters.indexOf(tokenFilter) == 0 && tokenFilter.getStream().hasNext() && tokenFilter.getStream().getNext()!=null)
					tokenFilter.increment();
				else if(tokenFilter.getStream().getCurrent()==null)
					break;
				else
					tokenFilter.evaluateCurrent();
					
			//}
		}
		return stream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}

}
