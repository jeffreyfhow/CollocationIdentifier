public class Bigram implements Comparable {
	
	private Word word1;
	private Word word2;
	
	public Bigram(Word w1, Word w2){
		word1 = w1;
		word2 = w2;
	}

	public boolean hasSameWords(Bigram b) {
		return this.word1.equals(b.word1) && this.word2.equals(b.word2);
	}
	
	/*public int compareWord1(Bigram b){
		return this.word1.compareTo(b.word1);
	}
		
	public int compareWord2(Bigram b){
		return this.word2.compareTo(b.word2);
	}*/

	@Override
	public int compareTo(Object obj) {
		Bigram b = (Bigram)obj;
		int result = this.word1.compareTo(b.word1);
		if(result == 0){
			result = this.word2.compareTo(b.word2);
		}
		return result;
	}

	public Word getWord1() {
		return word1;
	}

	public Word getWord2() {
		return word2;
	}

}
