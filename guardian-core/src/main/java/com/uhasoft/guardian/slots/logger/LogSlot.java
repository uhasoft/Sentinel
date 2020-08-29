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
package com.uhasoft.guardian.slots.logger;

import com.uhasoft.guardian.log.RecordLog;
import com.uhasoft.guardian.context.Context;
import com.uhasoft.guardian.node.DefaultNode;
import com.uhasoft.guardian.slotchain.AbstractLinkedProcessorSlot;
import com.uhasoft.guardian.slotchain.ProcessorSlot;
import com.uhasoft.guardian.slotchain.ResourceWrapper;
import com.uhasoft.guardian.slots.block.BlockException;
import com.uhasoft.guardian.spi.SpiOrder;

/**
 * A {@link ProcessorSlot} that is response for logging block exceptions
 * to provide concrete logs for troubleshooting.
 */
@SpiOrder(-8000)
public class LogSlot extends AbstractLinkedProcessorSlot<DefaultNode> {

    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper, DefaultNode obj, int count, boolean prioritized, Object... args)
        throws Throwable {
        try {
            fireEntry(context, resourceWrapper, obj, count, prioritized, args);
        } catch (BlockException e) {
            EagleEyeLogUtil.log(resourceWrapper.getName(), e.getClass().getSimpleName(), e.getRuleLimitApp(),
                context.getOrigin(), count);
            throw e;
        } catch (Throwable e) {
            RecordLog.warn("Unexpected entry exception", e);
        }

    }

    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        try {
            fireExit(context, resourceWrapper, count, args);
        } catch (Throwable e) {
            RecordLog.warn("Unexpected entry exit exception", e);
        }
    }
}
