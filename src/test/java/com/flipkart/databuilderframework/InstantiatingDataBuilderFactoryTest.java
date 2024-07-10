package com.flipkart.databuilderframework;

import static org.junit.jupiter.api.Assertions.fail;

import com.flipkart.databuilderframework.engine.*;
import com.flipkart.databuilderframework.engine.impl.InstantiatingDataBuilderFactory;
import com.flipkart.databuilderframework.model.Data;
import com.flipkart.databuilderframework.model.DataBuilderMeta;
import com.flipkart.databuilderframework.model.ExecutionGraph;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.util.Collections;
import org.junit.jupiter.api.Test;


public class InstantiatingDataBuilderFactoryTest {
    public static class WrongBuilder extends DataBuilder {

        public WrongBuilder(String blah) {

        }

        @Override
        public Data process(DataBuilderContext context) {
            return null;
        }
    }
    private DataBuilderMetadataManager dataBuilderMetadataManager = new DataBuilderMetadataManager();
    private ExecutionGraphGenerator executionGraphGenerator = new ExecutionGraphGenerator(dataBuilderMetadataManager);
    private DataBuilderFactory dataBuilderFactory = new InstantiatingDataBuilderFactory(dataBuilderMetadataManager);

    @BeforeEach
    public void setup() throws Exception {
        dataBuilderMetadataManager.register(ImmutableSet.of("A", "B"), "C", "BuilderA", TestBuilderA.class);
        dataBuilderMetadataManager.register(ImmutableSet.of("A", "B"), "C", "BuilderB", null);
        dataBuilderMetadataManager.register(ImmutableSet.of("A", "B"), "X", "BuilderC", WrongBuilder.class);
    }


    @Test
    public void testCreate() throws Exception {
        try {
            Assertions.assertNotNull(dataBuilderFactory.create(
                    DataBuilderMeta.builder()
                            .name("BuilderA")
                            .consumes(Collections.emptySet())
                            .build()));
            dataBuilderFactory.create(DataBuilderMeta.builder()
                    .name("BuilderB")
                    .consumes(Collections.emptySet())
                    .build()); //Should throw
        } catch (DataBuilderFrameworkException e) {
            if(DataBuilderFrameworkException.ErrorCode.NO_BUILDER_FOUND_FOR_NAME == e.getErrorCode()) {
                return;
            }
        }
        Assertions.fail();
     }

    @Test
    public void testFail() throws Exception {
        ExecutionGraph executionGraph = new ExecutionGraph();
        try {
            dataBuilderFactory.create(DataBuilderMeta.builder()
                    .name("BuilderC")
                    .consumes(Collections.emptySet())
                    .build()); //Should throw
        } catch (DataBuilderFrameworkException e) {
            if(DataBuilderFrameworkException.ErrorCode.INSTANTIATION_FAILURE == e.getErrorCode()) {
                return;
            }
        }
        Assertions.fail();
    }
}
