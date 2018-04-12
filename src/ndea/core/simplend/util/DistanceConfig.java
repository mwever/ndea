package ndea.core.simplend.util;

public class DistanceConfig {

  public static final double WEIGHT_LOWER_BOUND = 0.0;
  public static final double WEIGHT_UPPER_BOUND = 1.0;

  public static int getNDGeneStringLength(final int numberOfClasses) {
    return (numberOfClasses * numberOfClasses + numberOfClasses) / 2 - 1;
  }

  public static int getNumberOfClasses(final int geneStringLength) {
    int numberOfClasses = (int) (-0.5 + Math.sqrt(0.25 + 2 * (geneStringLength + 1)));
    return numberOfClasses;
  }

}
