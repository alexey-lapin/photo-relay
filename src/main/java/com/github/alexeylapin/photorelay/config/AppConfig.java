package com.github.alexeylapin.photorelay.config;

import com.github.alexeylapin.photorelay.HandleContext;
import com.github.alexeylapin.photorelay.Handler;
import com.github.alexeylapin.photorelay.Scheduler;
import com.github.alexeylapin.photorelay.Visit;
import com.github.alexeylapin.photorelay.config.def.ActionFactory;
import com.github.alexeylapin.photorelay.config.def.ConditionFactory;
import com.github.alexeylapin.photorelay.config.def.HandlerDef;
import com.github.alexeylapin.photorelay.config.def.factory.CompositeActionFactory;
import com.github.alexeylapin.photorelay.config.def.factory.CompositeConditionFactory;
import com.github.alexeylapin.photorelay.config.def.factory.FilesActionFactory;
import com.github.alexeylapin.photorelay.config.def.factory.MetadataConditionFactory;
import com.github.alexeylapin.photorelay.impl.DefaultHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(Handlers.class)
@EnableScheduling
public class AppConfig {

    @Bean
    public MetadataConditionFactory metadataConditionFactory() {
        return new MetadataConditionFactory();
    }

    @Bean
    public CompositeConditionFactory conditionFactory(List<ConditionFactory> delegates) {
        return new CompositeConditionFactory(delegates);
    }

    @Bean
    public FilesActionFactory filesActionFactory() {
        return new FilesActionFactory();
    }

    @Bean
    public CompositeActionFactory actionFactory(List<ActionFactory> delegates) {
        return new CompositeActionFactory(delegates);
    }

    @ConditionalOnProperty("visit.path")
    @Bean
    public Visit visit(List<Handler> handlers, @Value("${visit.path}") Path path) {
        handlers.sort(Comparator.comparingInt(Handler::getOrder));
        return new Visit(path, handlers);
    }

    @ConditionalOnProperty("visit.path")
    @Bean
    public Scheduler scheduler(Visit visit) {
        return new Scheduler(visit);
    }

    @Bean
    public List<Handler> handler(Handlers handlers,
                                 CompositeConditionFactory conditionFactory,
                                 CompositeActionFactory actionFactory) {
        List<Handler> handlersList = new ArrayList<>();
        for (HandlerDef handlerDef : handlers.getHandlers()) {
            handlerDef.validate();
            Predicate<HandleContext> predicate = conditionFactory.create(handlerDef.getCondition());
            Consumer<HandleContext> action = actionFactory.create(handlerDef.getAction());
            handlersList.add(new DefaultHandler(0, handlerDef.getName(), predicate, action));
        }

        return handlersList;
    }

}
