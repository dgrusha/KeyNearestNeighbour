package knn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

  public static void main(String[] args) throws IOException {
    Scanner scanner = new Scanner(System.in);

    Set<DataWrapper> trainingSet = loadDataFromFile("iris_training.txt");
    Set<DataWrapper> testSet = loadDataFromFile("iris_test.txt");

    System.out.println("Enter the number of nearest neighbors:");
    int k = scanner.nextInt();
    int correctlyClassified = knnAndGetCorrectAnswerCounter(trainingSet, testSet, k);
    System.out.println("Number of correctly classified: " + correctlyClassified);
    System.out.println(
        "Accuracy: " + ((correctlyClassified / (double) testSet.size()) * 100) + "%"
    );
    scanner.nextLine();
    while (true) {
      try {
        System.out.println("Provide a date for the test: ");
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("stop"))
          break;
        String[] temp = input.split(" ");
        double[] values = new double[temp.length];

        for (int i = 0; i < temp.length; i++) {
          values[i] = Double.parseDouble(temp[i]);
        }

        System.out.println("Classification: " + getClassification(trainingSet, values, k));
      } catch (Exception e) {
        e.printStackTrace(System.out);
      }
    }
  }

  private static Set<DataWrapper> loadDataFromFile(String path) throws IOException {
    Set<DataWrapper> trainData = new HashSet<>();

    List<String> lines = Files.readAllLines(Paths.get(path));

    DataWrapper data = new DataWrapper(lines.get(0));
    trainData.add(data);
    int valuesSize = data.getValues().length;

    for (int i = 1; i < lines.size(); i++) {
      data = new DataWrapper(lines.get(i));
      if (data.getValues().length != valuesSize) {
        System.out.println(lines.get(i)
            + " - The number of attributes does not match the trainSet number of attributes!");
      } else {
        trainData.add(data);
      }
    }

    return trainData;
  }

  private static int knnAndGetCorrectAnswerCounter(Set<DataWrapper> trainingSet, Set<DataWrapper> testSet, int k) {
    int counter = 0;
    for (DataWrapper testData : testSet) {
      String species = getClassification(trainingSet, testData.getValues(), k);
      if (species.equals(testData.getSpecies()))
        counter++;
    }
    return counter;
  }

  private static String getClassification(Set<DataWrapper> trainingSet, double[] testData, int k) {
    Map<DataWrapper, Double> distances = new HashMap<>();
    for (DataWrapper trainingData : trainingSet) {
      double distance = getDistanceBetweenTwoPoints(trainingData.getValues(), testData);
      distances.put(trainingData, distance);
    }

    distances = distances
        .entrySet()
        .stream()
        .sorted(Comparator.comparingDouble(Map.Entry::getValue))
        .limit(k)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    List<String> species = distances
        .keySet()
        .stream()
        .map(DataWrapper::getSpecies)
        .collect(Collectors.toList());

    Map<String, Integer> resMap = new HashMap<>();

    int maxCount = 0;
    for (String s : species) {
      if (resMap.containsKey(s)) {
        int count = resMap.get(s) + 1;
        resMap.put(s, count);
        if (count > maxCount) {
          maxCount = count;
        }
      } else {
        resMap.put(s, 1);
        if (maxCount < 1) {
          maxCount = 1;
        }
      }
    }
    int count = maxCount;

    List<String> results = resMap
        .entrySet()
        .stream()
        .filter(e -> e.getValue() == count)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());

    if (results.size() == 1) {
      return results.get(0);
    } else {
      return getResultByTotalDistance(results, distances);
    }
  }

  private static String getResultByTotalDistance(List<String> allSpecies, Map<DataWrapper, Double> distances) {
    double minDistances = Double.MAX_VALUE;
    int minDistancesIndex = 0;
    for (int i = 0; i < allSpecies.size(); i++) {
      String species = allSpecies.get(i);

      double distance = distances
          .entrySet()
          .stream()
          .filter(e -> e.getKey().getSpecies().equals(species))
          .mapToDouble(Map.Entry::getValue)
          .sum();
      if (distance < minDistances) {
        minDistances = distance;
        minDistancesIndex = i;
      }
    }

    return allSpecies.get(minDistancesIndex);
  }

  private static double getDistanceBetweenTwoPoints(double[] firstPoint, double[] secondPoint) {
    if (firstPoint.length != secondPoint.length)
      throw new RuntimeException("training and test number of attributes do not match.");

    double distanceTemp = 0;

    for (int i = 0; i < firstPoint.length; i++) {
      distanceTemp += Math.pow(firstPoint[i] - secondPoint[i], 2);
    }

    return Math.sqrt(distanceTemp);
  }
}
