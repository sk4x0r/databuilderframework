package com.flipkart.databuilderframework.speed;

import com.flipkart.databuilderframework.engine.*;
import com.flipkart.databuilderframework.engine.impl.InstantiatingDataBuilderFactory;
import com.flipkart.databuilderframework.model.*;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;

@Slf4j
public class ConcurrencyTest {
    private DataBuilderMetadataManager dataBuilderMetadataManager = new DataBuilderMetadataManager();
    private DataFlowExecutor executor = new MultiThreadedDataFlowExecutor(
                                                new InstantiatingDataBuilderFactory(dataBuilderMetadataManager),
                                                Executors.newFixedThreadPool(10));
    private DataFlowExecutor optimizedMultiThreadedExecutor = new OptimizedMultiThreadedDataFlowExecutor(
            new InstantiatingDataBuilderFactory(dataBuilderMetadataManager),
            Executors.newFixedThreadPool(10));
    private DataFlowExecutor simpleExecutor = new SimpleDataFlowExecutor(
                                                new InstantiatingDataBuilderFactory(dataBuilderMetadataManager));
    private ExecutionGraphGenerator executionGraphGenerator = new ExecutionGraphGenerator(dataBuilderMetadataManager);

    @BeforeEach
    public void setup() throws Exception {
        dataBuilderMetadataManager.register(ImmutableSet.of("REQ"), "A", "BuilderA", ServiceCallerA.class );
        dataBuilderMetadataManager.register(ImmutableSet.of("REQ"), "B", "BuilderB", ServiceCallerB.class );
        dataBuilderMetadataManager.register(ImmutableSet.of("REQ"), "C", "BuilderC", ServiceCallerC.class );
        dataBuilderMetadataManager.register(ImmutableSet.of("REQ"), "D", "BuilderD", ServiceCallerD.class );
        dataBuilderMetadataManager.register(ImmutableSet.of("REQ"), "E", "BuilderE", ServiceCallerE.class );
        dataBuilderMetadataManager.register(ImmutableSet.of("REQ"), "F", "BuilderF", ServiceCallerF.class );
        dataBuilderMetadataManager.register(ImmutableSet.of("REQ"), "G", "BuilderG", ServiceCallerG.class );
        dataBuilderMetadataManager.register(ImmutableSet.of("A", "B", "C", "D", "E", "F", "G"), "RES", "ResponseBuilder", DataCombiner.class );

    }

    @Test
    public void testFunctionality() throws Exception {
        DataFlow dataFlow = new DataFlow();
        dataFlow.setTargetData("RES");
        dataFlow.setExecutionGraph(executionGraphGenerator.generateGraph(dataFlow).deepCopy());
        DataFlowInstance dataFlowInstance = new DataFlowInstance();
        dataFlowInstance.setId("testflow");
        dataFlowInstance.setDataFlow(dataFlow);
        long startTime = System.currentTimeMillis();

        {
            DataDelta dataDelta = new DataDelta(Lists.<Data>newArrayList(new RequestData()));
            DataExecutionResponse response = executor.run(dataFlowInstance, dataDelta);
            Assertions.assertEquals(8, response.getResponses().size());
            Assertions.assertTrue(response.getResponses().containsKey("RES"));
        }
        log.info("{}", System.currentTimeMillis() - startTime);

    }
    @Test
    public void testSpeed() throws Exception {
        DataFlow dataFlow = new DataFlow();
        dataFlow.setTargetData("RES");
        ExecutionGraph executionGraph = executionGraphGenerator.generateGraph(dataFlow);
        dataFlow.setExecutionGraph(executionGraph);

        long mTime = 0 ;
        for(int i = 0; i < 1000; i++) {
            DataFlowInstance dataFlowInstance = new DataFlowInstance();
            dataFlowInstance.setId("testflow");
            dataFlowInstance.setDataFlow(dataFlow);
            DataDelta dataDelta = new DataDelta(Lists.<Data>newArrayList(new RequestData()));
            long startTime = System.currentTimeMillis();
            DataExecutionResponse response = executor.run(dataFlowInstance, dataDelta);
            mTime += (System.currentTimeMillis() - startTime);
            Assertions.assertEquals(8, response.getResponses().size());
            //System.out.println("MT:" + System.currentTimeMillis());
        }
        long sTime = 0;
        for(int i = 0; i < 1000; i++) {
            DataFlowInstance dataFlowInstance = new DataFlowInstance();
            dataFlowInstance.setId("testflow");
            dataFlowInstance.setDataFlow(dataFlow);
            DataDelta dataDelta = new DataDelta(Lists.<Data>newArrayList(new RequestData()));
            long startTime = System.currentTimeMillis();
            DataExecutionResponse response = simpleExecutor.run(dataFlowInstance, dataDelta);
            sTime += (System.currentTimeMillis() - startTime);
            Assertions.assertEquals(8, response.getResponses().size());
            //System.out.println("ST:" + System.currentTimeMillis());
        }
        
        long omTime = 0 ;
        for(int i = 0; i < 1000; i++) {
            DataFlowInstance dataFlowInstance = new DataFlowInstance();
            dataFlowInstance.setId("testflow");
            dataFlowInstance.setDataFlow(dataFlow);
            DataDelta dataDelta = new DataDelta(Lists.<Data>newArrayList(new RequestData()));
            long startTime = System.currentTimeMillis();
            DataExecutionResponse response = optimizedMultiThreadedExecutor.run(dataFlowInstance, dataDelta);
            omTime += (System.currentTimeMillis() - startTime);
            Assertions.assertEquals(8, response.getResponses().size());
            //System.out.println("MT:" + System.currentTimeMillis());
        }
        log.info("OMT: {} MT: {} ST: {}",omTime, mTime, sTime);
    }
}
