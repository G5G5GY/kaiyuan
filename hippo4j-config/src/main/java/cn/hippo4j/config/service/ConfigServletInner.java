/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.config.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static cn.hippo4j.common.constant.Constants.WEIGHT_CONFIGS;

/**
 * Config servlet inner.
 */
@Service
@RequiredArgsConstructor
public class ConfigServletInner {

    @NonNull
    private final LongPollingService longPollingService;

    private final Cache<String, Long> deWeightCache = CacheBuilder.newBuilder()
            .maximumSize(1024)
            .build();

    /**
     * Poll configuration.
     *
     * @param request
     * @param response
     * @param clientMd5Map
     * @param probeRequestSize
     * @return
     */
    public String doPollingConfig(HttpServletRequest request, HttpServletResponse response, Map<String, String> clientMd5Map, int probeRequestSize) {
        if (LongPollingService.isSupportLongPolling(request) && weightVerification(request)) {
            longPollingService.addLongPollingClient(request, response, clientMd5Map, probeRequestSize);
            return HttpServletResponse.SC_OK + "";
        }
        return HttpServletResponse.SC_OK + "";
    }

    /**
     * Check whether the repeat request is repeated.
     * <p>
     * When a user proposes to deploy in the company environment, the same request will be called repeatedly.
     * This problem belongs to an extremely individual scenario. Since it cannot be reproduced, so first solve the problem in this way.
     *
     * @param request
     * @return
     */
    private boolean weightVerification(HttpServletRequest request) {
        String clientIdentify = request.getParameter(WEIGHT_CONFIGS);
        Long timeVal = deWeightCache.getIfPresent(clientIdentify);
        if (timeVal == null) {
            deWeightCache.put(clientIdentify, System.currentTimeMillis());
            return true;
        }
        return false;
    }
}
