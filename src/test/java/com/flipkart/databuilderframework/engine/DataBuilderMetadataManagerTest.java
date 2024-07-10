package com.flipkart.databuilderframework.engine;

import com.flipkart.databuilderframework.complextest.SB;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataBuilderMetadataManagerTest {

    @Test
    public void testGetConsumesSetFor() throws Exception {
        DataBuilderMetadataManager manager = new DataBuilderMetadataManager();
        Assertions.assertEquals(null, manager.getConsumesSetFor("A"));
        manager.register(ImmutableSet.of("CR", "CAID", "VAS"), "OO", "SB", SB.class);
        Assertions.assertEquals(1, manager.getConsumesSetFor("CR").size());
    }
}
