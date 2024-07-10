package com.flipkart.databuilderframework;

import com.flipkart.databuilderframework.engine.*;
import com.flipkart.databuilderframework.engine.impl.InstantiatingDataBuilderFactory;
import com.flipkart.databuilderframework.model.*;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Created with IntelliJ IDEA.
 * User: vinay.varma
 * Date: 5/18/15
 * Time: 11:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class PerfWithAccessesTest {
    private DataBuilderMetadataManager dataBuilderMetadataManager = new DataBuilderMetadataManager();
    private DataFlowExecutor executor = new SimpleDataFlowExecutor(new InstantiatingDataBuilderFactory(dataBuilderMetadataManager));
    private DataFlow dataFlow = new DataFlow();
    private DataFlow dataFlowWithAccesses = new DataFlow();

    @BeforeEach
    public void setup() throws Exception {
        dataFlow = new DataFlowBuilder()
                .withAnnotatedDataBuilder(TestBuilderWithoutAccesses.class)
                .withTargetData("X")
                .build();
        dataFlowWithAccesses = new DataFlowBuilder()
                .withAnnotatedDataBuilder(TestBuilderAccesses.class)
                .withTargetData("X")
                .build();
    }

    @Test
    public void shouldBeComparableWithoutAnyAccesibleData() throws DataBuilderFrameworkException, DataValidationException {
        long simpleTime = 0;
        for (int i = 0; i < 10000; i++) {
            DataFlowInstance dataFlowInstance = new DataFlowInstance();
            dataFlowInstance.setId("testflow");
            dataFlowInstance.setDataFlow(dataFlow);
            DataDelta dataDelta = new DataDelta(Lists.<Data>newArrayList(new TestDataA("HEy")));
            long startTime = System.currentTimeMillis();
            DataExecutionResponse response = executor.run(dataFlowInstance, dataDelta);
            simpleTime += (System.currentTimeMillis() - startTime);
            Assertions.assertEquals(1, response.getResponses().size());
        }
        long accessTime = 0;
        for (int i = 0; i < 10000; i++) {
            DataFlowInstance dataFlowInstance = new DataFlowInstance();
            dataFlowInstance.setId("testflow");
            dataFlowInstance.setDataFlow(dataFlowWithAccesses);
            DataDelta dataDelta = new DataDelta(Lists.<Data>newArrayList(new TestDataA("HEy"), new TestDataD("DD")));
            long startTime = System.currentTimeMillis();
            DataExecutionResponse response = executor.run(dataFlowInstance, dataDelta);
            accessTime += (System.currentTimeMillis() - startTime);
            Assertions.assertEquals(1, response.getResponses().size());
        }
        long simpleAccessTime = 0;
        for (int i = 0; i < 10000; i++) {
            DataFlowInstance dataFlowInstance = new DataFlowInstance();
            dataFlowInstance.setId("testflow");
            dataFlowInstance.setDataFlow(dataFlowWithAccesses);
            DataDelta dataDelta = new DataDelta(Lists.<Data>newArrayList(new TestDataA("HEy")));
            long startTime = System.currentTimeMillis();
            DataExecutionResponse response = executor.run(dataFlowInstance, dataDelta);
            simpleAccessTime += (System.currentTimeMillis() - startTime);
            Assertions.assertEquals(1, response.getResponses().size());
        }

        System.out.println(String.format("Time without Accesses :%d Time with Accesses :%d Time with accesses w/o data :%d "
                , simpleTime, accessTime, simpleAccessTime));
    }


}
