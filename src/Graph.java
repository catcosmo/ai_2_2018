import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Graph {

    private static Set<RasterNode> rasterNodes = new HashSet<>();

    public static void calculateShortestPathFromSource(RasterNode[] rasterNodes, RasterNode source) {
        fillGraph(rasterNodes);
        source.set_distance((long)0);

        Set<RasterNode> settledRasterNodes = new HashSet<>();
        Set<RasterNode> unsettledRasterNodes = new HashSet<>();

        unsettledRasterNodes.add(source);

        while (unsettledRasterNodes.size() != 0) {
            RasterNode currentRasterNode = getLowestDistanceRasterNode(unsettledRasterNodes);
            unsettledRasterNodes.remove(currentRasterNode);
            for (Map.Entry< RasterNode, Long> adjacencyPair:
                    currentRasterNode.get_adjacentRasterNodes().entrySet()) {
                RasterNode adjacentRasterNode = adjacencyPair.getKey();
                Long edgeWeight = adjacencyPair.getValue();
                //edgeWeight *= -1;
                if (!settledRasterNodes.contains(adjacentRasterNode)) {
                    calculateMinimumDistance(adjacentRasterNode, edgeWeight, currentRasterNode);
                    unsettledRasterNodes.add(adjacentRasterNode);
                }
            }
            settledRasterNodes.add(currentRasterNode);
        }
        return;
    }

    private static void addRasterNode(RasterNode rasterNodeA) {
        rasterNodes.add(rasterNodeA);
    }

    private static void fillGraph(RasterNode[] rasterNodes){
        for (int node = 0; node < rasterNodes.length; node++) {
            addRasterNode(rasterNodes[node]);
        }
    }

    private static RasterNode getLowestDistanceRasterNode(Set < RasterNode > unsettledRasterNodes) {
        RasterNode lowestDistanceRasterNode = null;
        long lowestDistance = Integer.MAX_VALUE;
        for (RasterNode rasterNode: unsettledRasterNodes) {
            long rasterNodeDistance = rasterNode.get_distance();
            if (rasterNodeDistance < lowestDistance) {
                lowestDistance = rasterNodeDistance;
                lowestDistanceRasterNode = rasterNode;
            }
        }
        return lowestDistanceRasterNode;
    }

    private static void calculateMinimumDistance(RasterNode evaluationRasterNode,
                                                 Long edgeWeigh, RasterNode sourceRasterNode) {
        Long sourceDistance = sourceRasterNode.get_distance();
        if (sourceDistance + edgeWeigh < evaluationRasterNode.get_distance()) {
            evaluationRasterNode.set_distance(sourceDistance + edgeWeigh);
            LinkedList<RasterNode> shortestPath = new LinkedList<>(sourceRasterNode.get_shortestPath());
            shortestPath.add(sourceRasterNode);
            evaluationRasterNode.set_shortestPath(shortestPath);
        }
    }
}