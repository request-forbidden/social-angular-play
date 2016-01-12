package utils;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

import java.util.List;

public class Page<T extends models.AbstractModel> {
    private List<T> data;
    private Integer total;
    private Integer take;
    private Integer skip;

    public Page(List<T> data, Integer total, Integer take, Integer skip) {
        this.data = data;
        this.total = total;
        this.take = take;
        this.skip = skip;
    }

    public JsonNode getJson() {
        return Json.toJson(this);
    }
}
