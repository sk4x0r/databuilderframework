package com.flipkart.databuilderframework;

import static org.junit.jupiter.api.Assertions.fail;

import com.flipkart.databuilderframework.engine.DataSetAccessor;
import com.flipkart.databuilderframework.model.DataDelta;
import com.flipkart.databuilderframework.model.DataSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class DataSetAccessorTest {

    @Test
    public void testGet() throws Exception {
        DataSet dataSet = new DataSet();
        DataSetAccessor dataSetAccessor = DataSet.accessor(dataSet);
        dataSetAccessor.merge(new TestDataA("RandomValue"));
        TestDataA testDataA = dataSetAccessor.get("A", TestDataA.class);
        Assertions.assertEquals("RandomValue", testDataA.getValue());
        Assertions.assertNull(dataSetAccessor.get("X", TestDataA.class));

        try {
            TestDataB testDataB = dataSetAccessor.get("A", TestDataB.class);
        } catch (ClassCastException e) {
            return;
        }
        Assertions.fail();
    }

    @Test
    public void testMerge() throws Exception {
        DataSet dataSet = new DataSet();
        DataSetAccessor dataSetAccessor = DataSet.accessor(dataSet);
        DataDelta dataDelta = new DataDelta(Lists.newArrayList(new TestDataA("Hello"),
                                                                new TestDataB("World")));
        dataSetAccessor.merge(dataDelta);
        TestDataA testDataA = dataSetAccessor.get("A", TestDataA.class);
        Assertions.assertEquals("Hello", testDataA.getValue());
        TestDataB testDataB = dataSetAccessor.get("B", TestDataB.class);
        Assertions.assertEquals("World", testDataB.getValue());
    }

    @Test
    public void testCheckForData() throws Exception {
        DataSet dataSet = new DataSet();
        DataSetAccessor dataSetAccessor = DataSet.accessor(dataSet);
        DataDelta dataDelta = new DataDelta(Lists.newArrayList(new TestDataA("Hello"),
                new TestDataB("World")));
        dataSetAccessor.merge(dataDelta);
        Assertions.assertTrue(dataSetAccessor.checkForData("A"));
        Assertions.assertFalse(dataSetAccessor.checkForData("X"));
        Assertions.assertTrue(dataSetAccessor.checkForData(ImmutableSet.of("A", "B")));
        Assertions.assertFalse(dataSetAccessor.checkForData(ImmutableSet.of("A", "X")));
        Assertions.assertFalse(dataSetAccessor.checkForData(ImmutableSet.of("X", "A")));
        Assertions.assertFalse(dataSetAccessor.checkForData(ImmutableSet.of("X", "Ys")));
    }
}
