package com.uhasoft.guardian.adapter.reactor;

import java.util.ArrayList;
import java.util.Collections;

import com.uhasoft.guardian.node.ClusterNode;
import com.uhasoft.guardian.slots.block.BlockException;
import com.uhasoft.guardian.slots.block.flow.FlowRule;
import com.uhasoft.guardian.slots.block.flow.FlowRuleManager;
import com.uhasoft.guardian.slots.clusterbuilder.ClusterBuilderSlot;

import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import static org.junit.Assert.*;

/**
 * @author Eric Zhao
 */
public class ReactorSphUTest {

    @Test
    public void testReactorEntryNormalWhenFlowControlTriggered() {
        String resourceName = createResourceName("testReactorEntryNormalWhenFlowControlTriggered");
        FlowRuleManager.loadRules(Collections.singletonList(
            new FlowRule(resourceName).setCount(0)
        ));
        StepVerifier.create(ReactorSphU.entryWith(resourceName, Mono.just(60))
            .subscribeOn(Schedulers.elastic())
            .map(e -> e * 3))
            .expectError(BlockException.class)
            .verify();

        ClusterNode cn = ClusterBuilderSlot.getClusterNode(resourceName);
        assertNotNull(cn);
        assertEquals(0, cn.passQps(), 0.01);
        assertEquals(1, cn.blockRequest());

        FlowRuleManager.loadRules(new ArrayList<>());
    }

    @Test
    public void testReactorEntryWithCommon() {
        String resourceName = createResourceName("testReactorEntryWithCommon");
        StepVerifier.create(ReactorSphU.entryWith(resourceName, Mono.just(60))
            .subscribeOn(Schedulers.elastic())
            .map(e -> e * 3))
            .expectNext(180)
            .verifyComplete();

        ClusterNode cn = ClusterBuilderSlot.getClusterNode(resourceName);
        assertNotNull(cn);
        assertEquals(1, cn.passQps(), 0.01);
    }

    @Test
    public void testReactorEntryWithBizException() {
        String resourceName = createResourceName("testReactorEntryWithBizException");
        StepVerifier.create(ReactorSphU.entryWith(resourceName, Mono.error(new IllegalStateException())))
            .expectError(IllegalStateException.class)
            .verify();

        ClusterNode cn = ClusterBuilderSlot.getClusterNode(resourceName);
        assertNotNull(cn);
        assertEquals(1, cn.passQps(), 0.01);
        assertEquals(1, cn.totalException());
    }

    private String createResourceName(String resourceName) {
        return "reactor_test_SphU_" + resourceName;
    }
}