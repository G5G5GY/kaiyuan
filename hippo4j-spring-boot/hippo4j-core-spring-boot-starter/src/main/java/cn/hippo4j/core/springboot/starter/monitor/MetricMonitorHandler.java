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

package cn.hippo4j.core.springboot.starter.monitor;

import cn.hippo4j.adapter.base.ThreadPoolAdapterState;
import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;
import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * Metric monitor handler.
 *
 * @author chen.ma
 * @date 2022/3/25 20:37
 */
public class MetricMonitorHandler extends AbstractDynamicThreadPoolMonitor {

    private final static String METRIC_NAME_PREFIX = "dynamic.thread-pool";

    private final static String DYNAMIC_THREAD_POOL_ID_TAG = METRIC_NAME_PREFIX + ".id";

    private final static String APPLICATION_NAME_TAG = "application.name";

    private final Map<String, ThreadPoolRunStateInfo> RUN_STATE_CACHE = Maps.newConcurrentMap();

    private final Map<String, ThreadPoolAdapterState> ADAPTER_STATE_CACHE = Maps.newConcurrentMap();

    public MetricMonitorHandler(ThreadPoolRunStateHandler threadPoolRunStateHandler) {
        super(threadPoolRunStateHandler);
    }

    @Override
    protected void execute(ThreadPoolRunStateInfo poolRunStateInfo) {
        ThreadPoolRunStateInfo stateInfo = RUN_STATE_CACHE.get(poolRunStateInfo.getTpId());
        if (stateInfo == null) {
            RUN_STATE_CACHE.put(poolRunStateInfo.getTpId(), poolRunStateInfo);
        } else {
            BeanUtil.copyProperties(poolRunStateInfo, stateInfo);
        }

        Environment environment = ApplicationContextHolder.getInstance().getEnvironment();
        String applicationName = environment.getProperty("spring.application.name", "application");
        Iterable<Tag> tags = Lists.newArrayList(
                Tag.of(DYNAMIC_THREAD_POOL_ID_TAG, poolRunStateInfo.getTpId()),
                Tag.of(APPLICATION_NAME_TAG, applicationName));

        // load
        Metrics.gauge(metricName("current.load"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getSimpleCurrentLoad);
        Metrics.gauge(metricName("peak.load"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getSimplePeakLoad);
        // thread pool
        Metrics.gauge(metricName("core.size"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getCoreSize);
        Metrics.gauge(metricName("maximum.size"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getMaximumSize);
        Metrics.gauge(metricName("current.size"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getPoolSize);
        Metrics.gauge(metricName("largest.size"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getLargestPoolSize);
        Metrics.gauge(metricName("active.size"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getActiveSize);
        // queue
        Metrics.gauge(metricName("queue.size"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getQueueSize);
        Metrics.gauge(metricName("queue.capacity"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getQueueCapacity);
        Metrics.gauge(metricName("queue.remaining.capacity"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getQueueRemainingCapacity);
        // other
        Metrics.gauge(metricName("completed.task.count"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getCompletedTaskCount);
        Metrics.gauge(metricName("reject.count"), tags, poolRunStateInfo, ThreadPoolRunStateInfo::getRejectCount);
    }

    @Override
    protected void execute(ThreadPoolAdapterState poolAdapterState) {
        ThreadPoolAdapterState stateInfo = ADAPTER_STATE_CACHE.get(poolAdapterState.getThreadPoolKey());
        if (stateInfo == null) {
            ADAPTER_STATE_CACHE.put(poolAdapterState.getThreadPoolKey(), poolAdapterState);
        } else {
            BeanUtil.copyProperties(poolAdapterState, stateInfo);
        }

        Environment environment = ApplicationContextHolder.getInstance().getEnvironment();
        String applicationName = environment.getProperty("spring.application.name", "application");
        Iterable<Tag> tags = Lists.newArrayList(
                Tag.of(DYNAMIC_THREAD_POOL_ID_TAG, poolAdapterState.getThreadPoolKey()),
                Tag.of(APPLICATION_NAME_TAG, applicationName));

        // thread pool
        Metrics.gauge(metricName("core.size"), tags, poolAdapterState, ThreadPoolAdapterState::getCoreSize);
        Metrics.gauge(metricName("maximum.size"), tags, poolAdapterState, ThreadPoolAdapterState::getMaximumSize);
        Metrics.gauge(metricName("current.size"), tags, poolAdapterState, ThreadPoolAdapterState::getPoolSize);
        Metrics.gauge(metricName("active.size"), tags, poolAdapterState, ThreadPoolAdapterState::getActiveSize);
        // queue
        Metrics.gauge(metricName("queue.capacity"), tags, poolAdapterState, ThreadPoolAdapterState::getBlockingQueueCapacity);
        Metrics.gauge(metricName("queue.size"), tags, poolAdapterState, ThreadPoolAdapterState::getQueueSize);
        Metrics.gauge(metricName("queue.remaining.capacity"), tags, poolAdapterState, ThreadPoolAdapterState::getRemainingCapacity);
        // other
        Metrics.gauge(metricName("completed.task.count"), tags, poolAdapterState, ThreadPoolAdapterState::getCompletedTaskCount);
    }

    private String metricName(String name) {
        return String.join(".", METRIC_NAME_PREFIX, name);
    }

    @Override
    public String getType() {
        return "metric";
    }
}
