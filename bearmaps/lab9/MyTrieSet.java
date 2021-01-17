package bearmaps.lab9;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyTrieSet implements TrieSet61B {
    Node root;

    /* Each node represents a character (except for the root, which does not
     * represent any character) and uses isKey to determine whether or not
     * that node represents the end of a key
     * It also has a map to connect to its child nodes by using their respective
     * characters as keys
     * */
    private class Node {
        private char c;
        private boolean isKey;
        private HashMap<Character, Node> map;

        private Node(boolean isKey) {
            this.isKey = isKey;
            map = new HashMap<>();
        }

        private Node(char c, boolean isKey) {
            this.c = c;
            this.isKey = isKey;
            map = new HashMap<>();
        }
    }

    public MyTrieSet() {
        root = new Node(false);
    }

    /** Clears all items out of Trie */
    @Override
    public void clear() {
        root.map = new HashMap<>();
    }

    /** Returns true if the Trie contains KEY, false otherwise */
    @Override
    public boolean contains(String key) {
        if (key == null || key.length() < 1) {
            return false;
        }

        Node curr = root;
        for (int i = 0, n = key.length(); i < n; i += 1) {
            char c = key.charAt(i);
            if (!curr.map.containsKey(c)) {
                return false;
            }
            curr = curr.map.get(c);
        }

        return curr.isKey;
    }

    /** Inserts string KEY into Trie */
    @Override
    public void add(String key) {
        if (key == null || key.length() < 1) {
            return;
        }

        Node curr = root;
        for (int i = 0, n = key.length(); i < n; i++) {
            char c = key.charAt(i);
            if (!curr.map.containsKey(c)) {
                curr.map.put(c, new Node(c, false));
            }
            curr = curr.map.get(c);
        }
        curr.isKey = true;
    }

    /** Returns a list of all words that start with PREFIX */
    @Override
    public List<String> keysWithPrefix(String prefix) {
        List<String> result = new ArrayList<>();

        if (prefix == null || prefix.length() < 1) {
            return result;
        }

        Node curr = root;
        for (int i = 0, n = prefix.length(); i < n; i += 1) {
            char c = prefix.charAt(i);
            if (!curr.map.containsKey(c)) {
                return result;
            }
            curr = curr.map.get(c);
        }

        keysWithPrefixHelper(prefix, result, curr);
        return result;
    }

    private void keysWithPrefixHelper(String s, List<String> sl, Node n) {
        if (n.isKey) {
            sl.add(s);
        }

        for (char c : n.map.keySet()) {
            keysWithPrefixHelper(s + c, sl, n.map.get(c));
        }
    }

    /** Returns the longest prefix of KEY that exists in the Trie
     *  Not required for Lab 9. If you don't implement this, throw an
     *  UnsupportedOperationException.
     */
    @Override
    public String longestPrefixOf(String key) {
        throw new UnsupportedOperationException("longestPrefixOf not supported");
    }
}
