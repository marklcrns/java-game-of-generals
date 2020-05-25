package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-25
 */
public class Utils {
  private Utils() {
    throw new RuntimeException("You cannot instantiate Utils class");
  }

  public static int getRandomWithExclusion(Random rnd, int start, int end, int... exclude) {
    int random = start + rnd.nextInt(end - start + 1 - exclude.length);
    for (int ex : exclude) {
      if (random < ex) {
        break;
      }
      random++;
    }
    return random;
  }

  // ref: https://stackoverflow.com/a/29172210/11850077
  public static int[] appendToIntArray(int[] arr, int num) {

    int[] newArr = new int[arr.length + 1];

    for (int i = 0; i < arr.length; i++) {
      newArr[i] = arr[i];
    }
    newArr[arr.length] = num;

    return newArr;
  }

  // // ref: https://www.educative.io/edpresso/how-to-generate-random-numbers-in-java
  // public static int getRandomNumber(int start, int end) {
  //   return (int) (Math.random() * (end - start + 1) + start);
  // }

  // public static int getRandomNumber(int start, int end, Integer[] exclusion) {
  //   int randomTile = (int) (Math.random() * (end - start + 1) + start);
  //
  //   if (Arrays.asList(exclusion).contains(randomTile))
  //     randomTile = (int) (Math.random() * (end - start + 1) + start);
  //
  //   return randomTile;
  // }


  // // ref: https://www.techiedelight.com/add-new-elements-to-array-java/
  // public static Integer[] appendToIntegerArray(Integer[] arr, int element) {
  //   List<Integer> list = new ArrayList<>(Arrays.asList(arr));
  //   list.add(element);
  //
  //   return list.toArray(new Integer[0]);
  // }

}
