package ndea.core.simplend.util.stats;

import java.io.File;
import java.util.List;

public interface IStatsParser {

  public String parse(List<Double> valueList);

  public void writeStatsToFile(File outFolder, String name, List<Double> valueList);

}
