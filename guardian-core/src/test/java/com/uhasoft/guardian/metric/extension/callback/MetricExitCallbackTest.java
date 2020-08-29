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
package com.uhasoft.guardian.metric.extension.callback;

import com.uhasoft.guardian.Entry;
import com.uhasoft.guardian.EntryType;
import com.uhasoft.guardian.context.Context;
import com.uhasoft.guardian.metric.extension.MetricExtensionProvider;
import com.uhasoft.guardian.slotchain.StringResourceWrapper;
import com.uhasoft.guardian.test.AbstractTimeBasedTest;

import org.junit.Assert;
import org.junit.Test;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Carpenter Lee
 */
public class MetricExitCallbackTest extends AbstractTimeBasedTest {

    @Test
    public void onExit() {
        FakeMetricExtension extension = new FakeMetricExtension();
        MetricExtensionProvider.addMetricExtension(extension);

        MetricExitCallback exitCallback = new MetricExitCallback();
        StringResourceWrapper resourceWrapper = new StringResourceWrapper("resource", EntryType.OUT);
        int count = 2;
        Object[] args = {"args1", "args2"};
        long prevRt = 20;
        extension.rt = prevRt;
        extension.success = 6;
        extension.thread = 10;
        Context context = mock(Context.class);
        Entry entry = mock(Entry.class);

        // Mock current time
        long curMillis = System.currentTimeMillis();
        setCurrentMillis(curMillis);

        int deltaMs = 100;
        when(entry.getError()).thenReturn(null);
        when(entry.getCreateTimestamp()).thenReturn(curMillis - deltaMs);
        when(context.getCurEntry()).thenReturn(entry);
        exitCallback.onExit(context, resourceWrapper, count, args);
        Assert.assertEquals(prevRt + deltaMs, extension.rt);
        Assert.assertEquals(extension.success, 6 + count);
        Assert.assertEquals(extension.thread, 10 - 1);
    }
    
    /**
     * @author bill_yip
     */
    @Test
    public void advancedExtensionOnExit() {
        FakeAdvancedMetricExtension extension = new FakeAdvancedMetricExtension();
        MetricExtensionProvider.addMetricExtension(extension);

        MetricExitCallback exitCallback = new MetricExitCallback();
        StringResourceWrapper resourceWrapper = new StringResourceWrapper("resource", EntryType.OUT);
        int count = 2;
        Object[] args = {"args1", "args2"};
        long prevRt = 20;
        extension.rt = prevRt;
        extension.complete = 6;
        extension.concurrency = 10;
        Context context = mock(Context.class);
        Entry entry = mock(Entry.class);

        // Mock current time
        long curMillis = System.currentTimeMillis();
        setCurrentMillis(curMillis);

        int deltaMs = 100;
        when(entry.getError()).thenReturn(null);
        when(entry.getCreateTimestamp()).thenReturn(curMillis - deltaMs);
        when(context.getCurEntry()).thenReturn(entry);
        exitCallback.onExit(context, resourceWrapper, count, args);
        Assert.assertEquals(prevRt + deltaMs, extension.rt);
        Assert.assertEquals(extension.complete, 6 + count);
        Assert.assertEquals(extension.concurrency, 10 - 1);
    }
}
