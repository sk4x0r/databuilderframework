package com.flipkart.databuilderframework;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.databuilderframework.engine.DataBuilderFrameworkException;
import com.flipkart.databuilderframework.engine.DataBuilderMetadataManager;
import com.flipkart.databuilderframework.engine.DataFlowBuilder;
import com.flipkart.databuilderframework.engine.ExecutionGraphGenerator;
import com.flipkart.databuilderframework.model.DataFlow;
import com.flipkart.databuilderframework.model.ExecutionGraph;
import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;



@Slf4j
public class ExecutionGraphGeneratorTest {
    private DataBuilderMetadataManager dataBuilderMetadataManager = new DataBuilderMetadataManager();
    private ExecutionGraphGenerator executionGraphGenerator = new ExecutionGraphGenerator(dataBuilderMetadataManager);

    @BeforeEach
    public void setup() throws Exception {
        dataBuilderMetadataManager.register(ImmutableSet.of("A", "B"), "C", "BuilderA", TestBuilderA.class );
        dataBuilderMetadataManager.register(ImmutableSet.of("C", "D"), "E", "BuilderB", TestBuilderB.class );
        dataBuilderMetadataManager.register(ImmutableSet.of("C", "E"), "F", "BuilderC", TestBuilderC.class );
        dataBuilderMetadataManager.register(ImmutableSet.of("F"),      "G", "BuilderD", TestBuilderD.class );
        dataBuilderMetadataManager.register(ImmutableSet.of("E", "C"), "G", "BuilderE", TestBuilderE.class );
    }

    @Test
    public void testGenerateGraphNoTarget() throws Exception {
        DataFlow dataFlow = new DataFlow();
        dataFlow.setName("test");
        try {
            executionGraphGenerator.generateGraph(dataFlow);
        } catch (DataBuilderFrameworkException e) {
            if(DataBuilderFrameworkException.ErrorCode.NO_TARGET_DATA == e.getErrorCode()) {
                return;
            }
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Unexpected error");
        }
    }

    @Test
    public void testGenerateGraphEmptyTarget() throws Exception {
        DataFlow dataFlow = new DataFlow();
        dataFlow.setName("test");
        dataFlow.setTargetData("");
        try {
            executionGraphGenerator.generateGraph(dataFlow);
        } catch (DataBuilderFrameworkException e) {
            if(DataBuilderFrameworkException.ErrorCode.NO_TARGET_DATA == e.getErrorCode()) {
                return;
            }
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Unexpected error");
        }
    }

    @Test
    public void testGenerateGraphNoExecutors() throws Exception {
        DataFlow dataFlow = new DataFlowBuilder()
                .withMetaDataManager(dataBuilderMetadataManager)
                .withName("test")
                .withTargetData("X")
                .build();
        ExecutionGraph e = dataFlow.getExecutionGraph();
        Assertions.assertTrue(e.getDependencyHierarchy().isEmpty());
    }

    @Test
    public void testGenerate() throws Exception {
        DataFlow dataFlow = new DataFlowBuilder()
                .withMetaDataManager(dataBuilderMetadataManager)
                .withName("test")
                .withTargetData("C")
                .build();
        ExecutionGraph e = dataFlow.getExecutionGraph();
        Assertions.assertFalse(e.getDependencyHierarchy().isEmpty());
        Assertions.assertEquals(1, e.getDependencyHierarchy().size());
        Assertions.assertEquals("BuilderA", e.getDependencyHierarchy().get(0).get(0).getName());
    }

    @Test
    public void testGenerateTwoStep() throws Exception  {
        DataFlow dataFlow = new DataFlowBuilder()
                .withMetaDataManager(dataBuilderMetadataManager)
                .withName("test")
                .withTargetData("E")
                .build();
        ExecutionGraph e = dataFlow.getExecutionGraph();
        Assertions.assertFalse(e.getDependencyHierarchy().isEmpty());
        Assertions.assertEquals(2, e.getDependencyHierarchy().size());
        Assertions.assertEquals("BuilderA", e.getDependencyHierarchy().get(0).get(0).getName());
        Assertions.assertEquals(1, e.getDependencyHierarchy().get(0).size());
        Assertions.assertEquals("BuilderB", e.getDependencyHierarchy().get(1).get(0).getName());
        Assertions.assertEquals(1, e.getDependencyHierarchy().get(1).size());
    }

