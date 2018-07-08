import java.util.*;

public class RasterNode {

    private int _startX;
    private int _startY;
    private int _size;
    private int _numberID;
    private long _weight;
    private long _leveledNeighbourhoodWeight;


    private List<RasterNode> _shortestPath = new LinkedList<>();

    private Long _distance = Long.MAX_VALUE;

    Map<RasterNode, Long> _adjacentRasterNodes = new HashMap<>();

    public void _addDestination(RasterNode destination, long distance) {
        _adjacentRasterNodes.put(destination, distance);
    }

    //fill list of neighbouring rasters
    public static void addAdjacencyLists(RasterNode[] rasterNodes){
        for (int srcNode = 0; srcNode < rasterNodes.length; srcNode++) {
            RasterNode sourceNode = rasterNodes[srcNode];
            for (int neighbour = 0; neighbour < rasterNodes.length; neighbour++) {
                if(Math.abs(rasterNodes[neighbour].get_startX()-sourceNode.get_startX())<=sourceNode.get_size() &&
                        Math.abs(rasterNodes[neighbour].get_startY()-sourceNode.get_startY())<=sourceNode.get_size() &&
                                srcNode!=neighbour){
                    sourceNode._addDestination(rasterNodes[neighbour],rasterNodes[neighbour]._weight);
                }
            }
        }
    }

    public void fillHotAreaNeighbourhoodWeight(RasterNode[] rasterNodes){
        int weightSum=0;
        int weight;
        // for all rasterNodes
        for (int node = 0; node < rasterNodes.length; node++) {
            //iterate over neighbourhood
            Iterator it = rasterNodes[node]._adjacentRasterNodes.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                weight=(int)pair.getValue();
                //weigh immediate neighbours higher than the ones on edge
                if(node%2==0)
                    weight*=0.5;
                else
                    weight*=1;
                //System.out.println(pair.getKey() + " = " + pair.getValue());
                weightSum+=weight;
            }
            weightSum+=rasterNodes[node].get_weight()*3;
            rasterNodes[node].set_leveledNeighbourhoodWeight(weightSum);
        }
    }


    public RasterNode(int _startX, int _startY, int _size, int _numberID, long _weight) {
        this._startX = _startX;
        this._startY = _startY;
        this._size = _size;
        this._numberID = _numberID;
        this._weight = _weight;
    }

    //GETTERS AND SETTERS

    public int get_startX() {
        return _startX;
    }

    public void set_startX(int _startX) {
        this._startX = _startX;
    }

    public int get_startY() {
        return _startY;
    }

    public void set_startY(int _startY) {
        this._startY = _startY;
    }

    public int get_size() {
        return _size;
    }

    public void set_size(int _size) {
        this._size = _size;
    }

    public int get_numberID() {
        return _numberID;
    }

    public void set_numberID(int _numberID) {
        this._numberID = _numberID;
    }

    public long get_weight() {
        return _weight;
    }

    public void set_weight(long _weight) {
        this._weight = _weight;
    }

    public List<RasterNode> get_shortestPath() {
        return _shortestPath;
    }

    public void set_shortestPath(List<RasterNode> _shortestPath) {
        this._shortestPath = _shortestPath;
    }

    public Long get_distance() {
        return _distance;
    }

    public void set_distance(Long _distance) {
        this._distance = _distance;
    }

    public Map<RasterNode, Long> get_adjacentRasterNodes() {
        return _adjacentRasterNodes;
    }

    public void set_adjacentRasterNodes(Map<RasterNode, Long> _adjacentRasterNodes) {
        this._adjacentRasterNodes = _adjacentRasterNodes;
    }

    public long get_leveledNeighbourhoodWeight() {
        return _leveledNeighbourhoodWeight;
    }

    public void set_leveledNeighbourhoodWeight(long _leveledNeighbourhoodWeight) {
        this._leveledNeighbourhoodWeight = _leveledNeighbourhoodWeight;
    }


}