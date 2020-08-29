/*
 * Copyright 1999-2019 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uhasoft.guardian.adapter.gateway.common.slot;

import java.util.List;

import com.uhasoft.guardian.adapter.gateway.common.rule.GatewayRuleManager;
import com.uhasoft.guardian.context.Context;
import com.uhasoft.guardian.node.DefaultNode;
import com.uhasoft.guardian.slotchain.AbstractLinkedProcessorSlot;
import com.uhasoft.guardian.slotchain.ResourceWrapper;
import com.uhasoft.guardian.slots.block.BlockException;
import com.uhasoft.guardian.slots.block.flow.param.ParamFlowChecker;
import com.uhasoft.guardian.slots.block.flow.param.ParamFlowException;
import com.uhasoft.guardian.slots.block.flow.param.ParamFlowRule;
import com.uhasoft.guardian.slots.block.flow.param.ParameterMetricStorage;
import com.uhasoft.guardian.spi.SpiOrder;

/**
 * @author Eric Zhao
 * @since 1.6.1
 */
@SpiOrder(-4000)
public class GatewayFlowSlot extends AbstractLinkedProcessorSlot<DefaultNode> {

    @Override
    public void entry(Context context, ResourceWrapper resource, DefaultNode node, int count,
                      boolean prioritized, Object... args) throws Throwable {
        checkGatewayParamFlow(resource, count, args);

        fireEntry(context, resource, node, count, prioritized, args);
    }

    private void checkGatewayParamFlow(ResourceWrapper resourceWrapper, int count, Object... args)
        throws BlockException {
        if (args == null) {
            return;
        }

        List<ParamFlowRule> rules = GatewayRuleManager.getConvertedParamRules(resourceWrapper.getName());
        if (rules == null || rules.isEmpty()) {
            return;
        }

        for (ParamFlowRule rule : rules) {
            // Initialize the parameter metrics.
            ParameterMetricStorage.initParamMetricsFor(resourceWrapper, rule);

            if (!ParamFlowChecker.passCheck(resourceWrapper, rule, count, args)) {
                String triggeredParam = "";
                if (args.length > rule.getParamIdx()) {
                    Object value = args[rule.getParamIdx()];
                    triggeredParam = String.valueOf(value);
                }
                throw new ParamFlowException(resourceWrapper.getName(), triggeredParam, rule);
            }
        }
    }

    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        fireExit(context, resourceWrapper, count, args);
    }
}
