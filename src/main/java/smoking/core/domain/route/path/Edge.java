package smoking.core.domain.route.path;

import lombok.Data;

@Data
public class Edge {
    private Node target;
    private double weight; // (거리 또는 페널티가 적용된 가중치)

    public Edge(Node target, double weight) {
        this.target = target;
        this.weight = weight;
    }
}
