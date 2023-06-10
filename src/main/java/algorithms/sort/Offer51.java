package algorithms.sort;

import java.util.Arrays;

/**
 * @description 逆序对
 * @date 2023/6/10 21:29
 * @author: qyl
 */
public class Offer51 {
    int[] nums;
    int res = 0;

    public int reversePairs(int[] nums) {
        this.nums = nums;
        mergeSort (0, nums.length - 1);
        return res;
    }

    private void mergeSort(int l, int r) {
        if (l < r) {
            int mid = l + r >> 1;
            mergeSort (l, mid);
            mergeSort (mid + 1, r);
            merge (l, mid, r);
        }
    }

    private void merge(int l, int mid, int r) {
        int[] c = new int[r - l + 1];
        int i = l, j = mid + 1, k = 0;
        while (i <= mid && j <= r) {
            if (nums[i] <= nums[j]) {
                c[k++] = nums[i++];
            } else {
                res += mid - i + 1;
                c[k++] = nums[j++];
            }
        }
        if (i <= mid) System.arraycopy (nums, i, c, k, mid - i + 1);
        if (j <= r) System.arraycopy (nums, j, c, k, r - j + 1);
        System.arraycopy(c, 0, nums, l, r-l+1);
    }

    public static void main(String[] args) {
        Offer51 offer51 = new Offer51 ();
        System.out.println (offer51.reversePairs (new int[]{7, 5, 6, 4}));
        System.out.println (Arrays.toString (offer51.nums));
    }
}
