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

public class Ikusth extends Plugin implements ExternalPlugin {
    private static final String OUTPUT_TYPE = "png";
    private static final String OUTPUT_DPI = "100";

    private Graphviz graphviz;

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
        graphviz = null;
    }

    @Override
    public void load(Module module) {
    }

    @Override
    public void generate(Module module) {
        BugReportModule bugReportModule = (BugReportModule)module;

        ThreadsGraphGenerator threadsGraphGenerator = new ThreadsGraphGenerator(bugReportModule.getThreadsDependencyGraph());
        Graph graph = threadsGraphGenerator.getGraphvizGraph();

        byte[] img = getGraphviz().getGraphByteArray(graph, OUTPUT_TYPE, OUTPUT_DPI);
        String graphOutputPathName = (new ThreadsGraphOutputWriter(img, OUTPUT_TYPE)).run();
        openOutputImageInBrowser(graphOutputPathName);
    }


    private Graphviz getGraphviz() {
        if (graphviz == null) {
            graphviz = new Graphviz();
        }
        return graphviz;
    }

    private void openOutputImageInBrowser(String graphOutputPathName) {
        try {
            File file = new File(graphOutputPathName);
            System.out.println("Launching browser with URI: " + file.toURI());
            java.awt.Desktop.getDesktop().browse(file.toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
