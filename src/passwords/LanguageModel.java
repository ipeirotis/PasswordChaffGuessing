package passwords;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LanguageModel {
	
	int n; // The size of the ngrams
	int V; // Alphabet size
	int D; // Number of documents
	Map<Integer, Integer> lengthCounts; // To estimate probability of length
	Map<String, Double> counts; // To count raw frequencies of ngrams
	Map<String, Map<Character, Double>> conditionalCounts; // To count conditional frequencies
	
	public LanguageModel(int alphabetSize, int n) {
		this.n=n;
		this.D = 0;
		this.V=alphabetSize+2; //accounting for the prefix/suffix dummy chars
		this.counts = new HashMap<String, Double>();
		this.lengthCounts = new HashMap<Integer, Integer>();
		this.conditionalCounts = new HashMap<String, Map<Character, Double>>();
	}
	
	public void addString(String s) {
		this.D++;
		
		List<Ngram> list = Ngram.getNgrams(s, this.n);
		for (Ngram ngram : list) {
			addNgram(ngram);
		}
		
		int length = s.length();
		if (this.lengthCounts.containsKey(length)) {
			int cnt = this.lengthCounts.get(length);
			this.lengthCounts.put(length, cnt+1);
		} else {
			this.lengthCounts.put(length, 1);
		}
	}
	
	private void addNgram(Ngram ngram) {
		String prefix = ngram.getPrefix(ngram.n-1);
		Character suffix = ngram.getSuffix(1).charAt(0);
		
		double cnt=0;
		if (this.counts.containsKey(prefix)) {
			cnt = this.counts.get(prefix);
		}
		this.counts.put(prefix, cnt+1);
		
		Map<Character, Double> map;
		if (this.conditionalCounts.containsKey(prefix)) {
			map = this.conditionalCounts.get(prefix);
		} else {
			map = new HashMap<Character, Double>();
		}
		double current = 0.0;
		if (map.containsKey(suffix)) {
			current = map.get(suffix);
		} 
		map.put(suffix, current+1.0);
		this.conditionalCounts.put(prefix, map);
	}
	
	//We use http://en.wikipedia.org/wiki/Additive_smoothing (with alpha=1.0)
	private double getConditionalProbability(Character character, String conditional, double alpha) {
		
		double cnt=0;
		if (this.counts.containsKey(conditional)) {
			cnt = this.counts.get(conditional);
		}
		
		double conditionalCnt = 0;
		if (this.conditionalCounts.containsKey(conditional)) {
			Map<Character, Double> map = this.conditionalCounts.get(conditional);
			if (map.containsKey(character)) {
				conditionalCnt = map.get(character);
			} 
		}
		
		return (conditionalCnt+alpha)/(cnt+alpha*V);
	}
	
	//We use http://en.wikipedia.org/wiki/Additive_smoothing (with alpha=1.0)
	private double getLengthProbability(Integer length, double alpha) {
		
		double cnt=0;
		if (this.lengthCounts.containsKey(length)) {
			cnt = this.lengthCounts.get(length);
		}
		
		double v = this.lengthCounts.size();
		
		
		return (cnt+alpha)/(this.D+alpha*v);
	}
	
	
	public double getLogProbability(String s) {
		Double result = 0.0;
		
		List<Ngram> list = Ngram.getNgrams(s, this.n);
		for (Ngram ngram : list) {
			String prefix = ngram.getPrefix(ngram.n-1);
			Character suffix = ngram.getSuffix(1).charAt(0);
			Double prob = getConditionalProbability(suffix, prefix, 1.0);
			//System.out.println(prefix+"/"+suffix+"/"+prob);
			result += Math.log(prob);
		}
		
		result += getLengthProbability(s.length(), 1.0);

		return result;
	}
	
	
	public static void main(String[] args) throws Exception {
		String[] strings = {"abc", "anc", "abc", "abc", "abc", "abc", "abc"};
		
		int V=4;
		int n=2;
		LanguageModel lm = new LanguageModel(V, n);
		
		for (String s: strings) {
			lm.addString(s.toLowerCase());
		}
		
		for (String s: strings) {
			double p = lm.getLogProbability(s);
			System.out.println(s+"\t"+p);
		}
	}
	
}
