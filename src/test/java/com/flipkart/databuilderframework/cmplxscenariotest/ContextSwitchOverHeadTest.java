package com.flipkart.databuilderframework.cmplxscenariotest;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.flipkart.databuilderframework.cmplxscenariotest.builders.BuilderA1;
import com.flipkart.databuilderframework.cmplxscenariotest.builders.BuilderA2;
import com.flipkart.databuilderframework.cmplxscenariotest.builders.BuilderA3;
import com.flipkart.databuilderframework.cmplxscenariotest.builders.BuilderB1;
import com.flipkart.databuilderframework.cmplxscenariotest.builders.BuilderB2;
import com.flipkart.databuilderframework.cmplxscenariotest.builders.BuilderB3;
import com.flipkart.databuilderframework.cmplxscenariotest.builders.BuilderB4;
import com.flipkart.databuilderframework.cmplxscenariotest.builders.BuilderB5;
import com.flipkart.databuilderframework.cmplxscenariotest.builders.BuilderC;
import com.flipkart.databuilderframework.cmplxscenariotest.builders.BuilderD;
import com.flipkart.databuilderframework.cmplxscenariotest.builders.BuilderE1;
import com.flipkart.databuilderframework.cmplxscenariotest.builders.BuilderE2;
import com.flipkart.databuilderframework.cmplxscenariotest.builders.BuilderE3;
import com.flipkart.databuilderframework.cmplxscenariotest.builders.BuilderE4;
import com.flipkart.databuilderframework.cmplxscenariotest.builders.BuilderE5;
import com.flipkart.databuilderframework.cmplxscenariotest.builders.BuilderE6;
import com.flipkart.databuilderframework.cmplxscenariotest.builders.BuilderF;
import com.flipkart.databuilderframework.cmplxscenariotest.builders.BuilderG;
import com.flipkart.databuilderframework.cmplxscenariotest.builders.BuilderH;
import com.flipkart.databuilderframework.cmplxscenariotest.builders.BuilderI;
import com.flipkart.databuilderframework.cmplxscenariotest.builders.BuilderJ;
import com.flipkart.databuilderframework.cmplxscenariotest.builders.BuilderK;
import com.flipkart.databuilderframework.cmplxscenariotest.data.DataA;
import com.flipkart.databuilderframework.cmplxscenariotest.data.DataI;
import com.flipkart.databuilderframework.cmplxscenariotest.data.InputAData;
import com.flipkart.databuilderframework.engine.*;
import com.flipkart.databuilderframework.engine.impl.InstantiatingDataBuilderFactory;
import com.flipkart.databuilderframework.model.*;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
public class ContextSwitchOverHeadTest {
	private final DataBuilderMetadataManager dataBuilderMetadataManager = new DataBuilderMetadataManager();
	private  DataFlow dataflow = new DataFlow();
	
	@BeforeEach
	public void setup() throws Exception {
		dataBuilderMetadataManager.register(BuilderA1.class);
		dataBuilderMetadataManager.register(BuilderA2.class);
		dataBuilderMetadataManager.register(BuilderA3.class);
		dataBuilderMetadataManager.register(BuilderB1.class);
		dataBuilderMetadataManager.register(BuilderB2.class);
		dataBuilderMetadataManager.register(BuilderB3.class);
		dataBuilderMetadataManager.register(BuilderB4.class);
		dataBuilderMetadataManager.register(BuilderB5.class);
		dataBuilderMetadataManager.register(BuilderC.class);
		dataBuilderMetadataManager.register(BuilderD.class);
		dataBuilderMetadataManager.register(BuilderE1.class);
		dataBuilderMetadataManager.register(BuilderE2.class);
		dataBuilderMetadataManager.register(BuilderE3.class);
		dataBuilderMetadataManager.register(BuilderE4.class);
		dataBuilderMetadataManager.register(BuilderE5.class);
		dataBuilderMetadataManager.register(BuilderE6.class);
		dataBuilderMetadataManager.register(BuilderF.class);
		dataBuilderMetadataManager.register(BuilderG.class);
		dataBuilderMetadataManager.register(BuilderH.class);
		dataBuilderMetadataManager.register(BuilderI.class);
		dataBuilderMetadataManager.register(BuilderJ.class);
		dataBuilderMetadataManager.register(BuilderK.class);

		dataflow.setDescription("Complex DataFlow");
		dataflow.setEnabled(true);
		dataflow.setTargetData("K");
		dataflow.setTransients(Sets.newHashSet("IA","I"));
		dataflow.setName("complext_flow");

		ExecutionGraphGenerator graphGenerator = new ExecutionGraphGenerator(dataBuilderMetadataManager);
		ExecutionGraph graph = graphGenerator.generateGraph(dataflow);
		dataflow.setExecutionGraph(graph);
	}

