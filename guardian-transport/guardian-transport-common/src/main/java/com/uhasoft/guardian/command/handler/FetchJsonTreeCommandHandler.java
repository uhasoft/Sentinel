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

import java.util.ArrayList;
import java.util.List;

import com.uhasoft.guardian.Constants;
import com.uhasoft.guardian.command.CommandHandler;
import com.uhasoft.guardian.command.CommandRequest;
import com.uhasoft.guardian.command.CommandResponse;
import com.uhasoft.guardian.command.annotation.CommandMapping;
import com.uhasoft.guardian.node.DefaultNode;
import com.uhasoft.guardian.node.Node;
import com.uhasoft.guardian.command.vo.NodeVo;

import com.alibaba.fastjson.JSON;

/**
 * @author leyou
 */
@CommandMapping(name = "jsonTree", desc = "get tree node VO start from root node")
public class FetchJsonTreeCommandHandler implements CommandHandler<String> {

    @Override
    public CommandResponse<String> handle(CommandRequest request) {
        List<NodeVo> results = new ArrayList<NodeVo>();
        visit(Constants.ROOT, results, null);
        return CommandResponse.ofSuccess(JSON.toJSONString(results));
    }

    /**
     * Preorder traversal.
     */
    private void visit(DefaultNode node, List<NodeVo> results, String parentId) {
        NodeVo vo = NodeVo.fromDefaultNode(node, parentId);
        results.add(vo);
        String id = vo.getId();
        for (Node n : node.getChildList()) {
            visit((DefaultNode)n, results, id);
        }
    }
}
