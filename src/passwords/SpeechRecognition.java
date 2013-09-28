package passwords;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SpeechRecognition {

	public static void main(String[] args) throws Exception {

		Path path = Paths.get("C:\\Temp\\11sec.flac");
		byte[] data = Files.readAllBytes(path);
		
		//String key = "AIzaSyCu4OUrWpEPYn7cWyrc3xQ9hmlmTNF0VLA";
		String key = "";
		String pair = generate_pair(16);

		String request = "https://www.google.com/speech-api/full-duplex/v1/up?key="+key+"&pair="+pair+"&lang=en-US&maxAlternatives=20&client=chromium&continuous";
		URL url = new URL(request);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "audio/x-flac; rate=16000;");
		connection.setRequestProperty("Transfer-Encoding", "chunked");
		connection.setRequestProperty("Referer", "http://www.quizz.us/");
		
		connection.setRequestProperty("User-Agent",
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.77 Safari/535.7");
		connection.setConnectTimeout(60000);
		connection.setUseCaches(false);

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.write(data);
		wr.flush();
		wr.close();
		System.out.println("Done Uploading");

		String request2 = "https://www.google.com/speech-api/full-duplex/v1/down?key="+key+"&pair="+pair;
		URL url2 = new URL(request2);
		HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();
		connection2.setRequestMethod("GET");
		connection2.setRequestProperty("Referer", "http://www.quizz.us/");
		connection2.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.77 Safari/535.7");

		BufferedReader in = new BufferedReader(new InputStreamReader(connection2.getInputStream()));
		String decodedString;
		while ((decodedString = in.readLine()) != null) {
			System.out.println(decodedString);
		}
		connection.disconnect();
	}

	private static String generate_pair(int length) {

		String c = "0123456789";
		String s = "";

		for (int i = 0; i < length; i++) {
			int r = (int) (Math.random() * (c.length() - 1));
			s += c.charAt(r);
		}

		return s;
	}

}
