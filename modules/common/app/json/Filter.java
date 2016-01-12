package json;

import java.util.ArrayList;
import java.util.List;

public class Filter {
	List<FilterDetails> filters;
	String logic;

	public void addFilter(FilterDetails fd) {
		if(filters == null) {
			filters = new ArrayList<>();
		}
		filters.add(fd);
	}

	public List<FilterDetails> getFilters() {
		return filters;
	}

	public void setFilters(List<FilterDetails> filters) {
		this.filters = filters;
	}

	public String getLogic() {
		return logic;
	}

	public void setLogic(String logic) {
		this.logic = logic;
	}

}
