package repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import json.DataSourceOptions;
import models.AbstractModel;
import utils.Page;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.sql.JoinType;
import play.db.jpa.JPA;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Repository<T extends AbstractModel> {

    default Session getSession() {
        return (Session) JPA.em().getDelegate();
    }

    default void addAliases(Criteria criteria, Map<String, JoinType> aliases) {
        if(aliases != null) aliases.forEach((k, v) -> criteria.createAlias(k, k, v));
    }

    List<T> parseJson(String json);
    ArrayNode removeFromJson(JsonNode jn);
    ArrayNode updateFromJson(JsonNode jn);
    ArrayNode getJsonList(Collection<?> l);

    T findById(Long id);
    T findByField(String field, Object value, String... aliases);
    T findByFields(Map<String, Object> restrictions, String... aliases);
    Page<T> paginate(Integer take, Integer skip, Map<String, JoinType> joins);
    Page<T> paginate(Integer take, Integer skip, String... aliases);
    Page<T> findAndPaginate(DataSourceOptions ds, Map<String, JoinType> aliases);
    Page<T> findAndPaginate(DataSourceOptions ds, String... aliases);
    List<T> find(DataSourceOptions ds, Map<String, JoinType> aliases);
    List<T> find(DataSourceOptions ds, String... aliases);
    List<T> findAll();

}
