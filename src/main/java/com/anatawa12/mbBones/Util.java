package com.anatawa12.mbBones;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Util {
    private Util(){
    }

    public static Iterable<Node> iterator(NodeList childNodes) {
        return () -> new Iterator<Node>() {
            int idx = 0;

            @Override
            public boolean hasNext() {
                return idx != childNodes.getLength();
            }

            @Override
            public Node next() {
                if (!hasNext()) throw new NoSuchElementException();
                return childNodes.item(idx++);
            }
        };
    }
}
