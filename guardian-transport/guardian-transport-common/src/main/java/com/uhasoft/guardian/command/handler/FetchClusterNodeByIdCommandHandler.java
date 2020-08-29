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
package com.uhasoft.guardian.command.handler;

import com.uhasoft.guardian.command.CommandHandler;
import com.uhasoft.guardian.command.CommandRequest;
import com.uhasoft.guardian.command.CommandResponse;
import com.uhasoft.guardian.command.annotation.CommandMapping;
import com.uhasoft.guardian.node.ClusterNode;
import com.uhasoft.guardian.command.vo.NodeVo;
import com.uhasoft.guardian.slots.clusterbuilder.ClusterBuilderSlot;
import com.uhasoft.guardian.util.StringUtil;
import com.alibaba.fastjson.JSON;

/**
 * @author qinan.qn
 */
@CommandMapping(name = "clusterNodeById", desc = "get clusterNode VO by id, request param: id={resourceName}")
public class FetchClusterNodeByIdCommandHandler implements CommandHandler<String> {

    @Override
    public CommandResponse<String> handle(CommandRequest request) {
        String id = request.getParam("id");
        if (StringUtil.isEmpty(id)) {
            return CommandResponse.ofFailure(new IllegalArgumentException("Invalid parameter: empty clusterNode name"));
        }
        ClusterNode node = ClusterBuilderSlot.getClusterNode(id);
        if (node != null) {
            return CommandResponse.ofSuccess(JSON.toJSONString(NodeVo.fromClusterNode(id, node)));
        } else {
            return CommandResponse.ofSuccess("{}");
        }
    }
}
