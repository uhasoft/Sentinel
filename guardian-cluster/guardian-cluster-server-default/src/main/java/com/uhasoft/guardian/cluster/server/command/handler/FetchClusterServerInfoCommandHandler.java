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
package com.uhasoft.guardian.cluster.server.command.handler;

import java.util.Set;

import com.uhasoft.guardian.cluster.flow.statistic.limit.GlobalRequestLimiter;
import com.uhasoft.guardian.cluster.server.config.ClusterServerConfigManager;
import com.uhasoft.guardian.cluster.server.config.ServerFlowConfig;
import com.uhasoft.guardian.cluster.server.config.ServerTransportConfig;
import com.uhasoft.guardian.cluster.server.connection.ConnectionGroup;
import com.uhasoft.guardian.cluster.server.connection.ConnectionManager;
import com.uhasoft.guardian.command.CommandHandler;
import com.uhasoft.guardian.command.CommandRequest;
import com.uhasoft.guardian.command.CommandResponse;
import com.uhasoft.guardian.command.annotation.CommandMapping;
import com.uhasoft.guardian.util.AppNameUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author Eric Zhao
 * @since 1.4.0
 */
@CommandMapping(name = "cluster/server/info", desc = "get cluster server info")
public class FetchClusterServerInfoCommandHandler implements CommandHandler<String> {

    @Override
    public CommandResponse<String> handle(CommandRequest request) {
        JSONObject info = new JSONObject();
        JSONArray connectionGroups = new JSONArray();
        Set<String> namespaceSet = ClusterServerConfigManager.getNamespaceSet();
        for (String namespace : namespaceSet) {
            ConnectionGroup group = ConnectionManager.getOrCreateConnectionGroup(namespace);
            if (group != null) {
                connectionGroups.add(group);
            }
        }

        ServerTransportConfig transportConfig = new ServerTransportConfig()
            .setPort(ClusterServerConfigManager.getPort())
            .setIdleSeconds(ClusterServerConfigManager.getIdleSeconds());
        ServerFlowConfig flowConfig = new ServerFlowConfig()
            .setExceedCount(ClusterServerConfigManager.getExceedCount())
            .setMaxOccupyRatio(ClusterServerConfigManager.getMaxOccupyRatio())
            .setIntervalMs(ClusterServerConfigManager.getIntervalMs())
            .setSampleCount(ClusterServerConfigManager.getSampleCount())
            .setMaxAllowedQps(ClusterServerConfigManager.getMaxAllowedQps());

        JSONArray requestLimitData = buildRequestLimitData(namespaceSet);

        info.fluentPut("port", ClusterServerConfigManager.getPort())
            .fluentPut("connection", connectionGroups)
            .fluentPut("requestLimitData", requestLimitData)
            .fluentPut("transport", transportConfig)
            .fluentPut("flow", flowConfig)
            .fluentPut("namespaceSet", namespaceSet)
            .fluentPut("embedded", ClusterServerConfigManager.isEmbedded());

        // Since 1.5.0 the appName is carried so that the caller can identify the appName of the token server.
        info.put("appName", AppNameUtil.getAppName());

        return CommandResponse.ofSuccess(info.toJSONString());
    }

    private JSONArray buildRequestLimitData(Set<String> namespaceSet) {
        JSONArray array = new JSONArray();
        for (String namespace : namespaceSet) {
            array.add(new JSONObject()
                .fluentPut("namespace", namespace)
                .fluentPut("currentQps", GlobalRequestLimiter.getCurrentQps(namespace))
                .fluentPut("maxAllowedQps", GlobalRequestLimiter.getMaxAllowedQps(namespace))
            );
        }
        return array;
    }
}

