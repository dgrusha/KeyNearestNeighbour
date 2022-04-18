package knn;

public final class DataWrapper {
  private final String species;
  private final double[] values;

  public DataWrapper(String data) {
    String[] temp = data
        .replaceAll(" +","")
        .replaceAll(",", "\\.")
        .split("\t");

    this.species = temp[temp.length-1];

    this.values = new double[temp.length-1];
    for (int i = 0; i < this.values.length; i++) {
      this.values[i] = Double.parseDouble(temp[i]);
    }
  }

  public DataWrapper(String species, double[] values) {
    this.species = species;
    this.values = values;
  }

  public String getSpecies() {
    return species;
  }

  public double[] getValues() {
    return values.clone();
  }
}
