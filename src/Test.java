import ext.sort.*;
import outskirts.util.Maths;
import outskirts.util.StringUtils;
import outskirts.util.logging.Log;
import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Quaternion;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class Test {

    static int i = 0;


    public static void main(String[] args) throws Exception {

        Vector4f axisAngle = new Vector4f(1, 1, 1, 2);


        Quaternion q = Quaternion.fromAxisAngle(axisAngle, null);

        Log.info(q);

        Log.info(Quaternion.toAxisAngle(q, null));

        Log.info(axisAngle.normalize());

    }



    private static void sorttest() {

        int[] arr = genarr(20);//{9, 8, 7, 6, 5, 0, 3, 2, 1, 4};
        Log.info(Arrays.toString(arr));

        Sort sort = new QuickSort();

        sort.sort(arr);

        Log.info(Arrays.toString(arr));
    }

    private static int[] genarr(int len) {
        int[] arr = new int[len];
        for (int i = 0;i < len;i++)
            arr[i] = i;
        for (int i = 0;i < len;i++)
            Sort.swap(arr, i, (int)(Math.random() * len));
        return arr;
    }

    private static void speed(String name, Runnable runnable) {
        int LOOPS = 100;
        //warm up
        runnable.run();
        long s = System.nanoTime();
        for (int i = 0;i < LOOPS;i++) { //1_000_000
            runnable.run();
        }
        long e = System.nanoTime() - s;
        Log.info("'%s' used %sms in %s calls", name, e/1_000_000f, LOOPS);
    }

}
