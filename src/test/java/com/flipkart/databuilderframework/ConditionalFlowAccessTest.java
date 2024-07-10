package com.flipkart.databuilderframework;

import com.flipkart.databuilderframework.engine.*;
import com.flipkart.databuilderframework.engine.impl.InstantiatingDataBuilderFactory;
import com.flipkart.databuilderframework.model.*;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConditionalFlowAccessTest {
    private DataBuilderMetadataManager dataBuilderMetadataManager = new DataBuilderMetadataManager();
    private DataFlowExecutor executor = new SimpleDataFlowExecutor(new InstantiatingDataBuilderFactory(dataBuilderMetadataManager));
    private ExecutionGraphGenerator executionGraphGenerator = new ExecutionGraphGenerator(dataBuilderMetadataManager);
    private DataFlow dataFlow;
    private DataFlow dataFlowError = new DataFlow();

    public static final class ConditionalBuilder extends DataBuilder {

        @Override
        public Data process(DataBuilderContext context) throws DataBuilderException {
            DataSetAccessor accessor = new DataSetAccessor(context.getDataSet());
            Assertions.assertTrue(accessor.checkForData("A")); //Assuming in this test without BuilderA Builder is not run and Builder B has access to A
            Assertions.assertFalse(accessor.checkForData("B")); //BuilderB does not have access to A
            TestDataC dataC = accessor.get("C", TestDataC.class);
            TestDataD dataD = accessor.get("D", TestDataD.class);
            if(dataC.getValue().equals("Hello World")
                    && dataD.getValue().equalsIgnoreCase("this")) {
                return new TestDataE("Wah wah!!");
            }
            return null;
        }
    }

    @BeforeEach
    public void setup() throws Exception {
        dataBuilderMetadataManager.register(ImmutableSet.of("A", "B"), "C", "BuilderA", TestBuilderA.class );
        dataBuilderMetadataManager.registerWithAccess(ImmutableSet.of("C", "D"), ImmutableSet.of("A"),"E", "BuilderB", ConditionalBuilder.class );
        dataBuilderMetadataManager.register(ImmutableSet.of("A", "E"), "F", "BuilderC", TestBuilderC.class );

        dataFlow = new DataFlowBuilder()
                        .withMetaDataManager(dataBuilderMetadataManager)
                        .withTargetData("F")
                        .build();

        dataFlowError = new DataFlowBuilder()
                        .withMetaDataManager(dataBuilderMetadataManager)
                        .withTargetData("Y")
                        .build();

    }

    @Test
    public void testRunStop() throws Exception {
        DataFlowInstance dataFlowInstance = new DataFlowInstance();
        dataFlowInstance.setId("testflow");
        dataFlowInstance.setDataFlow(dataFlow);
        {
            DataDelta dataDelta = new DataDelta(Lists.<Data>newArrayList(new TestDataA("Hello")));
            DataExecutionResponse response = executor.run(dataFlowInstance, dataDelta);
            Assertions.assertTrue(response.getResponses().isEmpty());
        }
        {
            DataDelta dataDelta = new DataDelta(Lists.<Data>newArrayList(new TestDataB("Bhai")));
            DataExecutionResponse response = executor.run(dataFlowInstance, dataDelta);
            Assertions.assertFalse(response.getResponses().isEmpty());
            Assertions.assertTrue(response.getResponses().containsKey("C"));
        }
        {
            DataDelta dataDelta = new DataDelta(Lists.<Data>newArrayList(new TestDataD("this")));
            DataExecutionResponse response = executor.run(dataFlowInstance, dataDelta);
            Assertions.assertTrue(response.getResponses().isEmpty());
        }

    }

    @Test
    public void testRun() throws Exception {
        DataFlowInstance dataFlowInstance = new DataFlowInstance();
        dataFlowInstance.setId("testflow");
        dataFlowInstance.setDataFlow(dataFlow);
        {
            DataDelta dataDelta = new DataDelta(Lists.<Data>newArrayList(new TestDataA("Hello")));
            DataExecutionResponse response = executor.run(dataFlowInstance, dataDelta);
            Assertions.assertTrue(response.getResponses().isEmpty());
        }
        {
            DataDelta dataDelta = new DataDelta(Lists.<Data>newArrayList(new TestDataB("World")));
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
