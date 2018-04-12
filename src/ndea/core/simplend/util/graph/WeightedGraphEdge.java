package ndea.core.simplend.util.graph;

public class WeightedGraphEdge<V> extends GraphEdge<V> implements Comparable<WeightedGraphEdge<V>> {

  private double weight;

  public WeightedGraphEdge(final V a, final V b, final double weight) {
    super(a, b);
    this.weight = weight;
  }

  public WeightedGraphEdge(final V a, final V b, final boolean isDirected, final double weight) {
    super(a, b, isDirected);
    this.weight = weight;
  }

  public double getWeight() {
    return this.weight;
  }

  @Override
  public int compareTo(final WeightedGraphEdge<V> arg0) {
    return Double.compare(this.weight, arg0.weight);
  }

}
