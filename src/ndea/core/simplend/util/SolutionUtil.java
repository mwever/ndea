package ndea.core.simplend.util;

import org.moeaframework.core.Solution;

public class SolutionUtil {

  private static final int DECIMALS_TO_ROUND = 4;

  public static String solutionToString(final Solution solution) {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
      sb.append("Obj" + i + ": " + valueToString(solution.getObjective(i)) + " ");
    }
    sb.append("\t| ");

    for (String att : solution.getAttributes().keySet()) {
      if (att.startsWith("cache")) {
        continue;
      }

      double value;
      try {
        value = (double) solution.getAttribute(att);
        sb.append(att + ": " + valueToString(value) + " ");
      } catch (Exception e) {
        sb.append(att + ": " + solution.getAttribute(att) + " ");
      }
    }
    return sb.toString();
  }

  private static String valueToString(final double value) {
    String valueString = round(value, DECIMALS_TO_ROUND) + "";
    if (valueString.length() < DECIMALS_TO_ROUND + 2) {
      while (valueString.length() < DECIMALS_TO_ROUND + 2) {
        valueString += "0";
      }
    }
    return valueString;
  }

  public static String solutionListToString(final Iterable<Solution> solutionIt) {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (Solution solution : solutionIt) {
      if (first) {
        first = false;
      } else {
        sb.append("\n");
      }
      sb.append(solutionToString(solution));
    }
    return sb.toString();
  }

  private static double round(final double valueToRound, final int decimals) {
    int multiplier = (int) Math.pow(10, decimals);
    double raisedValue = Math.round(valueToRound * multiplier);
    double roundedValue = raisedValue / multiplier;
    return roundedValue;
  }

}
