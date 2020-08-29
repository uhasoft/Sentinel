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
package com.uhasoft.guardian.slots.system;

import com.uhasoft.guardian.context.Context;
import com.uhasoft.guardian.node.DefaultNode;
import com.uhasoft.guardian.slotchain.AbstractLinkedProcessorSlot;
import com.uhasoft.guardian.slotchain.ProcessorSlot;
import com.uhasoft.guardian.slotchain.ResourceWrapper;
import com.uhasoft.guardian.spi.SpiOrder;

/**
 * A {@link ProcessorSlot} that dedicates to {@link SystemRule} checking.
 *
 * @author jialiang.linjl
 * @author leyou
 */
@SpiOrder(-5000)
public class SystemSlot extends AbstractLinkedProcessorSlot<DefaultNode> {

    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper, DefaultNode node, int count,
                      boolean prioritized, Object... args) throws Throwable {
        SystemRuleManager.checkSystem(resourceWrapper);
        fireEntry(context, resourceWrapper, node, count, prioritized, args);
    }

    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        fireExit(context, resourceWrapper, count, args);
    }

}
