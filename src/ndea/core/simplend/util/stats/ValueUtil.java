package ndea.core.simplend.util.stats;

public class ValueUtil {

  public static String valueToString(final double value, final int decimals) {
    String valueString = round(value, decimals) + "";
    if (valueString.length() < decimals + 2) {
      while (valueString.length() < decimals + 2) {
        valueString += "0";
      }
    }
    return valueString;
  }

  public static double round(final double valueToRound, final int decimals) {
    int multiplier = (int) Math.pow(10, decimals);
    double raisedValue = Math.round(valueToRound * multiplier);
    double roundedValue = raisedValue / multiplier;
    return roundedValue;
  }

}
