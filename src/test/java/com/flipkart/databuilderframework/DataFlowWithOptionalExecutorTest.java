package com.flipkart.databuilderframework;

import com.flipkart.databuilderframework.engine.*;
import com.flipkart.databuilderframework.engine.impl.InstantiatingDataBuilderFactory;
import com.flipkart.databuilderframework.model.*;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;



@Slf4j
public class DataFlowWithOptionalExecutorTest {
    private static class TestListener implements DataBuilderExecutionListener {

        @Override
        public void preProcessing(DataFlowInstance dataFlowInstance,
                                  DataDelta dataDelta) throws Exception {
            log.info("Being called for: {}", dataFlowInstance.getId());
        }

        @Override
        public void beforeExecute(DataBuilderContext builderContext,
        						  DataFlowInstance dataFlowInstance,
                                  DataBuilderMeta builderToBeApplied,
                                  DataDelta dataDelta, Map<String, Data> prevResponses) throws Exception {
            log.info("{} called for: {}", builderToBeApplied.getName(), dataFlowInstance.getId());
        }

        @Override
        public void afterExecute(DataBuilderContext builderContext,
        						 DataFlowInstance dataFlowInstance,
                                 DataBuilderMeta builderToBeApplied,
                                 DataDelta dataDelta, Map<String, Data> prevResponses, Data currentResponse) throws Exception {
            log.info("{} called for: {}", builderToBeApplied.getName(), dataFlowInstance.getId());
        }

        @Override
        public void afterException(DataBuilderContext builderContext,
        						   DataFlowInstance dataFlowInstance,
                                   DataBuilderMeta builderToBeApplied,
                                   DataDelta dataDelta,
                                   Map<String, Data> prevResponses, Throwable frameworkException) throws Exception {
            log.info("{} called for: {}", builderToBeApplied.getName(), dataFlowInstance.getId());
        }

        @Override
        public void postProcessing(DataFlowInstance dataFlowInstance,
                                   DataDelta dataDelta, DataExecutionResponse response,
                                   Throwable frameworkException) throws Exception {
            log.info("Being called for: {}", dataFlowInstance.getId());
        }
    }
    private static class TestListenerBeforeExecutionError implements DataBuilderExecutionListener {

        @Override
        public void preProcessing(DataFlowInstance dataFlowInstance,
                                  DataDelta dataDelta) throws Exception {
            log.info("Being called for: {}", dataFlowInstance.getId());
        }

        @Override
        public void beforeExecute(DataBuilderContext builderContext,
        						  DataFlowInstance dataFlowInstance,
                                  DataBuilderMeta builderToBeApplied,
                                  DataDelta dataDelta, Map<String, Data> prevResponses) throws Exception {
            //System.out.println(builderToBeApplied.getName() + " being called for: " + dataFlowInstance.getId());
            throw new Exception("Blah blah");
        }

        @Override
        public void afterExecute(DataBuilderContext builderContext,
        						 DataFlowInstance dataFlowInstance,
                                 DataBuilderMeta builderToBeApplied,
                                 DataDelta dataDelta, Map<String, Data> prevResponses, Data currentResponse) throws Exception {
            log.info("{} called for: {}", builderToBeApplied.getName(), dataFlowInstance.getId());
        }

        @Override
        public void afterException(DataBuilderContext builderContext,
        						   DataFlowInstance dataFlowInstance,
                                   DataBuilderMeta builderToBeApplied,
                                   DataDelta dataDelta,
                                   Map<String, Data> prevResponses, Throwable frameworkException) throws Exception {
            log.info("{} called for: {}", builderToBeApplied.getName(), dataFlowInstance.getId());
        }

        @Override
        public void postProcessing(DataFlowInstance dataFlowInstance,
                                   DataDelta dataDelta, DataExecutionResponse response,
                                   Throwable frameworkException) throws Exception {
            log.info("Being called for: {}", dataFlowInstance.getId());
        }
    }
    private static class TestListenerAfterExecutionError implements DataBuilderExecutionListener {

