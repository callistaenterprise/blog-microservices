/*
 * Copyright 2012-2015 the original author or authors.
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

package org.springframework.boot.actuate.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.Servlet;
import javax.servlet.ServletRegistration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} that records Servlet interactions
 * with a {@link CounterService} and {@link GaugeService}.
 *
 * @author Dave Syer
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
@Configuration
@ConditionalOnBean({ CounterService.class, GaugeService.class })
@ConditionalOnClass({ Servlet.class, ServletRegistration.class,
        OncePerRequestFilter.class, HandlerMapping.class })
@AutoConfigureAfter(MetricRepositoryAutoConfiguration.class)
@ConditionalOnProperty(name = "endpoints.metrics.filter.enabled", matchIfMissing = true)
public class MetricFilterAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(MetricFilterAutoConfiguration.class);

    @Autowired
    @Qualifier("dropwizardMetricServices")
    private CounterService counterService;

    @Autowired
    @Qualifier("dropwizardMetricServices")
    private GaugeService gaugeService;

    public MetricFilterAutoConfiguration() {
        LOG.debug("ML FIX for MetricFilterAutoConfiguration APPLIED - 1!");
    }

    @Bean
    public MetricsFilter metricFilter() {
        LOG.debug("ML FIX for MetricFilterAutoConfiguration APPLIED - 2!");
        return new MetricsFilter(this.counterService, this.gaugeService);
    }
}