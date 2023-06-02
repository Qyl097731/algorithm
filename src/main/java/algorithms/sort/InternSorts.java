package algorithms.sort;

import java.util.Arrays;

/**
 * @description 简单排序汇总
 * @date 2023/6/2 15:07
 * @author: qyl
 */
public class InternSorts {
    /**
     * 统计逆序对的结果
     */
    private static long res = 0;

    /**
     * 选择排序：每轮交换确定一个最终位置
     * 时间复杂度 n^2
     *
     * @param array
     */
    private int[] selectionSort(int[] array) {
        if (array != null && array.length > 0) {
            int n = array.length;
            for (int i = 0; i <= n - 2; i++) {
                int min = i;
                for (int j = i + 1; j <= n - 1; j++) {
                    if (array[j] < array[min]) {
                        min = j;
                    }
                }
                if (min != i) {
                    swap (array, min, i);
                }
            }
        }
        return array;
    }

    /**
     * 冒泡排序:每轮交换确定一个最终位置
     * 时间复杂度 n - n^2
     *
     * @param array
     */
    private int[] bubbleSort(int[] array) {
        if (array != null && array.length > 0) {
            int n = array.length;
            for (int i = 0; i <= n - 2; i++) {
                boolean flag = false;
                for (int j = 0; j <= n - i - 2; j++) {
                    if (array[j] > array[j + 1]) {
                        swap (array, j, j + 1);
                        flag = true;
                    }
                }
                /**
                 * 优化：如果没有交换说明已经有序
                 */
                if (!flag) {
                    break;
                }
            }
        }
        return array;
    }

    /**
     * 归并排序
     * 时间复杂度 n * logn
     * 空间复杂度 n
     *
     * @param array
     */
    private int[] mergeSort(int[] array) {
        if (array != null) {
            int n = array.length;
            if (n > 1) {
                int mid = n / 2;
                merge (mergeSort (Arrays.copyOfRange (array, 0, mid))
                        , mergeSort (Arrays.copyOfRange (array, mid, n))
                        , array);
            }
        }
        return array;
    }

    private void merge(int[] a, int[] b, int[] array) {
        int i = 0, j = 0, k = 0;
        while (i < a.length && j < b.length) {
            if (a[i] <= b[j]) {
                array[k++] = a[i++];
            } else {
                array[k++] = b[j++];
                /**
                 * 统计逆序对
                 */
                res += a.length - i;
            }
        }
        if (i < a.length) {
            System.arraycopy (a, i, array, k, a.length - i);
        } else {
            System.arraycopy (b, j, array, k, b.length - j);
        }
    }

    /**
     * 快排
     * 时间复杂度 nlogn - n^2 （顺序）
     *
     * @param array
     * @param start
     * @param end
     */
    private int[] quickSort(int[] array, int start, int end) {
        if (array != null && start < end) {
            int povid = partition (array, start, end);
            quickSort (array, 0, povid - 1);
            quickSort (array, povid + 1, end);
        }
        return array;
    }

    private int partition(int[] array, int start, int end) {
        int num = array[start];
        while (start < end) {
            while (start < end && array[end] >= num) {
                end--;
            }
            array[start] = array[end];
            while (start < end && array[start] <= num) {
                start++;
            }
            array[end] = array[start];
        }
        array[start] = num;
        return start;
    }

    /**
     * 插入排序
     * 时间复杂度 n - n^2
     * @param array
     */
    private int[] insertSort(int[] array){
        if (array != null) {
            int n = array.length;
            for (int i = 1; i < n; i++) {
                int j = i - 1 , num = array[i];
                while(j >= 0 && array[j] > num){
                    array[j + 1] = array[j];
                    j--;
                }
                array[j + 1] = num;
            }
        }
        return array;
    }

    /**
     * 基数排序
     * 时间复杂度 O(d*(n+r))
     * d是位数，r是基数，
     * n是比较的数目
     * @param array
     * @return
     */
    private int[] radixSort(int[] array){
        if (array != null) {
            int maxNum = Arrays.stream (array).max ().getAsInt ();
            int exp = 1;
            while(maxNum / exp != 0){
                countSort (array,exp);
                exp *= 10;
            }
        }
        return array;
    }

    private void countSort(int[] array,int exp){
        int[] count = new int[10];
        int[] output = new int[array.length];
        for (int num : array) {
            count[num / exp % 10]++;
        }
        for (int i = 1; i < 10; i++) {
            count[i] += count[i-1];
        }
        for (int i = array.length -1; i >= 0; i--) {
            output[count[array[i] / exp % 10] - 1] = array[i];
            count[array[i] / exp % 10]--;
        }
        System.arraycopy (output, 0, array, 0, output.length);
    }

    private void swap(int[] array, int i, int j) {
        if (array != null && array.length > 0) {
            if (i >= 0 && j >= 0 && i <= array.length && j <= array.length) {
                array[i] ^= array[j];
                array[j] ^= array[i];
                array[i] ^= array[j];
            }
        }
    }

    public static void main(String[] args) {
        InternSorts internSorts = new InternSorts ();
        int[] array = {3, 4, 1, 2, 6, 10, 20};
        System.out.println (Arrays.toString (internSorts.selectionSort (array.clone ())));
        System.out.println (Arrays.toString (internSorts.bubbleSort (array.clone ())));
        System.out.println (Arrays.toString (internSorts.mergeSort (array.clone ())));
        System.out.println ("逆序对个数：" + res);
        System.out.println (Arrays.toString (internSorts.quickSort (array.clone (), 0, array.length - 1)));
        System.out.println(Arrays.toString(internSorts.insertSort(array.clone())));
        System.out.println (Arrays.toString(internSorts.radixSort(array.clone())));
    }
}
