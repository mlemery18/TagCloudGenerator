import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Put a short phrase describing the program here.
 *
 * @author Maria Emery
 *
 */
public final class TagCloudGeneratorStandardJava {

    private static final int MINFONT = 11;

    private static final int RANGEFONT = 37;

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private TagCloudGeneratorStandardJava() {
    }

    private static class SortValues
            implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> o1,
                Map.Entry<String, Integer> o2) {
            return (o2.getValue()).compareTo(o1.getValue());
        }
    }

    private static class Alpha
            implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> o1,
                Map.Entry<String, Integer> o2) {
            return (o1.getKey()).compareToIgnoreCase(o2.getKey());
        }
    }

    /**
     *
     * @param out
     * @param title
     * @param n
     */
    private static void headerHTML(PrintWriter out, String title, int n) {
        out.println("<html>");
        out.println("\t<head>");
        out.println("\t\t<title>Top " + n + " words in " + title + "</title>");
        out.println(
                "\t\t<link href=\"doc/tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">");
        out.println("\t</head>");
        out.println("\t<body>");
        out.println("\t\t<h2>Top " + n + " words in " + title + "</h2>");
        out.println("\t\t<hr>");
        out.println("\t\t<div class=\"cdiv\">");
        out.println("\t\t\t<p class=\"cbox\">");
    }

    private static void enderHTML(PrintWriter out) {
        out.println("\t\t\t</p>");
        out.println("\t\t</div>");
        out.println("\t</body>");
        out.println("</html>");
    }

    private static void alphabetizeWords(PrintWriter out,
            Map<String, Integer> counts, int min, int max, int n) {
        List<Map.Entry<String, Integer>> listMap = new LinkedList<Map.Entry<String, Integer>>(
                counts.entrySet());
        Comparator<Map.Entry<String, Integer>> alpha = new Alpha();
        Collections.sort(listMap, alpha);
        counts.clear();
        for (Map.Entry<String, Integer> entry : listMap) {
            counts.put(entry.getKey(), entry.getValue());
        }

        Iterator<Map.Entry<String, Integer>> it = listMap.iterator();
        int i = 0;
        while (it.hasNext() && i < n) {
            Map.Entry<String, Integer> me = it.next();
            out.print("\t\t\t\t<span style=\"cursor:default\" class=\"f");
            int d = max - min;
            if (d == 0) {
                out.print(MINFONT);
            } else {
                out.print(
                        ((RANGEFONT * (me.getValue() - min)) / (d)) + MINFONT);
            }
            out.print("\" title=\"count: = " + me.getValue() + "\">");
            out.println(me.getKey() + "</span>");
            i++;
        }

    }

    private static int getMax(Set<Map.Entry<String, Integer>> mapSet) {
        Iterator<Map.Entry<String, Integer>> it = mapSet.iterator();
        int x = 0;
        while (it.hasNext()) {
            int tempx = it.next().getValue();
            if (x < tempx) {
                x = tempx;
            }
        }
        return x;
    }

    private static int getMin(Set<Map.Entry<String, Integer>> mapSet) {
        Iterator<Map.Entry<String, Integer>> it = mapSet.iterator();
        int x = 0;
        while (it.hasNext()) {
            int tempx = it.next().getValue();
            if (x > tempx) {
                x = tempx;
            }
        }
        return x;
    }

    private static void sortMapValues(PrintWriter out,
            Map<String, Integer> counts, int n) {
        List<Map.Entry<String, Integer>> listMap = new LinkedList<Map.Entry<String, Integer>>(
                counts.entrySet());
        Comparator<Map.Entry<String, Integer>> values = new SortValues();
        Collections.sort(listMap, values);
        counts.clear();
        Iterator<Map.Entry<String, Integer>> it = listMap.iterator();
        int i = 0;
        while (it.hasNext() && i < n) {
            Map.Entry<String, Integer> me = it.next();
            counts.put(me.getKey(), me.getValue());
            i++;
        }

        int max = getMax(counts.entrySet());
        int min = getMin(counts.entrySet());
        alphabetizeWords(out, counts, min, max, n);

    }

    private static String nextWordOrSeparator(String line, int position,
            Set<Character> set) {
        String word = " ";
        int i = position;
        if (!set.contains(line.charAt(position))) {
            while (i < line.length() && !set.contains(line.charAt(i))) {
                i++;
            }
        } else {
            while (i < line.length() && set.contains(line.charAt(i))) {
                i++;
            }
        }
        return line.substring(position, i);
    }

    private static Map<String, Integer> countWords(BufferedReader in,
            Set<Character> set) {
        Map<String, Integer> counts = new HashMap<String, Integer>();
        String line = " ";
        while (line != null) {
            try {

                line = in.readLine();
                if (line != null) {
                    int position = 0;
                    while (position < line.length()) {
                        String word = nextWordOrSeparator(line, position, set)
                                .toLowerCase();
                        if (!set.contains(word.charAt(0))) {
                            if (!counts.containsKey(word)) {
                                counts.put(word, 1);
                            } else {
                                counts.put(word, counts.get(word) + 1);
                            }
                        }
                        position += word.length();
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading the input file");
            }
        }
        return counts;
    }

    /**
     * Generates a set of separators given a string.
     *
     * @param str
     *            a string of the possible separators
     * @return a set of characters of the possible separators
     */
    private static Set<Character> generateSeperatorSet(String str) {
        Set<Character> set = new HashSet<Character>();
        for (int i = 0; i < str.length(); i++) {
            set.add(str.charAt(i));
        }
        return set;
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        final String str = " \t\n\r,-.!?[]';:/()";
        Set<Character> set = generateSeperatorSet(str);
        String inFile = " ", outFile = " ";
        int n = 0;
        BufferedReader input = null;
        PrintWriter output;
        BufferedReader keyboard = new BufferedReader(
                new InputStreamReader(System.in));
        System.out.println("Enter name for an output file: ");
        try {
            outFile = keyboard.readLine();
        } catch (IOException e) {
            System.err.println("Error finding output file");
            return;
        }
        try {
            output = new PrintWriter(new FileWriter(outFile));
        } catch (IOException e) {
            System.err.println("Error opening output file");
            return;
        }
        System.out.println("Enter name for input text file: ");
        try {
            inFile = keyboard.readLine();
        } catch (IOException e) {
            System.err.println("Error finding input file");
        }
        try {
            input = new BufferedReader(new FileReader(inFile));
        } catch (IOException e) {
            System.err.println("Error opening input file.");
        }
        System.out.println("Enter number of words to be generated");
        try {
            n = Integer.parseInt(keyboard.readLine());
        } catch (IOException e) {
            System.err.println("Error reading in the integer");
        }
        Map<String, Integer> map = countWords(input, set);
        headerHTML(output, inFile, n);
        sortMapValues(output, map, n);
        enderHTML(output);
        output.close();

    }
}
