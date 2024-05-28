import java.util.*;

public class HashListAutocomplete implements Autocompletor {

    private static final int MAX_PREFIX = 10;
    private Map<String, List<Term>> myMap;
    private int mySize;

    public HashListAutocomplete(String[] terms, double[] weights) {

        if (terms == null || weights == null) {
			throw new NullPointerException("One or more arguments null");
		}

		if (terms.length != weights.length) {
			throw new IllegalArgumentException("terms and weights are not the same length");
		}
		initialize(terms,weights);
	}

    @Override
    public List<Term> topMatches(String prefix, int k) {
        if (MAX_PREFIX < prefix.length()) {
            prefix = prefix.substring(0, MAX_PREFIX);
        }
        List<Term> templst = myMap.getOrDefault(prefix, new ArrayList<>());
        int maxlim = Math.min(k, templst.size());
        List<Term> flist = templst.subList(0, maxlim);
        return flist;
    }

    @Override
    public void initialize(String[] terms, double[] weights) {
        myMap = new HashMap<String, List<Term>>();

        for (int a=0; a < terms.length; a++) {
            for (int b = 0; b <= Math.min(MAX_PREFIX,terms[a].length()); b++) {
                myMap.putIfAbsent(terms[a].substring(0,b), new ArrayList<>());
                Term addval = new Term(terms[a], weights[a]);
                myMap.get(terms[a].substring(0, b)).add(addval);
            }
        }

        for (String every : myMap.keySet()) {
			Collections.sort(myMap.get(every), 
            Comparator.comparing(Term::getWeight).reversed());
		}        
    }

    @Override
    public int sizeInBytes() {
        if (mySize == 0) {
            for (String x : myMap.keySet()) 
            {mySize += BYTES_PER_CHAR * x.length();}
            for (List<Term> y : myMap.values()) 
            {for (Term z : y) 
                {mySize += (BYTES_PER_CHAR * z.getWord().length()) + BYTES_PER_DOUBLE;}
            }
        }
        return mySize;
    }
}

