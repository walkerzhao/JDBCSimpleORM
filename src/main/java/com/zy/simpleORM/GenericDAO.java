package com.zy.simpleORM;

import java.util.List;
import java.util.Map;

//数据库操作的接口，常用的增删改查，以及按照条件来查询
public interface GenericDAO<T> {
	public void save(T t) throws Exception;   //增加数据
	
	public void delete(Object id, Class<T> clazz) throws Exception;  //删除数据
	
	public void update(T t) throws Exception;   //更新数据
	
	public T get(Object id, Class<T> clazz) throws Exception;   //查找数据
	
	public List<T> findAllByConditions(Map<String, Object> sqlWhereMap, Class<T> clazz ) throws Exception;   //根据搜索条件，批量查询数据
}
