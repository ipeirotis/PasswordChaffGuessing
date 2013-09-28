package passwords;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

class PasswordScore implements Comparable<PasswordScore> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(PasswordScore ps) {

		int c = this.score.compareTo(ps.score);
		if (c == 0)
			return this.password.compareTo(ps.password);
		else
			return this.score.compareTo(ps.score);

	}

	Integer	position;
	String	password;
	Double	score;

}

public class EstimateLikelihood {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		int N = 5;
		// Read the training file in memory
		List<String> training = readTextFile("C:\\Dropbox\\workspace\\Passwords\\data\\training_set_passwords.txt");

		// Read the test file in memory
		List<String> test = readTextFile("C:\\Dropbox\\workspace\\Passwords\\data\\panos_test.tsv");

		List<String> alphabet = new ArrayList<String>();
		alphabet.addAll(training);
		alphabet.addAll(test);

		int V = getAlphabetSize(alphabet);
		LanguageModel[] lm = new LanguageModel[N + 1];

		// Build the LM for various n-gram lengths
		for (int n = 1; n <= N; n++) {
			lm[n] = new LanguageModel(V, n);
			for (String s : training) {
				lm[n].addString(s);
			}
		}

		List<String> result1 = listLikelihoods(test, lm, N);
		writeTextFile(result1, "C:\\Dropbox\\workspace\\Passwords\\data\\test-likelihoods.txt");

		List<String> result2 = listPredictions(test, lm);
		writeTextFile(result2, "C:\\Dropbox\\workspace\\Passwords\\data\\test-predictions.txt");

	}

	private static List<String> listLikelihoods(List<String> test, LanguageModel[] lm, int N) {

		// Write the likelihood estimations for the test data
		List<String> result = new ArrayList<String>();

		String header = "Password\tLength";
		for (int n = 1; n <= N; n++) {
			header += "\tLL" + n;
			header += "\tNormLL" + n;
		}
		result.add(header);

		for (String line : test) {

			StringBuffer sb = new StringBuffer();
			String[] passwords = line.split("\t");

			for (int i = 0; i < passwords.length; i++) {
				String s = passwords[i];
				for (int n = 1; n <= N; n++) {

					double prob = -lm[n].getLogProbability(s);
					double p = Math.round(100.0 * prob) / 100.0;
					double normp = Math.round(100.0 * prob / s.length()) / 100.0;
					sb.append("\t" + p + "\t" + normp);
				}
				result.add(sb.toString());
			}
			result.add("-------");
		}

		return result;
	}

	/**
	 * @param test
	 * @param lm
	 * @return
	 */
	private static List<String> listPredictions(List<String> test, LanguageModel[] lm) {

		// Write the likelihood estimations for the test data
		List<String> result = new ArrayList<String>();

		for (String line : test) {
			TreeSet<PasswordScore> order = new TreeSet<PasswordScore>();
			String[] passwords = line.split("\t");

			for (int i = 0; i < passwords.length; i++) {
				String s = passwords[i];
				int n = 4;
				double prob = -lm[n].getLogProbability(s);
				PasswordScore ps = new PasswordScore();
				ps.score = prob;
				ps.password = s;
				ps.position = i;
				order.add(ps);
			}

			StringBuffer sb = new StringBuffer();
			for (PasswordScore ps : order) {
				sb.append(ps.position + "\t");
			}
			result.add(sb.toString());
		}
		return result;
	}

	static int getAlphabetSize(List<String> lines) {
		Set<Character> alphabet = new HashSet<Character>();
		for (String s : lines) {
			char[] chars = s.toCharArray();
			for (Character c : chars) {
				alphabet.add(c);
			}
		}
		return alphabet.size();
	}

	final static Charset	ENCODING	= StandardCharsets.US_ASCII;

	static List<String> readTextFile(String aFileName) throws IOException {

		Path path = Paths.get(aFileName);
		return Files.readAllLines(path, ENCODING);
	}

	static void writeTextFile(List<String> aLines, String aFileName) throws IOException {

		Path path = Paths.get(aFileName);
		Files.write(path, aLines, ENCODING);
	}

}
