/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author nikhillo
 * Class that represents a stream of Tokens. All {@link Analyzer} and
 * {@link TokenFilter} instances operate on this to implement their
 * behavior
 */
public class TokenStream implements Iterator<Token>{
	//TODO TokenStream class implementation
	private List<Token> tokens;
	private int index;
	private int elementRemoved;

	public TokenStream() {		
		index = elementRemoved = -1;
		tokens = new ArrayList<Token>();
	}

	public TokenStream(Collection<Token> tokenList) {
		index = elementRemoved = -1;
		tokens = new ArrayList<Token>(tokenList);
	}

	/**
	 * Method that checks if there is any Token left in the stream
	 * with regards to the current pointer.
	 * DOES NOT ADVANCE THE POINTER
	 * @return true if at least one Token exists, false otherwise
	 */
	@Override
	public boolean hasNext() {
		return index < tokens.size() - 1;
	}

	/**
	 * Method to return the next Token in the stream. If a previous
	 * hasNext() call returned true, this method must return a non-null
	 * Token.
	 * If for any reason, it is called at the end of the stream, when all
	 * tokens have already been iterated, return null
	 */
	@Override
	public Token next() {
		if(elementRemoved != -1){
			index = elementRemoved;
			elementRemoved = -1;
		}else
			index++;		
		if(index >= tokens.size())
			return null;
		return tokens.get(index);
	}

	/**
	 * Method to remove the current Token from the stream.
	 * Note that "current" token refers to the Token just returned
	 * by the next method. 
	 * Must thus be NO-OP when at the beginning of the stream or at the end
	 */
	@Override
	public void remove() {
		if(index < 0 || index >= tokens.size())
			return;
		tokens.remove(index);
		elementRemoved = index;
		index = -1;
	}

	/**
	 * Method to reset the stream to bring the iterator back to the beginning
	 * of the stream. Unless the stream has no tokens, hasNext() after calling
	 * reset() must always return true.
	 */
	public void reset() {
		index = -1;
	}

	/**
	 * Method to append the given TokenStream to the end of the current stream
	 * The append must always occur at the end irrespective of where the iterator
	 * currently stands. After appending, the iterator position must be unchanged
	 * Of course this means if the iterator was at the end of the stream and a 
	 * new stream was appended, the iterator hasn't moved but that is no longer
	 * the end of the stream.
	 * @param stream : The stream to be appended
	 */
	public void append(TokenStream stream) {
		if(stream == null)
			return;
		stream.reset();
		while (stream.hasNext()) {
			tokens.add(stream.next());	
		}
	}

	/**
	 * Method to get the current Token from the stream without iteration.
	 * The only difference between this method and {@link TokenStream#next()} is that
	 * the latter moves the stream forward, this one does not.
	 * Calling this method multiple times would not alter the return value of {@link TokenStream#hasNext()}
	 * @return The current {@link Token} if one exists, null if end of stream
	 * has been reached or the current Token was removed
	 */
	public Token getCurrent() {
		if(index == -1 || index >= tokens.size())
			return null;
		return tokens.get(index);
	}

	public Token getNext(){
		if(elementRemoved != -1){
			index = elementRemoved;
			elementRemoved = -1;
		}
		if(index + 1 < tokens.size())
			return tokens.get(index+1);
		return null;

	}

	public Token getRest(){
		int tempIndex=index;
		Token returnToken=new Token();
		if(tempIndex+1 < tokens.size())
		{
			tempIndex++;
			while(tempIndex<tokens.size())
			{
				returnToken.merge(tokens.get(tempIndex));
				tempIndex++;
			}
			return returnToken;
		}
		return null;
	}

	public Token getNextToNext(){

		if(elementRemoved != -1){
			index = elementRemoved;
			elementRemoved = -1;
		}
		if(index + 2 < tokens.size())
			return tokens.get(index+2);
		return null;
	}


	public Token getPrevious(){
		if(index - 1 >-1)
			return tokens.get(index-1);
		return null;
	}

	public void removeNext() {
		//System.out.println("Token being removed is : "+tokens.get(index+1));
		tokens.remove(index+1);
		
	}

}
