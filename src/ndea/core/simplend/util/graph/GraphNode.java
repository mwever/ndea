package ndea.core.simplend.util.graph;

public class GraphNode<V> {

  private V value;

  public GraphNode() {
    super();
  }

  public GraphNode(final V value) {
    this.value = value;
  }

  public V getValue() {
    return this.value;
  }

}
