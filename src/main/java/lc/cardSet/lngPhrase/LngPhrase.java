package lc.cardSet.lngPhrase;

import java.util.ArrayList;

public class LngPhrase {
	public String iValue;
	public String iTranscription = null;
	public ArrayList<String> iExamples;
	
	public LngPhrase(String val) {
		iValue = val;
		iExamples = new ArrayList<String>();
	}
}