    @Test
    public void testGenerateInterdependentStep() throws Exception  {
        DataFlow dataFlow = new DataFlowBuilder()
                .withMetaDataManager(dataBuilderMetadataManager)
                .withName("test")
                .withTargetData("F")
                .build();
        ExecutionGraph e = dataFlow.getExecutionGraph();
        Assertions.assertEquals(3, e.getDependencyHierarchy().size());
        Assertions.assertEquals("BuilderA", e.getDependencyHierarchy().get(0).get(0).getName());
        Assertions.assertEquals(1, e.getDependencyHierarchy().get(0).size());
        Assertions.assertEquals("BuilderB", e.getDependencyHierarchy().get(1).get(0).getName());
        Assertions.assertEquals(1, e.getDependencyHierarchy().get(1).size());
        Assertions.assertEquals("BuilderC", e.getDependencyHierarchy().get(2).get(0).getName());
        Assertions.assertEquals(1, e.getDependencyHierarchy().get(2).size());
    }

    @Test
    public void testGenerateInterdependentStepConflict() throws Exception  {
        try {
            DataFlow dataFlow = new DataFlowBuilder()
                    .withMetaDataManager(dataBuilderMetadataManager)
                    .withName("test")
                    .withTargetData("G")
                    .build();
        } catch (DataBuilderFrameworkException e) {
            if(DataBuilderFrameworkException.ErrorCode.BUILDER_RESOLUTION_CONFLICT_FOR_DATA == e.getErrorCode()) {
                return;
            }
        }
        Assertions.fail("A conflict should have come here");
    }

    @Test
    public void testGenerateInterdependentStepConflictNoData() throws Exception  {
        DataFlow dataFlow = new DataFlow();
        dataFlow.setName("test");
        dataFlow.setTargetData("G");
        dataFlow.setResolutionSpecs(Collections.singletonMap("G", "aa"));
        try {
            ExecutionGraph e = executionGraphGenerator.generateGraph(dataFlow);
        } catch (DataBuilderFrameworkException e) {
            if(DataBuilderFrameworkException.ErrorCode.NO_BUILDER_FOR_DATA == e.getErrorCode()) {
                return;
            }
        }
        Assertions.fail("A conflict should have come here");
    }
    @Test
    public void testGenerateInterdependentStepWithResolution() throws Exception  {
        final DataFlow dataFlow = new DataFlowBuilder()
                .withMetaDataManager(dataBuilderMetadataManager)
                .withName("test")
                .withTargetData("G")
                .withResolutionSpec("G", "BuilderE")
                .build();
        ExecutionGraph e = dataFlow.getExecutionGraph();
        Assertions.assertEquals(3, e.getDependencyHierarchy().size());
        Assertions.assertEquals("BuilderA", e.getDependencyHierarchy().get(0).get(0).getName());
        Assertions.assertEquals(1, e.getDependencyHierarchy().get(0).size());
        Assertions.assertEquals("BuilderB", e.getDependencyHierarchy().get(1).get(0).getName());
        Assertions.assertEquals(1, e.getDependencyHierarchy().get(1).size());
        Assertions.assertEquals("BuilderE", e.getDependencyHierarchy().get(2).get(0).getName());
        Assertions.assertEquals(1, e.getDependencyHierarchy().get(2).size());

    }
    @Test
    public void testGenerateInterdependentStepWithResolutionAlt() throws Exception  {
        final DataFlow dataFlow = new DataFlowBuilder()
                                    .withMetaDataManager(dataBuilderMetadataManager)
                                    .withName("test")
                                    .withTargetData("G")
                                    .withResolutionSpec("G", "BuilderD")
                                    .build();
        ExecutionGraph e = dataFlow.getExecutionGraph();
        log.info("{}", new ObjectMapper().writeValueAsString(e));
        Assertions.assertEquals(4, e.getDependencyHierarchy().size());
        Assertions.assertEquals("BuilderA", e.getDependencyHierarchy().get(0).get(0).getName());
        Assertions.assertEquals(1, e.getDependencyHierarchy().get(0).size());
        Assertions.assertEquals("BuilderB", e.getDependencyHierarchy().get(1).get(0).getName());
        Assertions.assertEquals(1, e.getDependencyHierarchy().get(1).size());
        Assertions.assertEquals("BuilderC", e.getDependencyHierarchy().get(2).get(0).getName());
        Assertions.assertEquals(1, e.getDependencyHierarchy().get(2).size());
        Assertions.assertEquals("BuilderD", e.getDependencyHierarchy().get(3).get(0).getName());

    }

}
