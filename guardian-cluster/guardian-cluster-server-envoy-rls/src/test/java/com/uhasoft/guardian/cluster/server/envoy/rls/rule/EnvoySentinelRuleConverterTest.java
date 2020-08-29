/*
 * Copyright 1999-2019 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uhasoft.guardian.cluster.server.envoy.rls.rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.uhasoft.guardian.slots.block.flow.FlowRule;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Eric Zhao
 */
public class EnvoySentinelRuleConverterTest {

    @Test
    public void testConvertToSentinelFlowRules() {
        String domain = "testConvertToSentinelFlowRules";
        EnvoyRlsRule rlsRule = new EnvoyRlsRule();
        rlsRule.setDomain(domain);
        List<EnvoyRlsRule.ResourceDescriptor> descriptors = new ArrayList<>();
        EnvoyRlsRule.ResourceDescriptor d1 = new EnvoyRlsRule.ResourceDescriptor();
        d1.setCount(10d);
        d1.setResources(Collections.singleton(new EnvoyRlsRule.KeyValueResource("k1", "v1")));
        descriptors.add(d1);
        EnvoyRlsRule.ResourceDescriptor d2 = new EnvoyRlsRule.ResourceDescriptor();
        d2.setCount(20d);
        d2.setResources(new HashSet<>(Arrays.asList(
            new EnvoyRlsRule.KeyValueResource("k2", "v2"),
            new EnvoyRlsRule.KeyValueResource("k3", "v3")
        )));
        descriptors.add(d2);
        rlsRule.setDescriptors(descriptors);

        List<FlowRule> rules = EnvoySentinelRuleConverter.toSentinelFlowRules(rlsRule);
        final String expectedK1 = domain + EnvoySentinelRuleConverter.SEPARATOR + "k1" + EnvoySentinelRuleConverter.SEPARATOR + "v1";
        FlowRule r1 = rules.stream()
            .filter(e -> e.getResource().equals(expectedK1))
            .findAny()
            .orElseThrow(() -> new AssertionError("the converted rule does not exist, expected key: " + expectedK1));
        assertEquals(10d, r1.getCount(), 0.01);

        final String expectedK2 = domain + EnvoySentinelRuleConverter.SEPARATOR + "k2" + EnvoySentinelRuleConverter.SEPARATOR + "v2" + EnvoySentinelRuleConverter.SEPARATOR + "k3" + EnvoySentinelRuleConverter.SEPARATOR + "v3";
        FlowRule r2 = rules.stream()
            .filter(e -> e.getResource().equals(expectedK2))
            .findAny()
            .orElseThrow(() -> new AssertionError("the converted rule does not exist, expected key: " + expectedK2));
        assertEquals(20d, r2.getCount(), 0.01);
    }
}
