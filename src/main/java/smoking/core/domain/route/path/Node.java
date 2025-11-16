package smoking.core.domain.route.path;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(of = {"latitude", "longitude"}) // A* 알고리즘의 Set 비교를 위해 좌표 기반 equals/hashCode
public class Node implements Comparable<Node> {

    private double latitude;
    private double longitude;

    // A* 알고리즘 비용
    private double gCost = Double.MAX_VALUE; // 출발지로부터의 실제 비용 (페널티 포함)
    private double hCost = 0; // 목적지까지의 예상 비용 (휴리스틱)

    private Node parent = null; // 경로 재구성을 위한 부모 노드
    private List<Edge> edges = new ArrayList<>();

    public Node(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void addEdge(Edge edge) {
        this.edges.add(edge);
    }

    // A*의 fCost (g + h)
    public double getFCost() {
        return gCost + hCost;
    }

    // PriorityQueue 정렬을 위함 (fCost가 낮은 순)
    @Override
    public int compareTo(Node other) {
        return Double.compare(this.getFCost(), other.getFCost());
    }
}
