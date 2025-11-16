package smoking.core.domain.route.path;

import org.springframework.stereotype.Component;
import smoking.core.domain.map.DTO.ExternalRouteData;
import smoking.core.domain.smoking.domain.entity.SmokingArea;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PathFinder {

    private static class Node implements Comparable<Node> {
        private final double latitude;
        private final double longitude;
        private double gCost = Double.MAX_VALUE;
        private double hCost = 0;
        private Node parent = null;
        private final List<Edge> edges = new ArrayList<>();

        public Node(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Double.compare(node.latitude, latitude) == 0 && Double.compare(node.longitude, longitude) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(latitude, longitude);
        }

        public double getFCost() { return gCost + hCost; }
        @Override
        public int compareTo(Node other) { return Double.compare(this.getFCost(), other.getFCost()); }
        public void addEdge(Edge edge) { this.edges.add(edge); }

        // Getters/Setters
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public double getGCost() { return gCost; }
        public void setGCost(double gCost) { this.gCost = gCost; }
        public void setHCost(double hCost) { this.hCost = hCost; }
        public Node getParent() { return parent; }
        public void setParent(Node parent) { this.parent = parent; }
        public List<Edge> getEdges() { return edges; }
    }

    private static class Edge {
        private final Node target;
        private double weight;

        public Edge(Node target, double weight) {
            this.target = target;
            this.weight = weight;
        }
        public Node getTarget() { return target; }
        public double getWeight() { return weight; }
        public void setWeight(double weight) { this.weight = weight; }
    }

    private static class Graph {
        private final List<Node> nodes = new ArrayList<>();
        public void addNode(Node node) { nodes.add(node); }
        public List<Node> getNodes() { return nodes; }
    }

    // --- PathFinder 본체 로직 ---

    private static final double SMOKING_AREA_PENALTY = 100.0;
    private static final double SMOKING_AREA_RADIUS_METERS = 30.0;

    public AvoidancePathResult calculateAvoidancePath(
            ExternalRouteData basicRoute,
            List<SmokingArea> avoidAreas
    ) {

        // 1. Google 원본 경로를 그래프로 변환
        Graph graph = createGraphFromPath(basicRoute.getAllPathCoordinates());
        // (수정) 원본 경로의 노드 리스트를 A* 실행 전에 복사해 둡니다.
        List<Node> originalPathNodes = new ArrayList<>(graph.getNodes());

        // 2. 그래프의 간선(Edge)에 흡연 구역 페널티 적용
        applySmokingAreaPenalty(graph, avoidAreas);

        // 3. A* 알고리즘 실행
        List<Node> finalPathNodes = runAStar(graph);

        // 4. 결과를 DTO로 변환
        List<List<Double>> finalPathCoordinates = finalPathNodes.stream()
                .map(node -> List.of(node.getLatitude(), node.getLongitude())) // Google (Lat, Lng)
                .collect(Collectors.toList());

        // 5. (수정) 실제 회피한 구역 목록 찾기
        List<SmokingArea> actualAvoidedAreas = findAvoidedAreas(originalPathNodes, finalPathNodes, avoidAreas);

        // 6. (수정) 음성 안내 생성 (회피 메시지 포함)
        List<AvoidancePathResult.Instruction> instructions =
                generateInstructions(finalPathNodes, basicRoute.getAllInstructions(), actualAvoidedAreas);

        return new AvoidancePathResult(finalPathCoordinates, instructions, actualAvoidedAreas);
    }

    /**
     * 1. Google 경로(좌표 리스트) -> Graph 변환
     */
    private Graph createGraphFromPath(List<List<Double>> path) {
        Graph graph = new Graph();
        Node prevNode = null;

        for (List<Double> coords : path) {
            double lat = coords.get(0); // Google: 위도(lat)가 0번 인덱스
            double lng = coords.get(1); // Google: 경도(lng)가 1번 인덱스

            Node currentNode = new Node(lat, lng);
            graph.addNode(currentNode);

            if (prevNode != null) {
                double distance = calculateHaversineDistance(
                        prevNode.getLatitude(), prevNode.getLongitude(),
                        currentNode.getLatitude(), currentNode.getLongitude()
                );
                prevNode.addEdge(new Edge(currentNode, distance));
                currentNode.addEdge(new Edge(prevNode, distance));
            }
            prevNode = currentNode;
        }
        return graph;
    }

    /**
     * 2. 흡연 구역 페널티 적용
     */
    private void applySmokingAreaPenalty(Graph graph, List<SmokingArea> avoidAreas) {
        if (avoidAreas == null || avoidAreas.isEmpty()) return;

        for (Node node : graph.getNodes()) {
            for (Edge edge : node.getEdges()) {
                double midLat = (node.getLatitude() + edge.getTarget().getLatitude()) / 2.0;
                double midLng = (node.getLongitude() + edge.getTarget().getLongitude()) / 2.0;

                for (SmokingArea area : avoidAreas) {
                    double distToArea = calculateHaversineDistance(
                            midLat, midLng,
                            area.getLatitude().doubleValue(), area.getLongitude().doubleValue()
                    );

                    if (distToArea <= SMOKING_AREA_RADIUS_METERS) {
                        edge.setWeight(edge.getWeight() * SMOKING_AREA_PENALTY);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 3. A* 알고리즘 실행
     */
    private List<Node> runAStar(Graph graph) {
        if (graph.getNodes() == null || graph.getNodes().isEmpty()) {
            return Collections.emptyList();
        }

        Node startNode = graph.getNodes().get(0);
        Node endNode = graph.getNodes().get(graph.getNodes().size() - 1);

        for (Node node : graph.getNodes()) {
            node.setHCost(calculateHaversineDistance(
                    node.getLatitude(), node.getLongitude(),
                    endNode.getLatitude(), endNode.getLongitude()
            ));
        }

        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Node> closedSet = new HashSet<>();

        startNode.setGCost(0);
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll();

            if (currentNode.equals(endNode)) {
                return reconstructPath(currentNode);
            }

            closedSet.add(currentNode);

            for (Edge edge : currentNode.getEdges()) {
                Node neighbor = edge.getTarget();
                if (closedSet.contains(neighbor)) continue;

                double tentativeGCost = currentNode.getGCost() + edge.getWeight();

                if (tentativeGCost < neighbor.getGCost()) {
                    neighbor.setParent(currentNode);
                    neighbor.setGCost(tentativeGCost);

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return Collections.emptyList();
    }

    /**
     * 3-A. A* 경로 재구성
     */
    private List<Node> reconstructPath(Node endNode) {
        List<Node> path = new ArrayList<>();
        Node current = endNode;
        while (current != null) {
            path.add(current);
            current = current.getParent();
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * (수정) 4-B. 음성 안내 생성 (회피 메시지 추가)
     */
    private List<AvoidancePathResult.Instruction> generateInstructions(
            List<Node> finalPath,
            List<String> originalInstructions,
            List<SmokingArea> avoidedAreas
    ) {

        List<AvoidancePathResult.Instruction> newInstructions = new ArrayList<>();

        // 1. 회피에 성공한 경우, 안내 맨 앞에 회피 메시지 추가
        if (!avoidedAreas.isEmpty()) {
            String areaNames = avoidedAreas.stream()
                    .map(SmokingArea::getAddress) // (주소 또는 ID 사용)
                    .collect(Collectors.joining(", "));

            newInstructions.add(new AvoidancePathResult.Instruction(
                    avoidedAreas.size() + "개의 흡연 구역(" + areaNames + ")을 피해 경로를 우회합니다.",
                    finalPath.get(0).getLatitude(),
                    finalPath.get(0).getLongitude()
            ));
        }

        // 2. Google이 제공한 원본 안내(HTML 태그 제거) 추가
        if (originalInstructions != null) {
            for (String text : originalInstructions) {
                newInstructions.add(new AvoidancePathResult.Instruction(
                        text.replaceAll("<[^>]*>", ""), // HTML 태그 제거
                        finalPath.get(0).getLatitude(), // (좌표는 단순화)
                        finalPath.get(0).getLongitude()
                ));
            }
        }

        return newInstructions;
    }

    /**
     * (수정) 4-C. 회피한 구역 목록 찾기 (실제 로직 구현)
     */
    private List<SmokingArea> findAvoidedAreas(
            List<Node> originalPath,
            List<Node> finalPath,
            List<SmokingArea> allAreas
    ) {

        // 1. 원본 경로 근처의 흡연 구역
        Set<SmokingArea> originalNearbyAreas = findNearbyAreas(originalPath, allAreas);

        // 2. 최종 경로 근처의 흡연 구역
        Set<SmokingArea> finalNearbyAreas = findNearbyAreas(finalPath, allAreas);

        // 3. 차집합 (Set Difference): (원본 근처 O) - (최종 근처 X) = 회피된 구역
        originalNearbyAreas.removeAll(finalNearbyAreas);

        return new ArrayList<>(originalNearbyAreas);
    }

    /**
     * (헬퍼) 특정 경로(Node 리스트) 근처의 흡연 구역 Set을 반환
     */
    private Set<SmokingArea> findNearbyAreas(List<Node> path, List<SmokingArea> allAreas) {
        Set<SmokingArea> nearbyAreas = new HashSet<>();
        if (path == null || allAreas == null) return nearbyAreas;

        for (Node node : path) {
            for (SmokingArea area : allAreas) {
                double dist = calculateHaversineDistance(
                        node.getLatitude(), node.getLongitude(),
                        area.getLatitude().doubleValue(), area.getLongitude().doubleValue()
                );
                if (dist <= SMOKING_AREA_RADIUS_METERS) {
                    nearbyAreas.add(area);
                }
            }
        }
        return nearbyAreas;
    }

    /**
     * (유틸리티) Haversine 공식을 사용한 두 좌표 간 거리 계산 (미터 단위)
     */
    private static double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반경 (km)

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c * 1000; // km를 미터로 변환
    }
}