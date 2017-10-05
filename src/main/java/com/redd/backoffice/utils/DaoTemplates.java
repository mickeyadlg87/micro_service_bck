package com.redd.backoffice.utils;

import cl.tastets.life.core.framework.dao.DataSourceDao;
import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.utils.Paginated;
import cl.tastets.life.objects.utils.QueryFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * interfaz general para los dao
 *
 * @author aleal
 */
public interface DaoTemplates {

    final Map<String, JdbcTemplate> jdbcTemplates = new HashMap<>();

    default JdbcTemplate getJdbcTemplate(DataSourceDao dao, String realm) {
        if (!jdbcTemplates.containsKey(realm)) {
            synchronized (jdbcTemplates) {
                jdbcTemplates.put(realm, new JdbcTemplate(dao.getSQLDataSource(realm)));
            }
        }
        return jdbcTemplates.get(realm);
    }

    default Integer getTotal(String realm, DataSourceDao dao, Supplier<String> query, Supplier<Object[]> parameters) {
        return getJdbcTemplate(dao, realm).queryForObject(query.get(), parameters.get(), Integer.class);
    }

    default Map<String, Object> getByCriteria(String realm, DataSourceDao dao, Supplier<String> query, Supplier<Object[]> parameters) {
        return getJdbcTemplate(dao, realm).queryForMap(query.get(), parameters.get());
    }

    default Collection getListByCriteria(String realm, DataSourceDao dao, Supplier<String> query, Supplier<Object[]> parameters) {
        return getJdbcTemplate(dao, realm).queryForList(query.get(), parameters.get());
    }

    default void fillFilterWithCriteria(Optional<QueryFilter> filter, BasicEntity k) {
        if (filter.isPresent()) {
            List filterParam = new ArrayList<>();
            filterParam.add(k);
            filter.get().put("filter", filterParam);
        } else {
            filter = Optional.of(QueryFilter.from());
            filter.get().getList("filter").add(k);
        }
    }

    /**
     * implementacion particular del criteria para el acceso a las entidades que
     * se desean consultar
     *
     * @param query
     * @param paginated
     * @param filter
     * @param sortPaginated
     * @return
     */
    String filterQueryValues(String query, Optional<Paginated> paginated, Optional<QueryFilter> filter, Boolean sortPaginated);
}
