package json;

import com.fasterxml.jackson.databind.JsonNode;

import utils.ReflectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import play.libs.Json;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DataSourceOptions {

	private Integer take;
	private Integer skip;

	private Filter filter;
	private List<Order> sort;

	public static DataSourceOptions parse(String json) {
		return parse(Json.parse(json));
	}

	public static DataSourceOptions parse(JsonNode jn) {
		return Json.fromJson(jn, DataSourceOptions.class);
	}

    public DataSourceOptions() {}

    public DataSourceOptions(Map<String, Object> filters) {
        this(filters, null);
    }

	public DataSourceOptions(Map<String, Object> filters, Map<String, String> order) {
        this.build(filters, order);
    }

    private void build(Map<String, Object> filters, Map<String, String> order) {
		if(filters != null) {
			this.filter = new Filter();
			filter.setLogic("and");
			filters.forEach((key, value) -> {
                String operator;
                if(value instanceof String) {
                    operator = "contains";
                } else {
                    operator = "equals";
                }

                filter.addFilter(new FilterDetails(key, operator, value.toString()));
            });
		}

		if(order != null) {
			this.sort = new ArrayList<>();
			order.forEach((key, value) -> sort.add(new Order(key, value)));
		}
    }

	public boolean isEmpty() {
		return take == null && skip == null && filter == null;
	}

	public void paginateQuery(Criteria criteria) {
		if(take != null && skip != null) {
			criteria.setFirstResult(skip);
			criteria.setMaxResults(take);
		}
	}

	public void addOrderCriteria(Criteria criteria) {
		if(sort != null) {
			for(Order order : sort) {
				if(order.dir.equals("asc")) {
					criteria.addOrder(org.hibernate.criterion.Order.asc(order.field));
				}
				else if(order.dir.equals("desc")) {
					criteria.addOrder(org.hibernate.criterion.Order.desc(order.field));
				}
			}
		}
	}

	public List<Criterion> getRestrictionsFromFilter(List<FilterDetails> details, Class clazz) {
		List<Criterion> restrictions = new ArrayList<>();
		for(FilterDetails fd : details) {
            if(fd.getLogic() != null && fd.getFilters() != null) {
                List<Criterion> innerRestrictions = getRestrictionsFromFilter(fd.getFilters(), clazz);
                if(fd.getLogic().equals("and")) {
                    restrictions.add(Restrictions.and(innerRestrictions.toArray(new Criterion[restrictions.size()])));
                }
                else if(fd.getLogic().equals("or")) {
                    restrictions.add(Restrictions.or(innerRestrictions.toArray(new Criterion[restrictions.size()])));
                }
                continue;
            }
			Object value = parse(fd.getValue(), clazz, fd.getField());
			if(value == null) {
				value = fd.getValue();
			}
			switch(fd.getOperator()) {
				case Contains:
					restrictions.add(Restrictions.ilike(fd.getField(), "%" + value + "%"));
					break;
				case StartsWith:
					restrictions.add(Restrictions.ilike(fd.getField(), "" + value + "%"));
					break;
				case EndsWith:
					restrictions.add(Restrictions.ilike(fd.getField(), "%" + value + ""));
					break;
				case Equals:
					restrictions.add(Restrictions.eq(fd.getField(), value));
					break;
				case Greater:
					restrictions.add(Restrictions.gt(fd.getField(), value));
					break;
				case GreaterOrEquals:
					restrictions.add(Restrictions.ge(fd.getField(), value));
					break;
				case LessThan:
					restrictions.add(Restrictions.lt(fd.getField(), value));
					break;
				case LessThanOrEquals:
					restrictions.add(Restrictions.le(fd.getField(), value));
					break;
				case NotContains:
					restrictions.add(Restrictions.not(Restrictions.ilike(fd.getField(), "%" + value + "%")));
					break;
				case NotEquals:
					restrictions.add(Restrictions.ne(fd.getField(), value));
					break;
				default:
					restrictions.add(Restrictions.ilike(fd.getField(), "%" + value + "%"));
					break;
			}
		}
        return restrictions;
	}

	public void addRestrictionsCriteria(Criteria criteria, Class clazz) {
		if(filter != null) {
            List<Criterion> restrictions = getRestrictionsFromFilter(filter.getFilters(), clazz);
			if(filter.getLogic().equals("and")) {
				criteria.add(Restrictions.and(restrictions.toArray(new Criterion[restrictions.size()])));
			}
			else if(filter.getLogic().equals("or")) {
				criteria.add(Restrictions.or(restrictions.toArray(new Criterion[restrictions.size()])));
			}
		}
	}

	public Object parse(String s, Class<?> clazz, String fieldName) {
		Object result;
		Field field = null;
		try {
			if(fieldName == null) return s;

            if(fieldName.contains(".")) {
				String[] classes = fieldName.split("\\.");
				if(classes.length > 2) {
					play.Logger.warn("Too many objects on on the way, didn't try finding type, returning as string.");
					return s;
				}
                String fieldNameCurrentClass = classes[0];
                String fieldNameChildClass = classes[1];

                Field nodeField = ReflectionUtils.getFieldUpTo(fieldNameCurrentClass, clazz, Object.class);
                field = nodeField.getType().getDeclaredField(fieldNameChildClass);
            }

            if(field == null) field = ReflectionUtils.getFieldUpTo(fieldName, clazz, Object.class);

			if(field.getType() == Integer.class || field.getGenericType() == Integer.TYPE) {
				try {
					result = Integer.parseInt(s);
					return result;
				} catch (NumberFormatException nx) {
					play.Logger.debug("not int");
				}
			}
			else if(field.getType() == Long.class || field.getGenericType() == Long.TYPE) {
				try {
					result = Long.parseLong(s);
					return result;
				} catch (NumberFormatException nx) {
					play.Logger.debug("not long");
				}
			}
			else if(field.getType() == Float.class || field.getGenericType() == Float.TYPE) {
				try {
					result = Float.parseFloat(s);
					return result;
				} catch (NumberFormatException nx) {
					play.Logger.debug("not float");
				}
			}
			else if(field.getType() == Double.class || field.getGenericType() == Double.TYPE) {
				try {
					result = Double.parseDouble(s);
					return result;
				} catch (NumberFormatException nx) {
					play.Logger.debug("not float");
				}
			}
			else if(field.getType() == Boolean.class || field.getGenericType() == Boolean.TYPE) {
				try {
					result = Boolean.parseBoolean(s);
					return result;
				} catch (NumberFormatException nx) {
					play.Logger.debug("not float");
				}
			}
			else if(field.getType() == Date.class) {
				try {
					result = new Date(Long.parseLong(s));
					return result;
				} catch (NumberFormatException nx) {
					play.Logger.debug("not date");
				}
			}
			else if(field.getType() == DateTime.class) {
				try {
					DateTime dt = new DateTime(s);
					return dt;
				} catch (NumberFormatException nx) {
					play.Logger.debug("not joda date");
				}
			}
			else if(field.getType() == Character.class || field.getGenericType() == Character.TYPE) {
				if(s != null && s.length() == 1) {
					Character c = s.charAt(0);
					return c;
				}
			} else if(field.getType().isEnum()) {
				try {
					Integer index = Integer.parseInt(s);
					return field.getType().getEnumConstants()[index];
				}
				catch(NumberFormatException ex) {
					play.Logger.warn(ex.getMessage());
				}
			}
		} catch (NoSuchFieldException e) {
			play.Logger.warn(e.getMessage());
			try {
				result = Integer.parseInt(s);
				return result;
			}
			catch(NumberFormatException ex) {
				play.Logger.warn(ex.getMessage());
			}
			return s;
		}
		return null;
	}


	public void setTake(Integer take) {
		this.take = take;
	}

	public void setSkip(Integer skip) {
		this.skip = skip;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public Filter getFilter() {
		return filter;
	}

	public Integer getSkip() {
		return skip;
	}

	public Integer getTake() {
		return take;
	}

	public List<Order> getSort() {
		return sort;
	}

	public void setSort(List<Order> sort) {
		this.sort = sort;
	}
}