        @Override
        public void preProcessing(DataFlowInstance dataFlowInstance,
                                  DataDelta dataDelta) throws Exception {
            log.info("Being called for: {}", dataFlowInstance.getId());
        }

        @Override
        public void beforeExecute(DataBuilderContext builderContext,
        						  DataFlowInstance dataFlowInstance,
                                  DataBuilderMeta builderToBeApplied,
                                  DataDelta dataDelta, Map<String, Data> prevResponses) throws Exception {
            log.info("{} called for: {}", builderToBeApplied.getName(), dataFlowInstance.getId());
        }

        @Override
        public void afterExecute(DataBuilderContext builderContext,
        						 DataFlowInstance dataFlowInstance,
                                 DataBuilderMeta builderToBeApplied,
                                 DataDelta dataDelta, Map<String, Data> prevResponses, Data currentResponse) throws Exception {
            //System.out.println(builderToBeApplied.getName() + " called for: " + dataFlowInstance.getId());
            throw new Exception("Blah blah");

        }

        @Override
        public void afterException(DataBuilderContext builderContext,
        						   DataFlowInstance dataFlowInstance,
                                   DataBuilderMeta builderToBeApplied,
                                   DataDelta dataDelta,
                                   Map<String, Data> prevResponses, Throwable frameworkException) throws Exception {
            log.info("{} called for: {}", builderToBeApplied.getName(), dataFlowInstance.getId());
        }

        @Override
        public void postProcessing(DataFlowInstance dataFlowInstance,
                                   DataDelta dataDelta, DataExecutionResponse response,
                                   Throwable frameworkException) throws Exception {
            log.info("Being called for: {}", dataFlowInstance.getId());
        }
    }

    private static class TestListenerAfterExceptionError implements DataBuilderExecutionListener {

        @Override
        public void preProcessing(DataFlowInstance dataFlowInstance,
                                  DataDelta dataDelta) throws Exception {
            log.info("Being called for: {}", dataFlowInstance.getId());
        }

        @Override
        public void beforeExecute(DataBuilderContext builderContext,
        						  DataFlowInstance dataFlowInstance,
                                  DataBuilderMeta builderToBeApplied,
                                  DataDelta dataDelta, Map<String, Data> prevResponses) throws Exception {
            log.info("{} called for: {}", builderToBeApplied.getName(), dataFlowInstance.getId());
        }

        @Override
        public void afterExecute(DataBuilderContext builderContext,
        						 DataFlowInstance dataFlowInstance,
                                 DataBuilderMeta builderToBeApplied,
                                 DataDelta dataDelta, Map<String, Data> prevResponses, Data currentResponse) throws Exception {
            log.info("{} called for: {}", builderToBeApplied.getName(), dataFlowInstance.getId());

        }

        @Override
        public void afterException(DataBuilderContext builderContext,
        						   DataFlowInstance dataFlowInstance,
                                   DataBuilderMeta builderToBeApplied,
                                   DataDelta dataDelta,
                                   Map<String, Data> prevResponses, Throwable frameworkException) throws Exception {
            //System.out.println(builderToBeApplied.getName() + " called for: " + dataFlowInstance.getId());
            throw new Exception("Blah blah");
        }
        @Override
        public void postProcessing(DataFlowInstance dataFlowInstance,
                                   DataDelta dataDelta, DataExecutionResponse response,
                                   Throwable frameworkException) throws Exception {
            log.info("Being called for: {}", dataFlowInstance.getId());
        }
    }

    private DataBuilderMetadataManager dataBuilderMetadataManager = new DataBuilderMetadataManager();
    private DataFlowExecutor executor = new SimpleDataFlowExecutor(new InstantiatingDataBuilderFactory(dataBuilderMetadataManager));
    private DataFlow dataFlow = new DataFlow();

    @BeforeEach
    public void setup() throws Exception {
        dataFlow = new DataFlowBuilder()
                .withAnnotatedDataBuilder(TestBuilderOptional.class)
                .withAnnotatedDataBuilder(TestBuilderB.class)
                .withAnnotatedDataBuilder(TestBuilderC.class)
                .withTargetData("F")
                .build();

    }

