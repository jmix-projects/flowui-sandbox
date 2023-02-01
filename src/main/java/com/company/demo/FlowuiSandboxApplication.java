package com.company.demo;

import com.company.demo.component.filter.registration.FilterComponentRegistration;
import com.company.demo.component.filter.registration.FilterComponentRegistrationBuilder;
import com.company.demo.component.groupfilter.GroupFilter;
import com.company.demo.component.groupfilter.GroupFilterConverter;
import com.company.demo.component.jpqlfilter.JpqlFilterConverter;
import com.company.demo.component.propertyfilter.PropertyFilterConverter;
import com.company.demo.entity.filter.GroupFilterCondition;
import com.company.demo.entity.filter.JpqlFilterCondition;
import com.company.demo.entity.filter.PropertyFilterCondition;
import com.google.common.base.Strings;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import io.jmix.flowui.component.jpqlfilter.JpqlFilter;
import io.jmix.flowui.component.propertyfilter.PropertyFilter;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@SpringBootApplication
@Theme(value = "flowuisandbox", variant = Lumo.LIGHT)
@PWA(name = "Jmix Flow UI Sandbox", shortName = "Jmix Flow UI Sandbox")
public class FlowuiSandboxApplication implements AppShellConfigurator {

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(FlowuiSandboxApplication.class, args);
    }

    @Bean
    @Primary
    @ConfigurationProperties("main.datasource")
    DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("main.datasource.hikari")
    DataSource dataSource(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @EventListener
    public void printApplicationUrl(ApplicationStartedEvent event) {
        LoggerFactory.getLogger(FlowuiSandboxApplication.class).info("Application started at "
                + "http://localhost:"
                + environment.getProperty("local.server.port")
                + Strings.nullToEmpty(environment.getProperty("server.servlet.context-path")));
    }




    @Bean("flowui_PropertyFilterRegistration")
    public FilterComponentRegistration registerPropertyFilter() {
        return FilterComponentRegistrationBuilder.create(PropertyFilter.class,
                        PropertyFilterCondition.class,
                        PropertyFilterConverter.class)
                .build();
    }

    @Bean("flowui_JpqlFilterRegistration")
    public FilterComponentRegistration registerJpqlFilter() {
        return FilterComponentRegistrationBuilder.create(JpqlFilter.class,
                        JpqlFilterCondition.class,
                        JpqlFilterConverter.class)
                .build();
    }

    @Bean("flowui_GroupFilterRegistration")
    public FilterComponentRegistration registerGroupFilter() {
        return FilterComponentRegistrationBuilder.create(GroupFilter.class,
                        GroupFilterCondition.class,
                        GroupFilterConverter.class)
                .build();
    }
}
