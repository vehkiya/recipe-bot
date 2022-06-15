package io.github.vehkiya.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import javax.sql.DataSource


@EnableAutoConfiguration
@EnableConfigurationProperties
@EntityScan(basePackages = ["io.github.vehkiya.data.model.persistence"])
@EnableJpaRepositories(basePackages = ["io.github.vehkiya.data.repository"])
class PersistenceConfiguration {

    @Bean
    @ConfigurationProperties("datasource.h2")
    fun dataSourceProperties(): DataSourceProperties = DataSourceProperties()

    @Bean
    fun dataSource(dataSourceProperties: DataSourceProperties): DataSource {
        return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource::class.java).build()
    }
}