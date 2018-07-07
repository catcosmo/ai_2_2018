import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RasterNode {

    private int _startX;
    private int _startY;
    private int _size;
    private int _numberID;
    private int _weight;


    private List<RasterNode> _shortestPath = new LinkedList<>();

    private Integer _distance = Integer.MAX_VALUE;

    Map<RasterNode, Integer> _adjacentRasterNodes = new HashMap<>();

    public void _addDestination(RasterNode destination, int distance) {
        _adjacentRasterNodes.put(destination, distance);
    }

    //fill list of neighbouring rasters
    public static void addAdjacencyLists(RasterNode[] rasterNodes){
        for (int srcNode = 0; srcNode < rasterNodes.length; srcNode++) {
            RasterNode sourceNode = rasterNodes[srcNode];
            for (int neighbour = 0; neighbour < rasterNodes.length; neighbour++) {
                if(Math.abs(rasterNodes[neighbour].get_startX()-sourceNode.get_startX())<=sourceNode.get_size() &&
                        Math.abs(rasterNodes[neighbour].get_startY()-sourceNode.get_startY())<=sourceNode.get_size() ){
                    sourceNode._addDestination(rasterNodes[neighbour],rasterNodes[neighbour]._weight);
                }
            }
        }
    }


    public RasterNode(int _startX, int _startY, int _size, int _numberID, int _weight) {
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

    public int get_weight() {
        return _weight;
    }

    public void set_weight(int _weight) {
        this._weight = _weight;
    }

    public List<RasterNode> get_shortestPath() {
        return _shortestPath;
    }

    public void set_shortestPath(List<RasterNode> _shortestPath) {
        this._shortestPath = _shortestPath;
    }

    public Integer get_distance() {
        return _distance;
    }

    public void set_distance(Integer _distance) {
        this._distance = _distance;
    }

    public Map<RasterNode, Integer> get_adjacentRasterNodes() {
        return _adjacentRasterNodes;
    }

    public void set_adjacentRasterNodes(Map<RasterNode, Integer> _adjacentRasterNodes) {
        this._adjacentRasterNodes = _adjacentRasterNodes;
    }
}