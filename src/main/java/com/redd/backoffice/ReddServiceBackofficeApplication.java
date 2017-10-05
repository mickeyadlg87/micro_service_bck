package com.redd.backoffice;

import cl.tastets.life.core.framework.Core;
import cl.tastets.life.core.framework.actions.command.AbstractCommandController;
import cl.tastets.life.core.framework.actions.query.AbstractQueryController;
import cl.tastets.life.core.framework.dao.DataSourceDao;
import com.google.common.base.Predicate;
import static com.google.common.base.Predicates.or;
import java.util.Arrays;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoDataAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.ApiInfoBuilder;
import static springfox.documentation.builders.PathSelectors.regex;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class,
    MongoRepositoriesAutoConfiguration.class,
    MongoAutoConfiguration.class,
    MongoDataAutoConfiguration.class
})
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableSwagger2
public class ReddServiceBackofficeApplication {

    @Autowired
    protected Environment env;
    @Autowired
    private ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(ReddServiceBackofficeApplication.class, args).registerShutdownHook();
    }

    /**
     * Bean que realiza las dependencias de CQRS para la aplicacion, registra
     * los beans controllers de Query y de Command
     *
     * @return Core
     */
    @Bean
    public Core getCore() {
        Core core = new Core();
        context.getBeansOfType(AbstractQueryController.class).values().forEach((queryController) -> {
            core.registerQueryController(queryController);
        });
        context.getBeansOfType(AbstractCommandController.class).values().forEach((commandController) -> {
            core.registerCommandController(commandController);
        });

        return core;
    }

    /**
     * Container de distintos dataSources
     *
     * @return Container de datasources
     */
    @Bean
    public DataSourceDao dataSourceDao() {
        DataSourceDao metadata = new DataSourceDao();
        metadata.put("backoffice", dsBackoffice());
        metadata.put("rslite", dsRslite());
        return metadata;
    }

    /**
     * DataSource de backoffice
     *
     * @return Conexion a bd de backoffice
     */
    @Bean
    public DataSource dsBackoffice() {
        BasicDataSource dsBackOffice = new BasicDataSource();
        dsBackOffice.setDriverClassName("com.mysql.jdbc.Driver");
        dsBackOffice.setUrl(env.getProperty("backoffice.url"));
        dsBackOffice.setUsername(env.getProperty("backoffice.user"));
        dsBackOffice.setPassword(env.getProperty("backoffice.password"));
        dsBackOffice.setMaxActive(env.getProperty("backoffice.max", Integer.class));
        dsBackOffice.setValidationQuery("SELECT 1");
        dsBackOffice.setTestOnBorrow(true);
        return dsBackOffice;
    }
    
    /**
     * DataSource de rslite
     *
     * @return Conexion a bd de rslite
     */
    @Bean
    public DataSource dsRslite() {
        BasicDataSource dsRslite = new BasicDataSource();
        dsRslite.setDriverClassName("com.mysql.jdbc.Driver");
        dsRslite.setUrl(env.getProperty("lite.url"));
        dsRslite.setUsername(env.getProperty("lite.user"));
        dsRslite.setPassword(env.getProperty("lite.password"));
        dsRslite.setMaxActive(env.getProperty("lite.max", Integer.class));
        dsRslite.setValidationQuery("SELECT 1");
        dsRslite.setTestOnBorrow(true);
        return dsRslite;
    }
    
    @Bean
    public Docket apiGateway() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("backoffice-services")
                .apiInfo(apiInfo())
                .select()
                .paths(apiPaths())
                .build()
                .securitySchemes(Arrays.asList(new ApiKey("key", "api_key", "header")));
    }

    private Predicate<String> apiPaths() {
        return or(
                regex("/backoffice.*")
        );
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Backoffice Microservice")
                .description("Microservicio para Backoffice")
                .contact("Redd")
                .licenseUrl("http://www.gps.cl")
                .build();
    }

    @Bean
    public WebMvcConfigurerAdapter adapter() {
        return new WebMvcConfigurerAdapter() {

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                if (!registry.hasMappingForPattern("/webjars/**")) {
                    registry.addResourceHandler("/webjars/**").addResourceLocations(
                            "classpath:/META-INF/resources/webjars/");
                }

            }
        };
    }
}
