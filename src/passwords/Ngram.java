package passwords;

import java.util.ArrayList;
import java.util.List;

public class Ngram {

	String chars;
	int n;
	
	public static char START_CHAR = '\u0391';
	public static char END_CHAR = '\u03A9';

	
	public Ngram(String s, int n, int k) {
		
		String prefix = "";
		String suffix = "";
		for (int i=0; i<n; i++) {
			prefix += START_CHAR;
			suffix += END_CHAR;
		}
		s = prefix+s+suffix;
		
		this.chars = s.substring(k, k+n);
		this.n = this.chars.length();
		
		assert(this.n == n);
		
	}
	
	public String getPrefix(int k) {
		return this.chars.substring(0, k);
	}
	
	public String getSuffix(int k) {
		return this.chars.substring(this.n-k, this.n);
	}
	
	public static List<Ngram> getNgrams(String s, int n) {
		List<Ngram> result = new ArrayList<Ngram>();
		for (int i=1; i<s.length()+n; i++) {
			Ngram p = new Ngram(s, n, i);
			result.add(p);
		}
		

		return result;
	}
	
	public String toString() {
		return "{n:"+this.n+" s:"+this.chars+"}";
	}
	
	public static void main(String[] args) throws Exception {
		String s = "Panos";
		int n=10;
		List<Ngram> p = Ngram.getNgrams(s, n);
		for (Ngram f: p) {
			System.out.println(f.toString());
		}
	}
	
}
