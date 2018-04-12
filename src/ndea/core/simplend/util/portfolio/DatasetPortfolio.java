package ndea.core.simplend.util.portfolio;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DatasetPortfolio {

  private static final File PORTFOLIO_PATH = new File("polychotomous" + File.separator);

  private Map<Integer, File> datasetPortfolio;
  private Map<String, Integer> datasetNameToIndex;

  private Map<String, Integer> datasetClassIndex;

  private int nextIndex = 0;

  public DatasetPortfolio() {
    this.datasetPortfolio = new HashMap<>();
    this.datasetNameToIndex = new HashMap<>();
    this.datasetClassIndex = new HashMap<>();

    for (File subFile : PORTFOLIO_PATH.listFiles()) {
      if (subFile.isFile() && subFile.getName().endsWith(".arff")) {
        this.add(subFile.getName().substring(0, subFile.getName().length() - 5));
      }
    }
  }

  public void add(final String name) {
    this.add(name, -1);
  }

  public void add(final String name, final int classIndex) {
    File datasetFile = new File(PORTFOLIO_PATH.getAbsolutePath() + File.separator + name + ".arff");
    if (!datasetFile.exists()) {
      throw new IllegalArgumentException("Dataset file " + datasetFile.getAbsolutePath() + " does not exist.");
    }
    this.datasetPortfolio.put(this.nextIndex, datasetFile);
    this.datasetNameToIndex.put(name, this.nextIndex);
    this.datasetClassIndex.put(datasetFile.getName(), classIndex);
    this.nextIndex++;
  }

  public List<File> getWholePortfolio() {
    return new LinkedList<>(this.datasetPortfolio.values());
  }

  public List<String> allSimpleNames() {
    List<String> simpleNames = new LinkedList<>(this.datasetNameToIndex.keySet());
    Collections.sort(simpleNames);
    return simpleNames;
  }

  public File get(final String name) {
    return this.datasetPortfolio.get(this.datasetNameToIndex.get(name));
  }

  public File get(final int index) {
    return this.datasetPortfolio.get(index);
  }

  public List<File> get(final int[] index) {
    return Arrays.stream(index).mapToObj(x -> this.datasetPortfolio.get(x)).collect(Collectors.toList());
  }

  public List<File> get(final String[] names) {
    return Arrays.stream(names).map(x -> this.datasetPortfolio.get(this.datasetNameToIndex.get(x))).collect(Collectors.toList());
  }

  public int getClassIndexOfDatasetFile(final String name) {
    return this.datasetClassIndex.get(name);
  }

}
