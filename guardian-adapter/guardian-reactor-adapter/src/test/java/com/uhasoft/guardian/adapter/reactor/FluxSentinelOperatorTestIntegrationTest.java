package com.uhasoft.guardian.adapter.reactor;

import java.util.ArrayList;
import java.util.Collections;

import com.uhasoft.guardian.node.ClusterNode;
import com.uhasoft.guardian.slots.block.BlockException;
import com.uhasoft.guardian.slots.block.flow.FlowRule;
import com.uhasoft.guardian.slots.block.flow.FlowRuleManager;
import com.uhasoft.guardian.slots.clusterbuilder.ClusterBuilderSlot;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.Assert.*;

/**
 * @author Eric Zhao
 */
public class FluxSentinelOperatorTestIntegrationTest {

    @Test
    public void testEmitMultipleValueSuccess() {
        String resourceName = createResourceName("testEmitMultipleSuccess");
        StepVerifier.create(Flux.just(1, 2)
            .map(e -> e * 2)
            .transform(new SentinelReactorTransformer<>(resourceName)))
            .expectNext(2)
            .expectNext(4)
            .verifyComplete();

        ClusterNode cn = ClusterBuilderSlot.getClusterNode(resourceName);
        assertNotNull(cn);
        assertEquals(1, cn.passQps(), 0.01);
    }

    @Test
    public void testEmitFluxError() {
        String resourceName = createResourceName("testEmitFluxError");
        StepVerifier.create(Flux.error(new IllegalAccessException("oops"))
            .transform(new SentinelReactorTransformer<>(resourceName)))
            .expectError(IllegalAccessException.class)
            .verify();

        ClusterNode cn = ClusterBuilderSlot.getClusterNode(resourceName);
        assertNotNull(cn);
        assertEquals(1, cn.passQps(), 0.01);
        assertEquals(1, cn.totalException());
    }

    @Test
    public void testEmitMultipleValuesWhenFlowControlTriggered() {
        String resourceName = createResourceName("testEmitMultipleValuesWhenFlowControlTriggered");
        FlowRuleManager.loadRules(Collections.singletonList(
            new FlowRule(resourceName).setCount(0)
        ));
        StepVerifier.create(Flux.just(1, 3, 5)
            .map(e -> e * 2)
            .transform(new SentinelReactorTransformer<>(resourceName)))
            .expectError(BlockException.class)
            .verify();

        ClusterNode cn = ClusterBuilderSlot.getClusterNode(resourceName);
        assertNotNull(cn);
        assertEquals(0, cn.passQps(), 0.01);
        assertEquals(1, cn.blockRequest());

        FlowRuleManager.loadRules(new ArrayList<>());
    }

    private String createResourceName(String resourceName) {
        return "reactor_test_flux_" + resourceName;
    }
}