/*
 * Copyright 1999-2020 Alibaba Group Holding Ltd.
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
package com.uhasoft.guardian.adapter.okhttp;

import com.uhasoft.guardian.*;
import com.uhasoft.guardian.EntryType;
import com.uhasoft.guardian.ResourceTypeConstants;
import com.uhasoft.guardian.Tracer;
import com.uhasoft.guardian.slots.block.BlockException;
import com.uhasoft.guardian.util.AssertUtil;
import com.uhasoft.guardian.util.StringUtil;

import com.uhasoft.guardian.Entry;
import com.uhasoft.guardian.SphU;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author zhaoyuguang
 */
public class SentinelOkHttpInterceptor implements Interceptor {

    private final SentinelOkHttpConfig config;

    public SentinelOkHttpInterceptor() {
        this.config = new SentinelOkHttpConfig();
    }

    public SentinelOkHttpInterceptor(SentinelOkHttpConfig config) {
        AssertUtil.notNull(config, "config cannot be null");
        this.config = config;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Entry entry = null;
        try {
            Request request = chain.request();
            String name = config.getResourceExtractor().extract(request, chain.connection());
            if (StringUtil.isNotBlank(config.getResourcePrefix())) {
                name = config.getResourcePrefix() + name;
            }
            entry = SphU.entry(name, ResourceTypeConstants.COMMON_WEB, EntryType.OUT);
            return chain.proceed(request);
        } catch (BlockException e) {
            return config.getFallback().handle(chain.request(), chain.connection(), e);
        } catch (IOException ex) {
            Tracer.traceEntry(ex, entry);
            throw ex;
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }
    }
}