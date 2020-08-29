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
package com.uhasoft.guardian.cluster.server.codec.netty;

import com.uhasoft.guardian.cluster.ClusterConstants;
import com.uhasoft.guardian.cluster.codec.response.ResponseEntityWriter;
import com.uhasoft.guardian.cluster.response.ClusterResponse;
import com.uhasoft.guardian.cluster.response.Response;
import com.uhasoft.guardian.cluster.server.codec.ServerEntityCodecProvider;
import com.uhasoft.guardian.log.RecordLog;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author Eric Zhao
 * @since 1.4.0
 */
public class NettyResponseEncoder extends MessageToByteEncoder<ClusterResponse> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ClusterResponse response, ByteBuf out) throws Exception {
        ResponseEntityWriter<ClusterResponse, ByteBuf> responseEntityWriter = ServerEntityCodecProvider.getResponseEntityWriter();
        if (responseEntityWriter == null) {
            RecordLog.warn("[NettyResponseEncoder] Cannot resolve the global response entity writer, reply bad status");
            writeBadStatusHead(response, out);
            return;
        }

        responseEntityWriter.writeTo(response, out);
    }


    private void writeBadStatusHead(Response response, ByteBuf out) {
        out.writeInt(response.getId());
        out.writeByte(ClusterConstants.RESPONSE_STATUS_BAD);
        out.writeByte(response.getStatus());
    }
}
