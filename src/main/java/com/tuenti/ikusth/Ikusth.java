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

import com.sonyericsson.chkbugreport.*;
import org.enoir.graphvizapi.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ikusth extends Plugin implements ExternalPlugin {
    private ThreadsDependencyGraph threadsDependencyGraph;
    private Graphviz graphviz;
    private String graphOutputFile;

    public Ikusth() {
    }

    public Ikusth(Graphviz graphviz) {
        this.graphviz = graphviz;
    }

    @Override
    public void initExternalPlugin(Module module) {
        if (module instanceof BugReportModule) {
            module.addPlugin(this);
        }
    }

    @Override
    public int getPrio() {
        return 100;
    }

    @Override
    public void reset() {
        threadsDependencyGraph = null;
        graphviz = null;
        graphOutputFile = null;
    }

    @Override
    public void load(Module module) {
    }

    @Override
    public void generate(Module module) {
        BugReportModule bugReportModule = (BugReportModule)module;
        this.threadsDependencyGraph = bugReportModule.getThreadsDependencyGraph();

        Graph graph = new Graph("g1", GraphType.DIGRAPH);
        graph.addAttribute(new Attribute("rankdir", "LR"));

        Iterable<String> threadNames = threadsDependencyGraph.getThreadNames();
        Map<String, Node> nodes = new HashMap<String, Node>();
        for (String threadName : threadNames) {
            Node node = new Node(this.sanitizeForDot(threadName));
            nodes.put(this.sanitizeForDot(threadName), node);
            graph.addNode(node);
        }

        Map<String, Iterable<LabeledEdge>> threadDependencyMap = threadsDependencyGraph.getThreadDependencyMap();
        List<String> deadlock = threadsDependencyGraph.getDeadLock();
        for (Map.Entry<String, Iterable<LabeledEdge>> entry : threadDependencyMap.entrySet()) {
            Node node = nodes.get(sanitizeForDot(entry.getKey()));
            for (LabeledEdge labelEdge : entry.getValue()) {
                Node toNode = nodes.get(sanitizeForDot(labelEdge.toName()));
                Edge edge = new Edge(node, toNode);
                edge.addAttribute(new Attribute("label", "\"" + getLabelLastPart(labelEdge) + "\""));
                if (deadlock.contains(entry.getKey()) && deadlock.contains(labelEdge.toName())) {
                    edge.addAttribute(new Attribute("color", "red"));
                }

                graph.addEdge(edge);
            }
        }
        String type = "png";
        graphOutputFile = "/tmp/outEX1."+ type;
        // TODO add random to file name
        File out = new File(graphOutputFile);
        this.writeGraphToFile(getGraphviz().getGraphByteArray(graph, type, "100"), out );

        openBrowserIfNeeded();
    }

    private Graphviz getGraphviz() {
        if (graphviz == null) {
            graphviz = new Graphviz();
        }
        return graphviz;
    }

    private String getLabelLastPart(LabeledEdge labelEdge) {
        String[] fullLabel = labelEdge.label().split("\\.");
        return fullLabel[fullLabel.length - 1];
    }

    private String sanitizeForDot(String input) {
        return input.replace("-","_").replace(" ", "").replace("(","").replace(")","");
    }

    private void openBrowserIfNeeded() {
        if (graphOutputFile != null) {
            try {
                File file = new File(graphOutputFile);
                System.out.println("Launching browser with URI: " + file.toURI());
                java.awt.Desktop.getDesktop().browse(file.toURI());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int writeGraphToFile(byte[] img, File to)
    {
        try {
            FileOutputStream fos = new FileOutputStream(to);
            fos.write(img);
            fos.close();
        } catch (java.io.IOException ioe) {
            // Do nothing
        }
        return 1;
    }

}
