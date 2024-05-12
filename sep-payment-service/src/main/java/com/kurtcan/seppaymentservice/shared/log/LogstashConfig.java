package com.kurtcan.seppaymentservice.shared.log;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.kurtcan.seppaymentservice.shared.constant.ProfileName;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.composite.loggingevent.LoggingEventPatternJsonProvider;
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
@Profile("!" + ProfileName.TEST)
public class LogstashConfig {

    @Value("${spring.application.name}")
    private String appName;

    private final LogstashProperties logstashProperties;

    @PostConstruct
    public void init() {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        LogstashTcpSocketAppender logstashTcpSocketAppender = new LogstashTcpSocketAppender();
        logstashTcpSocketAppender.setName("logstash");
        logstashTcpSocketAppender.setContext(loggerContext);
        logstashTcpSocketAppender.addDestination(STR."\{logstashProperties.getHost()}:\{logstashProperties.getPort()}");

        LoggingEventCompositeJsonEncoder encoder = new LoggingEventCompositeJsonEncoder();
        encoder.setContext(loggerContext);

        LoggingEventPatternJsonProvider patternProvider = getPatternProvider(loggerContext, appName);
        encoder.getProviders().addProvider(patternProvider);

        encoder.start();
        logstashTcpSocketAppender.setEncoder(encoder);
        logstashTcpSocketAppender.start();

        rootLogger.detachAndStopAllAppenders();
        rootLogger.addAppender(logstashTcpSocketAppender);
    }

    private static LoggingEventPatternJsonProvider getPatternProvider(LoggerContext loggerContext, String appName) {
        LoggingEventPatternJsonProvider patternProvider = new LoggingEventPatternJsonProvider();
        patternProvider.setPattern(STR."""
                {
                    "timestamp": "%date{yyyy-MM-dd'T'HH:mm:ss.SSSZ}",
                    "severity": "%level",
                    "service": "\{appName}",
                    "thread": "%thread",
                    "logger": "%logger",
                    "message": "%message"
                 }
                """);
        patternProvider.setContext(loggerContext);
        return patternProvider;
    }

}