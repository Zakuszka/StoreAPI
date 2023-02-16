package com.store.storeapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatasourceConfig {

	@Value("${spring-datasource-driverClassName}")
	private String driverClassName;

	@Value("${spring-datasource-url}")
	private String jdbcUrl;

	@Value("${spring-datasource-username}")
	private String username;

	@Value("${spring-datasource-password}")
	private String password;

	@Bean
	@Primary
	@ConfigurationProperties("spring.datasource.hikari")
	public DataSource datasource() {
		DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder
				.driverClassName(driverClassName)
				.url(jdbcUrl)
				.username(username)
				.password(password);

		return dataSourceBuilder.build();
	}
}
