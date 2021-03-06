/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.uhasoft.guardian.slots.block.system;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import com.uhasoft.guardian.node.ClusterNode;
import com.uhasoft.guardian.node.DefaultNode;
import com.uhasoft.guardian.slots.block.BlockException;
import com.uhasoft.guardian.slots.system.SystemRule;
import org.junit.Test;

import com.uhasoft.guardian.context.Context;
import com.uhasoft.guardian.slots.system.SystemRuleManager;

/**
 * @author jialiang.linjl
 */
public class SystemRuleTest {

    @Test
    public void testSystemRule_load() {
        SystemRule systemRule = new SystemRule();

        systemRule.setAvgRt(4000L);

        SystemRuleManager.loadRules(Collections.singletonList(systemRule));
    }

    @Test
    public void testSystemRule_avgRt() throws BlockException {

        SystemRule systemRule = new SystemRule();

        systemRule.setAvgRt(4L);

        Context context = mock(Context.class);
        DefaultNode node = mock(DefaultNode.class);
        ClusterNode cn = mock(ClusterNode.class);

        when(context.getOrigin()).thenReturn("");
        when(node.getClusterNode()).thenReturn(cn);

    }

}
