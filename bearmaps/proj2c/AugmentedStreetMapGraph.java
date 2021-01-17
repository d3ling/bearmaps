package bearmaps.proj2c;

import bearmaps.hw4.streetmap.Node;
import bearmaps.hw4.streetmap.StreetMapGraph;
import bearmaps.lab9.MyTrieSet;
import bearmaps.proj2ab.KDTree;
import bearmaps.proj2ab.Point;

import java.util.*;

/**
 * An augmented graph that is more powerful that a standard StreetMapGraph.
 * Specifically, it supports the following additional operations:
 *
 *
 * @author Alan Yao, Josh Hug, ________
 */
public class AugmentedStreetMapGraph extends StreetMapGraph {
    private HashMap<Point, Node> pointToNodeMap;
    private KDTree streetRoutesKD;
    private MyTrieSet locationsTrie;
    private HashMap<String, String> cleanToFullLocationNameMap;
    private HashMap<String, List<Map<String, Object>>> cleanNameToListOfLocationsMap;

    public AugmentedStreetMapGraph(String dbPath) {
        super(dbPath);

        List<Node> nodes = this.getNodes();
        List<Point> points = new ArrayList<>();
        pointToNodeMap = new HashMap<>();
        locationsTrie = new MyTrieSet();
        cleanToFullLocationNameMap = new HashMap<>();
        cleanNameToListOfLocationsMap = new HashMap<>();

        for (Node n : nodes) {
            long id = n.id();
            int num_neighbors = neighbors(id).size();

            /* Set up for the closest method */
            if (num_neighbors > 0) {
                Point p = new Point(n.lon(), n.lat());
                points.add(p);
                pointToNodeMap.put(p, n);
            }

            String location_name = name(id);
            if (location_name != null) {
                /* Set up for the getLocationsByPrefix method */
                String clean_location_name = cleanString(location_name);
                locationsTrie.add(clean_location_name);
                cleanToFullLocationNameMap.put(clean_location_name, location_name);

                /* Set up for the getLocations method */
                HashMap<String, Object> location = new HashMap<>();
                location.put("lat", n.lat());
                location.put("lon", n.lon());
                location.put("name", location_name);
                location.put("id", id);

                List<Map<String, Object>> location_list = cleanNameToListOfLocationsMap.get(clean_location_name);
                if (location_list == null) {
                    location_list = new ArrayList<>();
                }
                location_list.add(location);
                cleanNameToListOfLocationsMap.put(clean_location_name, location_list);
            }
        }

        streetRoutesKD = new KDTree(points);
    }


    /**
     * For Project Part II
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    public long closest(double lon, double lat) {
        Point nearest_p = streetRoutesKD.nearest(lon, lat);
        return pointToNodeMap.get(nearest_p).id();
    }


    /**
     * For Project Part III (gold points)
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public List<String> getLocationsByPrefix(String prefix) {
        String clean_prefix = cleanString(prefix);
        List<String> clean_location_names = locationsTrie.keysWithPrefix(clean_prefix);
        List<String> full_location_names = new ArrayList<>();

        for (String clean_name : clean_location_names) {
            String full_name = cleanToFullLocationNameMap.get(clean_name);
            full_location_names.add(full_name);
        }

        return full_location_names;
    }

    /**
     * For Project Part III (gold points)
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose cleaned name matches the
     * cleaned <code>locationName</code>, and each location is a map of parameters for the Json
     * response as specified: <br>
     * "lat" -> Number, The latitude of the node. <br>
     * "lon" -> Number, The longitude of the node. <br>
     * "name" -> String, The actual name of the node. <br>
     * "id" -> Number, The id of the node. <br>
     */
    public List<Map<String, Object>> getLocations(String locationName) {
        String clean_location_name = cleanString(locationName);
        return cleanNameToListOfLocationsMap.get(clean_location_name);
    }


    /**
     * Useful for Part III. Do not modify.
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    private static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

}
