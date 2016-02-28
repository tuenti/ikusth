package com.tuenti.ikusth;

import com.sonyericsson.chkbugreport.BugReportModule;
import com.sonyericsson.chkbugreport.Context;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ThreadsGraphGeneratorTest {
    private static final String ANY_NODE = "Node";
    private BugReportModule bugReportModule;
    private ThreadsGraphGenerator threadsGraphGenerator;

    @Before
    public void setUp() {
        bugReportModule = new BugReportModule(new Context());
    }

    @Test
    public void testEmpty() {
        bugReportModule.initThreadsDependencyGraph(0);
        threadsGraphGenerator = new ThreadsGraphGenerator(bugReportModule.getThreadsDependencyGraph());
        assertEquals("{\n" +
                "rankdir=LR;\n" +
                "}\n", threadsGraphGenerator.getGraphvizGraph().genDotString());
    }

    @Test
    public void testOneNode() {
        bugReportModule.initThreadsDependencyGraph(1);
        bugReportModule.addNodeToThreadsDependencyGraph(ANY_NODE);
        threadsGraphGenerator = new ThreadsGraphGenerator(bugReportModule.getThreadsDependencyGraph());
        assertEquals("{\n" +
                "rankdir=LR;\n" +
                "Node[]\n" +
                "}\n", threadsGraphGenerator.getGraphvizGraph().genDotString());
    }

}
