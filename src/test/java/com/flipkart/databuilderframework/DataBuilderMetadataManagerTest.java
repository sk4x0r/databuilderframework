package com.flipkart.databuilderframework;

import com.flipkart.databuilderframework.engine.DataBuilderFrameworkException;
import com.flipkart.databuilderframework.engine.DataBuilderMetadataManager;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;



public class DataBuilderMetadataManagerTest {
    @Test
    public void testRegister() throws Exception {
        DataBuilderMetadataManager dataBuilderMetadataManager
                                        = new DataBuilderMetadataManager();
        dataBuilderMetadataManager.register(ImmutableSet.of("A", "B"), "C", "BuilderA", TestBuilderA.class );
        try {
            dataBuilderMetadataManager.register(ImmutableSet.of("A", "B"), "C", "BuilderA", TestBuilderB.class );
        } catch (DataBuilderFrameworkException e) {
            if(e.getErrorCode() == DataBuilderFrameworkException.ErrorCode.BUILDER_EXISTS) {
                return;
            }
        }
        Assertions.fail("Duplicate error should have come");
    }
}
