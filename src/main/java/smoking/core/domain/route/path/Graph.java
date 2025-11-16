package smoking.core.domain.route.path;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Graph {
    private final List<Node> nodes = new ArrayList<>();

    public void addNode(Node node) {
        nodes.add(node);
    }
}
