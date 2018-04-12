package ndea.core.simplend.util.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Graph<V, E> {

  private Set<V> nodes;
  private Set<E> edges;

  public Graph() {
    super();
    this.nodes = new HashSet<>();
    this.edges = new HashSet<>();
  }

  public Set<V> getNodes() {
    return this.nodes;
  }

  public boolean addNode(final V newNode) {
    return this.nodes.add(newNode);
  }

  public boolean addAllNodes(final Collection<V> newNodes) {
    return this.nodes.addAll(newNodes);
  }

  public Set<E> getEdges() {
    return this.edges;
  }

  public boolean addEdge(final E newEdge) {
    return this.edges.add(newEdge);
  }

  public boolean addAllEdges(final Collection<E> newEdges) {
    return this.edges.addAll(newEdges);
  }

}
