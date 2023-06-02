package algorithms.math;


/**
 * @description 简单记忆化 多项式求值 秦九韶算法
 * @date 2023/6/2 15:51
 * @author: qyl
 */
public class Polynomial {
    public void calculatePolynomial(long[] array,int x){
        long res = 0;
        if (array != null && array.length > 0) {
            int n = array.length;
            for(int i = 0; i < n ; i++){
                res = res * x + array[i];
            }
        }
        System.out.println (res);
    }

    public static void main(String[] args) {
        Polynomial polynomial = new Polynomial ();
        polynomial.calculatePolynomial (new long[] {3, 0, -2, 0, 1, 7},3);
    }
}
