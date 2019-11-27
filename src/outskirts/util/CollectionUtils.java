package outskirts.util;

import outskirts.util.function.TriConsumer;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CollectionUtils {

    //populateMap() or asMap() ?
    public static <K, V> Map<K, V> asMap(Map<K, V> dest, Iterable<K> keys, Iterable<V> values) {
        Iterator<V> vIter = values.iterator();

        for (K key : keys) {
            dest.put(key, vIter.next());
        }

        if (vIter.hasNext()) {
            throw new NoSuchElementException();
        }

        return dest;
    }

    /**
     * @param kvs Keys & Values
     */
    public static <K, V> Map<K, V> asMap(Map<Object, Object> dest, Object... kvs) {
        Validate.isTrue(kvs.length % 2 == 0, "Keys and Values must be paired.");

        for (int i = 0;i < kvs.length;) {
            dest.put(kvs[i++], kvs[i++]);
        }

        return (Map<K, V>)dest;
    }

    public static <K, V> Map<K, V> asMap(Object... kvs) {
        return asMap(new HashMap<>(), kvs);
    }


    public static <E> List<E> asList(List<E> dest, E... elements) {
        dest.addAll(Arrays.asList(elements));
        return dest;
    }

    public static float[] toArray(List<Float> list) {
        float[] array = new float[list.size()];
        int i = 0;
        for (Float data : list) {
            array[i++] = data;
        }
        return array;
    }

    //needs hashCode search?, indexOf() ?
    public static boolean contains(Object[] array, Object find) {
        for (Object item : array) {
            if (item.equals(find)) {
                return true;
            }
        }
        return false;
    }

    public static <T> T get(T[] array, Predicate<T> predicate) {
        for (T e : array) {
            if (predicate.test(e)) {
                return e;
            }
        }
        return null;
    }

    public static <T> T[] fill(T[] array, Supplier<T> supplier) {
        for (int i = 0;i < array.length;i++)
            array[i] = supplier.get();
        return array;
    }

    public static <T> T[] subarray(int beginIndex, int endIndex, T[] array) {
        T[] result = (T[]) Array.newInstance(array.getClass().getComponentType(), endIndex - beginIndex);
        System.arraycopy(array, beginIndex, result, 0, result.length);
        return result;
    }

    //this custom should be in library..?
    public static <T> T[] shuffle(T[] array) {
        for (int i = 0;i < array.length;i++) {
            int target = i + Maths.RAND.nextInt(array.length - i);
            swap(array, i, target);
        }
        return array;
    }

    public static <T> T[] swap(T[] array, int i1, int i2) {
        T obj1 = array[i1];
        array[i1] = array[i2];
        array[i2] = obj1;
        return array;
    }

    public static <T> List<T> swap(List<T> list, int i1, int i2) {
        T obj1 = list.get(i1);
        list.set(i1, list.get(i2));
        list.set(i2, obj1);
        return list;
    }

    public static <K, V> String toString(Map<K, V> map, String delimiter, TriConsumer<StringBuilder, K, V> accumulator) {
        StringBuilder sb = new StringBuilder("{");
        int counter = 0;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            accumulator.accept(sb, entry.getKey(), entry.getValue());
            counter++;
            if (counter != map.size()) {
                sb.append(delimiter);
            }
        }
        sb.append("}");
        return sb.toString();
    }


    /**
     * QuickSort is not stable-sort, but not needs extra array-alloc than Arrays.sort(T[])'s MargeSort
     */
    private static class QuickSort {

        private <T> void quickSort(List<T> list, int left, int right, Comparator<T> c) {
            int i = left;
            int j = right;
            T pivot = list.get((left + right) / 2);

            // partition
            do {
                while (c.compare(list.get(i), pivot) < 0) i++;
                while (c.compare(pivot, list.get(j)) < 0) j--;

                if (i <= j) {
                    swap(list, i, j);
                    i++;
                    j--;
                }
            } while (i <= j);

            // recursion
            if (left < j) {
                quickSort(list, left, j, c);
            }
            if (i < right) {
                quickSort(list, i, right, c);
            }
        }
    }
}
