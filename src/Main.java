import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        String inputFile = "input.txt";
        String outputFile = "output.txt";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            int n = Integer.parseInt(reader.readLine());
            List<String> firstList = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                firstList.add(reader.readLine());
            }
            int m = Integer.parseInt(reader.readLine());
            List<String> secondList = new ArrayList<>();
            for (int i = 0; i < m; i++) {
                secondList.add(reader.readLine());
            }
            reader.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            Map<String, String> pairs = new HashMap<>();
            for (String s1 : firstList) {
                String bestMatch = "";
                double bestSimilarity = 0.0;
                for (String s2 : secondList) {
                    double similarity = calculateStringSimilarity(s1, s2);
                    if (similarity > bestSimilarity) {
                        bestSimilarity = similarity;
                        bestMatch = s2;
                    }
                }
                pairs.put(s1, bestMatch);
            }

            for (int i = 0; i < pairs.values().size(); i++) {
                String value = (String) pairs.values().toArray()[i];
                for (int j = i + 1; j < pairs.values().size(); j++) {
                    String sameValue = (String) pairs.values().toArray()[j];
                    if (value == sameValue) {
                        Set<String> keys = getKeysByValue(pairs, value);
                        String bestKey = null;
                        for (String key : keys) {
                            if (bestKey == null) {
                                bestKey = key;
                            } else {
                                double similarity = calculateStringSimilarity(key, value);
                                double bestSimilarity = calculateStringSimilarity(bestKey, value);
                                if (similarity >= bestSimilarity) {
                                    pairs.put(bestKey, null);
                                    bestKey = key;
                                } else {
                                    pairs.put(key, null);
                                }
                            }
                        }
                    }
                }
            }

            for (String key : firstList) {
                if (pairs.get(key) != null) {
                    writer.write(key + ":" + pairs.get(key));
                } else {
                    writer.write(key + ":?");
                }
                writer.newLine();
                writer.flush();
            }
            for (String value : secondList) {
                if (!pairs.values().contains(value)) {
                    writer.write(value + ":?");
                    writer.newLine();
                    writer.flush();
                }
            }

            System.out.println("Результаты записаны в файл " + outputFile);
            writer.close();

        } catch (IOException e) {
            System.out.println("Ошибка при работе с файлами: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Set<String> getKeysByValue(Map<String, String> pairs, String value) {
        Set<String> keys = new HashSet<>();
        for (String key : pairs.keySet()) {
            if (pairs.get(key) == value) {
                keys.add(key);
            }
        }
        return keys;
    }

    private static double calculateStringSimilarity(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0 || j == 0) {
                    dp[i][j] = 0;
                } else if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        int lcsLength = dp[m][n];
        int maxLength = Math.max(m, n);
        double similarity = (double) lcsLength / maxLength;
        return similarity;
    }
}
