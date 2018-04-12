package ndea.core.simplend.util;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SeedGenerator {

  private Random r;

  public SeedGenerator(final Random mainRandom) {
    this.r = mainRandom;
  }

  public List<Long> generateSeeds(final int numberOfSeeds) {
    return IntStream.range(0, numberOfSeeds).mapToObj(x -> this.r.nextLong()).collect(Collectors.toList());
  }

}
