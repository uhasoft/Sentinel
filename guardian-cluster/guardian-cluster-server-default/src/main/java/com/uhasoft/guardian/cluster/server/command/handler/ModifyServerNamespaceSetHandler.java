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

import java.net.URLDecoder;
import java.util.Set;

import com.uhasoft.guardian.cluster.server.config.ClusterServerConfigManager;
import com.uhasoft.guardian.command.CommandHandler;
import com.uhasoft.guardian.command.CommandRequest;
import com.uhasoft.guardian.command.CommandResponse;
import com.uhasoft.guardian.command.annotation.CommandMapping;
import com.uhasoft.guardian.log.RecordLog;
import com.uhasoft.guardian.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

/**
 * @author Eric Zhao
 * @since 1.4.0
 */
@CommandMapping(name = "cluster/server/modifyNamespaceSet", desc = "modify server namespace set")
public class ModifyServerNamespaceSetHandler implements CommandHandler<String> {

    @Override
    public CommandResponse<String> handle(CommandRequest request) {
        String data = request.getParam("data");
        if (StringUtil.isBlank(data)) {
            return CommandResponse.ofFailure(new IllegalArgumentException("empty data"));
        }
        try {
            data = URLDecoder.decode(data, "utf-8");
            RecordLog.info("[ModifyServerNamespaceSetHandler] Receiving cluster server namespace set: " + data);
            Set<String> set = JSON.parseObject(data, new TypeReference<Set<String>>() {});
            ClusterServerConfigManager.loadServerNamespaceSet(set);
            return CommandResponse.ofSuccess("success");
        } catch (Exception e) {
            RecordLog.warn("[ModifyServerNamespaceSetHandler] Decode cluster server namespace set error", e);
            return CommandResponse.ofFailure(e, "decode client cluster config error");
        }
    }
}
