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
package com.uhasoft.guardian.command.handler.cluster;

import com.uhasoft.guardian.cluster.ClusterStateManager;
import com.uhasoft.guardian.cluster.client.TokenClientProvider;
import com.uhasoft.guardian.cluster.server.EmbeddedClusterTokenServerProvider;
import com.uhasoft.guardian.command.CommandHandler;
import com.uhasoft.guardian.command.CommandRequest;
import com.uhasoft.guardian.command.CommandResponse;
import com.uhasoft.guardian.command.annotation.CommandMapping;
import com.alibaba.fastjson.JSONObject;

/**
 * @author Eric Zhao
 * @since 1.4.0
 */
@CommandMapping(name = "getClusterMode", desc = "get cluster mode status")
public class FetchClusterModeCommandHandler implements CommandHandler<String> {

    @Override
    public CommandResponse<String> handle(CommandRequest request) {
        JSONObject res = new JSONObject()
            .fluentPut("mode", ClusterStateManager.getMode())
            .fluentPut("lastModified", ClusterStateManager.getLastModified())
            .fluentPut("clientAvailable", isClusterClientSpiAvailable())
            .fluentPut("serverAvailable", isClusterServerSpiAvailable());
        return CommandResponse.ofSuccess(res.toJSONString());
    }

    private boolean isClusterClientSpiAvailable() {
        return TokenClientProvider.getClient() != null;
    }

    private boolean isClusterServerSpiAvailable() {
        return EmbeddedClusterTokenServerProvider.getServer() != null;
    }
}
