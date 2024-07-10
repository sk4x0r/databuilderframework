package com.flipkart.databuilderframework.complextest;

import com.flipkart.databuilderframework.TestBuilderA;
import com.flipkart.databuilderframework.TestBuilderB;
import com.flipkart.databuilderframework.TestBuilderC;
import com.flipkart.databuilderframework.TestBuilderD;
import com.flipkart.databuilderframework.engine.DataBuilderMetadataManager;
import com.flipkart.databuilderframework.engine.ExecutionGraphGenerator;
import com.flipkart.databuilderframework.model.DataFlow;
import com.flipkart.databuilderframework.model.ExecutionGraph;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExampleTest {

    @Test
    public void testBalancedTree() throws Exception {
        DataBuilderMetadataManager dataBuilderMetadataManager = new DataBuilderMetadataManager();
        ExecutionGraphGenerator executionGraphGenerator = new ExecutionGraphGenerator(dataBuilderMetadataManager);
        dataBuilderMetadataManager.register(ImmutableSet.of("A", "B"), "C", "BuilderA", TestBuilderA.class );
        dataBuilderMetadataManager.register(ImmutableSet.of("D", "E"), "F", "BuilderC", TestBuilderB.class );
        dataBuilderMetadataManager.register(ImmutableSet.of("C", "F"), "G", "BuilderD", TestBuilderC.class );
        DataFlow dataFlow = new DataFlow();
        dataFlow.setName("test");
        dataFlow.setTargetData("G");
        ExecutionGraph e = executionGraphGenerator.generateGraph(dataFlow);
        Assertions.assertEquals(2, e.getDependencyHierarchy().size());
        Assertions.assertEquals(2, e.getDependencyHierarchy().get(0).size());
        Assertions.assertEquals(1, e.getDependencyHierarchy().get(1).size());
    }

    @Test
    public void testDiamond() throws Exception {
        DataBuilderMetadataManager dataBuilderMetadataManager = new DataBuilderMetadataManager();
        ExecutionGraphGenerator executionGraphGenerator = new ExecutionGraphGenerator(dataBuilderMetadataManager);
        dataBuilderMetadataManager.register(ImmutableSet.of("A", "B"), "C", "BuilderA", TestBuilderA.class );
        dataBuilderMetadataManager.register(ImmutableSet.of("C", "E"), "F", "BuilderB", TestBuilderB.class );
        dataBuilderMetadataManager.register(ImmutableSet.of("C", "G"), "H", "BuilderC", TestBuilderC.class );
        dataBuilderMetadataManager.register(ImmutableSet.of("F", "H"), "X", "BuilderD", TestBuilderD.class );
        DataFlow dataFlow = new DataFlow();
        dataFlow.setName("test");
        dataFlow.setTargetData("X");
        ExecutionGraph e = executionGraphGenerator.generateGraph(dataFlow);
        Assertions.assertEquals(3, e.getDependencyHierarchy().size());
        Assertions.assertEquals(1, e.getDependencyHierarchy().get(0).size());
        Assertions.assertEquals(2, e.getDependencyHierarchy().get(1).size());
        Assertions.assertEquals(1, e.getDependencyHierarchy().get(2).size());
    }

}
