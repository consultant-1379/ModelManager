/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.sdk.modelmanager.services.configuration;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.ericsson.aia.metadata.api.MetaDataServiceIfc;
import com.ericsson.aia.metadata.exception.MetaDataServiceException;
import com.ericsson.component.aia.sdk.modelmanager.ModelManager;
import com.ericsson.component.aia.sdk.modelmanager.core.services.ModelManagerImpl;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * This will define beans needed for this application to run.
 *
 * @author ezsalro
 *
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = { MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, EmbeddedMongoAutoConfiguration.class,
        MongoRepositoriesAutoConfiguration.class, CassandraAutoConfiguration.class })
@EnableSwagger2
@ComponentScan(basePackages = { "com.ericsson.component.aia.sdk.modelmanager.services" })
public class ModelManagerApplication {

    @Autowired
    private ModelManagerServiceConfig modelManagerServiceConfig;

    @Autowired
    private MetaDataServiceIfc metaDataServiceIfc;

    /**
     * This method creates {@link ModelManager} bean.
     *
     * @return {@link ModelManager} bean.
     * @throws MetaDataServiceException
     *             if {@link MetaDataServiceIfc} bean creation fails.
     * @throws IOException
     *             if {@link MetaDataServiceIfc} bean creation fails.
     */
    @Bean
    public ModelManager modelManager() throws MetaDataServiceException, IOException {
        modelManagerServiceConfig.printConfiguration();
        modelManagerServiceConfig.updateApplicationManagerConfiguration();
        return ModelManagerImpl.builder().metaDataServiceManager(metaDataServiceIfc).build();
    }

    /**
     * Swagger Configuration
     *
     * @return Docket
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select().apis(RequestHandlerSelectors.any()).paths(paths()).build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Model Manager APIs").description("Lists Model Manager APIs").build();
    }

    private Predicate<String> paths() {
        return Predicates.and(PathSelectors.regex("/.*"), Predicates.not(PathSelectors.regex("/error.*")));
    }

    /**
     * Starting point of the application
     *
     * @param args
     *            arguments for the application.
     */
    public static void main(final String[] args) {
        //creating application
        SpringApplication.run(ModelManagerApplication.class, args);
    }
}
