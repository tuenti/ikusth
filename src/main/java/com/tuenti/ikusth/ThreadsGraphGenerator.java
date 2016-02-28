/*
 * Copyright (C) 2016 Tuenti Technologies
 *
 * This file is part of Ikusth.
 *
 * ChkBugReport is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * ChkBugReport is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ChkBugReport.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.tuenti.ikusth;

import com.sonyericsson.chkbugreport.LabeledEdge;
import com.sonyericsson.chkbugreport.ThreadsDependencyGraph;
import org.enoir.graphvizapi.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ThreadsGraphGenerator {
    private final ThreadsDependencyGraph threadsDependencyGraph;

    public ThreadsGraphGenerator(ThreadsDependencyGraph threadsDependencyGraph) {
        this.threadsDependencyGraph = threadsDependencyGraph;
    }

    public Graph getGraphvizGraph() {
        Graph graph = new Graph("g1", GraphType.DIGRAPH);
        graph.addAttribute(new Attribute("rankdir", "LR"));

        Map<String, Node> nodes = addNodesToGraph(threadsDependencyGraph, graph);
        Map<String, Iterable<LabeledEdge>> threadDependencyMap = threadsDependencyGraph.getThreadDependencyMap();

        List<String> deadlock = threadsDependencyGraph.getDeadLock();
        for (Map.Entry<String, Iterable<LabeledEdge>> entry : threadDependencyMap.entrySet()) {
            Node node = nodes.get(sanitizeForDot(entry.getKey()));
            for (LabeledEdge labelEdge : entry.getValue()) {
                Node toNode = nodes.get(sanitizeForDot(labelEdge.toName()));
                Edge edge = new Edge(node, toNode);
                addLabelToEdge(labelEdge, edge);
                if (deadlock.contains(entry.getKey()) && deadlock.contains(labelEdge.toName())) {
                    markEdgeAsRed(edge);
                }

                graph.addEdge(edge);
            }
        }
        return graph;
    }


    private void addLabelToEdge(LabeledEdge labelEdge, Edge edge) {
        edge.addAttribute(new Attribute("label", "\"" + getLabelLastPart(labelEdge) + "\""));
    }

    private void markEdgeAsRed(Edge edge) {
        edge.addAttribute(new Attribute("color", "red"));
    }

    private Map<String, Node> addNodesToGraph(ThreadsDependencyGraph threadsDependencyGraph, Graph graph) {
        Iterable<String> threadNames = threadsDependencyGraph.getThreadNames();
        Map<String, Node> nodes = new HashMap<String, Node>();
        for (String threadName : threadNames) {
            Node node = new Node(this.sanitizeForDot(threadName));
            nodes.put(this.sanitizeForDot(threadName), node);
            graph.addNode(node);
        }
        return nodes;
    }

    /**
     * Adapts input String to something suitable for GraphViz node name.
     * @param input
     * @return String
     */
    private String sanitizeForDot(String input) {
        return input.replace("-","_").replace(" ", "").replace("(","").replace(")","");
    }

    private String getLabelLastPart(LabeledEdge labelEdge) {
        String[] fullLabel = labelEdge.label().split("\\.");
        return fullLabel[fullLabel.length - 1];
    }
}