	@Test
	public void testWhenConccurencyIsLessThanBuilderSize() throws Exception{
		runTestWithUnBoundedQueue(150, 100);
	}
	
	@Test
	public void testWhenConccurencyIsMoreThanBuilderSize() throws Exception{
		log.info("testWhenConccurencyIsMoreThanBuilderSize");
		runTestWithUnBoundedQueue(50, 150);
	}
	
	@Test
	public void testWhenConccurencyIsSameAsBuilderSize() throws Exception{
		log.info("testWhenConccurencyIsSameAsBuilderSize");
		runTestWithUnBoundedQueue(150, 150);
	}
	
	public void runTestWithUnBoundedQueue(int builderThreadSize, int concurrentRequestSize) throws Exception{

		log.info("running unbounded queue test with builder size {} and concurrecy {}",builderThreadSize,concurrentRequestSize);
		final SimpleDataFlowExecutor se = new SimpleDataFlowExecutor(new InstantiatingDataBuilderFactory(dataBuilderMetadataManager));
		final ProfileExecutor builderExecutor = new ProfileExecutor(builderThreadSize, -1, 50);
		final MultiThreadedDataFlowExecutor me =  new MultiThreadedDataFlowExecutor(new InstantiatingDataBuilderFactory(dataBuilderMetadataManager),builderExecutor);;
		final OptimizedMultiThreadedDataFlowExecutor ome =  new OptimizedMultiThreadedDataFlowExecutor(new InstantiatingDataBuilderFactory(dataBuilderMetadataManager),builderExecutor);;
		final DataFlow dataflowRef = dataflow;
		
		ExecutorService exec = Executors.newFixedThreadPool(concurrentRequestSize); // concurrent users
		long start = System.currentTimeMillis();
		for(int i=0; i<1000; i++){
			exec.execute(new Runnable() {

				@Override
				public void run() {
					try {
						final DataFlowInstance instance  = new DataFlowInstance("test", dataflowRef);
						DataExecutionResponse resp = se.run(instance, new DataA(), new InputAData());
						Assertions.assertEquals(true,resp.getResponses().containsKey("K"));
					} catch (DataBuilderFrameworkException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (DataValidationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		exec.shutdown();
		exec.awaitTermination(200, TimeUnit.SECONDS);
		log.info("se {}", (System.currentTimeMillis() - start));



		ExecutorService newExec = Executors.newFixedThreadPool(concurrentRequestSize); // concurrent users
		start = System.currentTimeMillis();
		for(int i=0; i<1000; i++){
			newExec.execute(new Runnable() {

				@Override
				public void run() {
					try {
						final DataFlowInstance instance  = new DataFlowInstance("test", dataflowRef);
						DataExecutionResponse resp = me.run(instance, new DataA(), new InputAData());
						Assertions.assertEquals(true,resp.getResponses().containsKey("K"));
						//						System.out.println(resp.getResponses().keySet());
						//						System.out.println(iRef);
					} catch (DataBuilderFrameworkException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (DataValidationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		newExec.shutdown();
		newExec.awaitTermination(200, TimeUnit.SECONDS);
		log.info("me {}", (System.currentTimeMillis() - start));
		log.info("me run: context switches over threshold count {}, max latent switch: {}, rejections {}",
				builderExecutor.getNumberOfContextSwitchesOverThresHold(), builderExecutor.getMaxContextSwitchLatency(),
				builderExecutor.getRejectedCount());
		builderExecutor.resetStats();


		ExecutorService omeExec = Executors.newFixedThreadPool(concurrentRequestSize); // concurrent users
		start = System.currentTimeMillis();
		for(int i=0; i<1000; i++){
			omeExec.execute(new Runnable() {

				@Override
				public void run() {
					try {
						final DataFlowInstance instance  = new DataFlowInstance("test", dataflowRef);
						DataExecutionResponse resp = ome.run(instance, new DataA(), new InputAData());
						Assertions.assertEquals(true,resp.getResponses().containsKey("K"));
					} catch (DataBuilderFrameworkException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (DataValidationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		omeExec.shutdown();
		omeExec.awaitTermination(200, TimeUnit.SECONDS);
		log.info("ome {}"+(System.currentTimeMillis() - start));
		log.info("ome run: context switches over threshold count {}, max latent switch: {}, rejections {}",
				builderExecutor.getNumberOfContextSwitchesOverThresHold(), builderExecutor.getMaxContextSwitchLatency(),
				builderExecutor.getRejectedCount());
		builderExecutor.resetStats();
		builderExecutor.shutdownNow();
	}

}
