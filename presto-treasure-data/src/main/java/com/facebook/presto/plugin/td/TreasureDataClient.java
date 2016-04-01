/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.plugin.td;

import com.facebook.presto.plugin.jdbc.BaseJdbcClient;
import com.facebook.presto.plugin.jdbc.BaseJdbcConfig;
import com.facebook.presto.plugin.jdbc.JdbcConnectorId;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Locale;
import java.util.Set;
import javax.inject.Inject;

/**
 *
 * @author zerosign
 */
public class TreasureDataClient extends BaseJdbcClient {
    
    @Inject
    public TreasureDataClient(JdbcConnectorId connectorId, BaseJdbcConfig config, String identifierQuote, Driver driver) {
        super(connectorId, config, identifierQuote, driver);
        connectionProperties.setProperty("user", config.getConnectionUser());
        connectionProperties.setProperty("password", config.getConnectionPassword());
    }

    @Override
    public PreparedStatement getPreparedStatement(Connection connection, String sql) throws SQLException {
        final PreparedStatement statement = connection.prepareStatement(sql);
        return statement;
    }

    @Override
    public final Set<String> getSchemaNames() {
        try(final Connection connection = driver.connect(connectionUrl, connectionProperties)) {
            
            final ResultSet results  = connection.getMetaData().getCatalogs();
            final ImmutableSet.Builder<String> schemas = ImmutableSet.builder();
            
            while(results.next()) {
                final String schema = results.getString("TABLE_CAT").toLowerCase(Locale.ENGLISH);
                schemas.add(schema);
            }
            
            return schemas.build();
            
        } catch (SQLException ex) {
            throw Throwables.propagate(ex);
        }
    }

    @Override
    protected final ResultSet getTables(final Connection connection, final String schemaName, final String tableName) throws SQLException {
       final DatabaseMetaData metadata = connection.getMetaData();
       return metadata.getTables(schemaName, null, "%", new String[]{"TABLE", "VIEW"});
    }
    
}
