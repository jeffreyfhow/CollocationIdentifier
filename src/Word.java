public class Word implements Comparable{

	private String lexeme;
	private String partOfSpeech;
	
	public Word(String lex, String pos){
		lexeme = lex;
		partOfSpeech = pos;
	}
	
	public boolean equals(Object obj){
		if(obj == null || this == null || this.getClass() != obj.getClass()){
			return false;
		}
		else{
			Word w = (Word)obj;
			return  this.lexeme.equals(w.lexeme) &&
					this.partOfSpeech.equals(w.partOfSpeech);
		}
	}

	public String getLexeme() {
		return lexeme;
	}

	public String getPartOfSpeech() {
		return partOfSpeech;
	}
	
	public String toString(){
		return lexeme + "["+partOfSpeech+"]";
	}

	@Override
	public int compareTo(Object obj) {
		Word w = (Word)obj;
		return this.toString().compareTo(w.toString());
	}
}
