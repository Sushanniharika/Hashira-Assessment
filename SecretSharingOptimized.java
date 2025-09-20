import java.io.FileReader;
import java.math.BigInteger;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

public class SecretSharingOptimized {

    // Decode y-value given base
    private static BigInteger decodeValue(String value, int base) {
        return new BigInteger(value, base);
    }

    // Lagrange interpolation at x=0 (optimized for constant term only)
    private static BigInteger lagrangeAtZero(List<BigInteger[]> points, int k) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {
            BigInteger xi = points.get(i)[0];
            BigInteger yi = points.get(i)[1];

            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    BigInteger xj = points.get(j)[0];
                    numerator = numerator.multiply(xj.negate());   // (0 - xj)
                    denominator = denominator.multiply(xi.subtract(xj));
                }
            }

            BigInteger term = yi.multiply(numerator).divide(denominator);
            result = result.add(term);
        }

        return result;
    }

    // Parse test case JSON and return secret
    private static BigInteger processTestCase(String fileName) throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject testCase = (JSONObject) parser.parse(new FileReader(fileName));

        JSONObject keys = (JSONObject) testCase.get("keys");
        int k = ((Long) keys.get("k")).intValue();

        List<BigInteger[]> points = new ArrayList<>();
        for (Object key : testCase.keySet()) {
            if (key.equals("keys")) continue;
            JSONObject entry = (JSONObject) testCase.get(key);
            int base = Integer.parseInt((String) entry.get("base"));
            String value = (String) entry.get("value");

            BigInteger y = decodeValue(value, base);
            BigInteger x = new BigInteger(key.toString());
            points.add(new BigInteger[]{x, y});
        }

        return lagrangeAtZero(points.subList(0, k), k);
    }

    public static void main(String[] args) {
        try {
            // Print only the secrets, one per line
            System.out.println(processTestCase("testcase1.json"));
            System.out.println(processTestCase("testcase2.json"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
