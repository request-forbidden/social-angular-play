package json;

public class FilterDetails extends Filter {
	public String field;
	public FilterOperations operator;
	public String value;

	public FilterDetails(String field, String operator, String value) {
		this.field = field;
		this.operator = parseOperator(operator);
		this.value = value;
	}

	public static FilterOperations parseOperator(String theOperator) {
		theOperator = theOperator.toLowerCase();
		switch (theOperator) {
			//equal ==
			case "eq":
			case "==":
			case "isequalto":
			case "equals":
			case "equalto":
			case "equal":
				return FilterOperations.Equals;
			//not equal !=
			case "neq":
			case "!=":
			case "isnotequalto":
			case "notequals":
			case "notequalto":
			case "notequal":
			case "ne":
				return FilterOperations.NotEquals;
			// Greater
			case "gt":
			case ">":
			case "isgreaterthan":
			case "greaterthan":
			case "greater":
				return FilterOperations.Greater;
			// Greater or equal
			case "gte":
			case ">=":
			case "isgreaterthanorequalto":
			case "greaterthanequal":
			case "ge":
				return FilterOperations.GreaterOrEquals;
			// Less
			case "lt":
			case "<":
			case "islessthan":
			case "lessthan":
			case "less":
				return FilterOperations.LessThan;
			// Less or equal
			case "lte":
			case "<=":
			case "islessthanorequalto":
			case "lessthanequal":
			case "le":
				return FilterOperations.LessThanOrEquals;
			case "startswith":
				return FilterOperations.StartsWith;

			case "endswith":
				return FilterOperations.EndsWith;
			//string.Contains()
			case "contains":
				return FilterOperations.Contains;
			case "doesnotcontain":
				return FilterOperations.NotContains;
			default:
				return FilterOperations.Contains;
		}
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public FilterOperations getOperator() {
		return operator;
	}

	public void setOperator(FilterOperations operator) {
		this.operator = operator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}