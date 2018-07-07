import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Graph {

    private Set<RasterNode> rasterNodes = new HashSet<>();

    public Graph calculateShortestPathFromSource(Graph graph, RasterNode[] rasterNodes, RasterNode source) {
        fillGraph(rasterNodes);
        source.set_distance(0);

        Set<RasterNode> settledRasterNodes = new HashSet<>();
        Set<RasterNode> unsettledRasterNodes = new HashSet<>();

        unsettledRasterNodes.add(source);

        while (unsettledRasterNodes.size() != 0) {
            RasterNode currentRasterNode = getLowestDistanceRasterNode(unsettledRasterNodes);
            unsettledRasterNodes.remove(currentRasterNode);
            for (Map.Entry< RasterNode, Integer> adjacencyPair:
                    currentRasterNode.get_adjacentRasterNodes().entrySet()) {
                RasterNode adjacentRasterNode = adjacencyPair.getKey();
                Integer edgeWeight = adjacencyPair.getValue();
                if (!settledRasterNodes.contains(adjacentRasterNode)) {
                    calculateMinimumDistance(adjacentRasterNode, edgeWeight, currentRasterNode);
                    unsettledRasterNodes.add(adjacentRasterNode);
                }
            }
            settledRasterNodes.add(currentRasterNode);
        }
        return graph;
    }

    private void addRasterNode(RasterNode rasterNodeA) {
        rasterNodes.add(rasterNodeA);
    }

    private void fillGraph(RasterNode[] rasterNodes){
        for (int node = 0; node < rasterNodes.length; node++) {
            addRasterNode(rasterNodes[node]);
        }
    }

    private static RasterNode getLowestDistanceRasterNode(Set < RasterNode > unsettledRasterNodes) {
        RasterNode lowestDistanceRasterNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (RasterNode rasterNode: unsettledRasterNodes) {
            int rasterNodeDistance = rasterNode.get_distance();
            if (rasterNodeDistance < lowestDistance) {
                lowestDistance = rasterNodeDistance;
                lowestDistanceRasterNode = rasterNode;
            }
        }
        return lowestDistanceRasterNode;
    }

    private static void calculateMinimumDistance(RasterNode evaluationRasterNode,
                                                 Integer edgeWeigh, RasterNode sourceRasterNode) {
        Integer sourceDistance = sourceRasterNode.get_distance();
        if (sourceDistance + edgeWeigh < evaluationRasterNode.get_distance()) {
            evaluationRasterNode.set_distance(sourceDistance + edgeWeigh);
            LinkedList<RasterNode> shortestPath = new LinkedList<>(sourceRasterNode.get_shortestPath());
            shortestPath.add(sourceRasterNode);
            evaluationRasterNode.set_shortestPath(shortestPath);
        }
    }
}