    @Test
    public void testRunWithoutOptional() throws Exception {
        DataFlowInstance dataFlowInstance = new DataFlowInstance();
        dataFlowInstance.setId("testflow");
        dataFlowInstance.setDataFlow(dataFlow);
        {
            DataDelta dataDelta = new DataDelta(new TestDataB("World"));
            DataExecutionResponse response = executor.run(dataFlowInstance, dataDelta);
            Assertions.assertTrue(response.getResponses().isEmpty());
            Assertions.assertFalse(response.getResponses().containsKey("C"));
        }
        {
            DataDelta dataDelta = new DataDelta(Lists.<Data>newArrayList(new TestDataA("Hello World")));
            DataExecutionResponse response = executor.run(dataFlowInstance, dataDelta);
            Assertions.assertFalse(response.getResponses().isEmpty());
            Assertions.assertTrue(response.getResponses().containsKey("C"));
        }
        {
            DataDelta dataDelta = new DataDelta(Lists.<Data>newArrayList(new TestDataD("this")));
            DataExecutionResponse response = executor.run(dataFlowInstance, dataDelta);
            Assertions.assertFalse(response.getResponses().isEmpty());
            Assertions.assertTrue(response.getResponses().containsKey("E"));
            Assertions.assertTrue(response.getResponses().containsKey("F"));
        }
    }

    @Test
    public void testRunOptional() throws Exception {
        DataFlowInstance dataFlowInstance = new DataFlowInstance();
        dataFlowInstance.setId("testflow");
        dataFlowInstance.setDataFlow(dataFlow);
        
        {
            DataDelta dataDelta = new DataDelta(new TestDataA("Hello"));
            DataExecutionResponse response = executor.run(dataFlowInstance, dataDelta);
            Assertions.assertTrue(response.getResponses().isEmpty());
            Assertions.assertFalse(response.getResponses().containsKey("C"));
        }
        {
            DataDelta dataDelta = new DataDelta(new TestDataB("World"));
            DataExecutionResponse response = executor.run(dataFlowInstance, dataDelta);
            Assertions.assertFalse(response.getResponses().isEmpty());
            Assertions.assertTrue(response.getResponses().containsKey("C"));
        }
        {
            DataDelta dataDelta = new DataDelta(Lists.<Data>newArrayList(new TestDataD("this")));
            DataExecutionResponse response = executor.run(dataFlowInstance, dataDelta);
            Assertions.assertFalse(response.getResponses().isEmpty());
            Assertions.assertTrue(response.getResponses().containsKey("E"));
            Assertions.assertTrue(response.getResponses().containsKey("F"));
        }
    }
    @Test
    public void testOptionalComingFirst() throws Exception {
        DataFlowInstance dataFlowInstance = new DataFlowInstance();
        dataFlowInstance.setId("testflow");
        dataFlowInstance.setDataFlow(dataFlow);
        {
            DataDelta dataDelta = new DataDelta(new TestDataB("World"));
            DataExecutionResponse response = executor.run(dataFlowInstance, dataDelta);
            Assertions.assertTrue(response.getResponses().isEmpty());
            Assertions.assertFalse(response.getResponses().containsKey("C"));
        }
        {
            DataDelta dataDelta = new DataDelta(new TestDataA("Hello"));
            DataExecutionResponse response = executor.run(dataFlowInstance, dataDelta);
            Assertions.assertFalse(response.getResponses().isEmpty());
            Assertions.assertTrue(response.getResponses().containsKey("C"));
        }
        {
            DataDelta dataDelta = new DataDelta(Lists.<Data>newArrayList(new TestDataD("this")));
            DataExecutionResponse response = executor.run(dataFlowInstance, dataDelta);
            Assertions.assertFalse(response.getResponses().isEmpty());
            Assertions.assertTrue(response.getResponses().containsKey("E"));
            Assertions.assertTrue(response.getResponses().containsKey("F"));
        }
    }

}
