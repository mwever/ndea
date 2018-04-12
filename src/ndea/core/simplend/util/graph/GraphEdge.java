package ndea.core.simplend.util.graph;

public class GraphEdge<V> {

  private V a;
  private V b;
  private boolean isDirected = false;

  public GraphEdge(final V a, final V b) {
    super();
    this.a = a;
    this.b = b;
  }

  public GraphEdge(final V a, final V b, final boolean isDirected) {
    this(a, b);
    this.isDirected = false;
  }

  public V getA() {
    return this.a;
  }

  public V getB() {
    return this.b;
  }

  public boolean isDirected() {
    return this.isDirected;
  }

}
