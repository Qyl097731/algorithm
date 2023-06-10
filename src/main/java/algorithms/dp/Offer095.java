package algorithms.dp;

/**
 * @description LCS
 * @date 2023/6/10 23:55
 * @author: qyl
 */
public class Offer095 {
    String text1, text2;
    int[][] dp;
    String commonString  = "";

    public int longestCommonSubsequence(String text1, String text2) {
        this.text1 = text1;
        this.text2 = text2;
        int n = text1.length ();
        int m = text2.length ();
        dp = new int[n + 1][m + 1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (text1.charAt (i) == text2.charAt (j)) {
                    dp[i + 1][j + 1] = dp[i][j] + 1;
                } else {
                    dp[i + 1][j + 1] = Math.max (dp[i][j + 1], dp[i + 1][j]);
                }
            }
        }
        int i = n - 1, j = m - 1;
        while(i >= 0 && j >= 0){
            if (text1.charAt (i) == text2.charAt(j)){
                commonString = text1.charAt(i) + commonString;
                i--;j--;
            }else if (dp[i][j + 1] > dp[i+1][j]){
                i--;
            }else {
                j--;
            }
        }
        System.out.println (commonString);
        return dp[n][m];
    }

}
