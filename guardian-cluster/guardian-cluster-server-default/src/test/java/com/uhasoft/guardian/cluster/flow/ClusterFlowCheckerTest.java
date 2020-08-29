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
package com.uhasoft.guardian.cluster.flow;

import com.uhasoft.guardian.cluster.TokenResult;
import com.uhasoft.guardian.cluster.flow.statistic.ClusterMetricStatistics;
import com.uhasoft.guardian.cluster.flow.statistic.metric.ClusterMetric;
import com.uhasoft.guardian.slots.block.ClusterRuleConstant;
import com.uhasoft.guardian.slots.block.flow.ClusterFlowConfig;
import com.uhasoft.guardian.slots.block.flow.FlowRule;

import com.uhasoft.guardian.cluster.ClusterFlowTestUtil;

/**
 * @author Eric Zhao
 * @since 1.4.0
 */
public class ClusterFlowCheckerTest {

    //@Test
    public void testAcquireClusterTokenOccupyPass() {
        long flowId = 98765L;
        final int threshold = 5;
        FlowRule clusterRule = new FlowRule("abc")
            .setCount(threshold)
            .setClusterMode(true)
            .setClusterConfig(new ClusterFlowConfig()
                .setFlowId(flowId)
                .setThresholdType(ClusterRuleConstant.FLOW_THRESHOLD_GLOBAL));
        int sampleCount = 5;
        int intervalInMs = 1000;
        int bucketLength = intervalInMs / sampleCount;
        ClusterMetric metric = new ClusterMetric(sampleCount, intervalInMs);
        ClusterMetricStatistics.putMetric(flowId, metric);

        System.out.println(System.currentTimeMillis());
        ClusterFlowTestUtil.assertResultPass(tryAcquire(clusterRule, false));
        ClusterFlowTestUtil.assertResultPass(tryAcquire(clusterRule, false));
        ClusterFlowTestUtil.sleep(bucketLength);
        ClusterFlowTestUtil.assertResultPass(tryAcquire(clusterRule, false));
        ClusterFlowTestUtil.sleep(bucketLength);
        ClusterFlowTestUtil.assertResultPass(tryAcquire(clusterRule, true));
        ClusterFlowTestUtil.assertResultPass(tryAcquire(clusterRule, false));
        ClusterFlowTestUtil.assertResultBlock(tryAcquire(clusterRule, true));
        ClusterFlowTestUtil.sleep(bucketLength);
        ClusterFlowTestUtil.assertResultBlock(tryAcquire(clusterRule, false));
        ClusterFlowTestUtil.assertResultBlock(tryAcquire(clusterRule, false));
        ClusterFlowTestUtil.sleep(bucketLength);
        ClusterFlowTestUtil.assertResultBlock(tryAcquire(clusterRule, false));
        ClusterFlowTestUtil.assertResultWait(tryAcquire(clusterRule, true), bucketLength);
        ClusterFlowTestUtil.assertResultBlock(tryAcquire(clusterRule, false));
        ClusterFlowTestUtil.sleep(bucketLength);
        ClusterFlowTestUtil.assertResultPass(tryAcquire(clusterRule, false));

        ClusterMetricStatistics.removeMetric(flowId);
    }

    private TokenResult tryAcquire(FlowRule clusterRule, boolean occupy) {
        return ClusterFlowChecker.acquireClusterToken(clusterRule, 1, occupy);
    }
}