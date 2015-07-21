package com.zy.simpleORM;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.zy.annotation.Column;
import com.zy.annotation.Entity;
import com.zy.annotation.Id;
 
//数据库常用操作的实现
public class GenericDAOImpl<T> implements GenericDAO<T> {
	private static final String TABLE_ALIAS = "t";
	//添加数据：1.获取表名，2.获取字段、字段值，3.拼接sql语句，4.执行sql语句，5.释放资源
	//insert into t_book (t_isbn,t_name,t_author,t_publishing,t_pubdate,t_price) values (?,?,?,?,?,?)
	@Override
	public void save(T t) throws Exception {
		// TODO Auto-generated method stub
		Class<?> clazz = t.getClass();
		//获取表名
		String tableName = getTableName(clazz);
		
		StringBuilder fieldNames = new StringBuilder();  //存储字段名字
		List<Object> fieldValues = new ArrayList<Object>(); //存储字段值
		StringBuilder placeholders = new StringBuilder();   //占位符
		//获取字段名字和值
		Field[] fields = clazz.getDeclaredFields();
		for(Field field:fields) {
			PropertyDescriptor pd = new PropertyDescriptor(field.getName(), t.getClass());
			if(field.isAnnotationPresent(Id.class)) {
				fieldNames.append(field.getAnnotation(Id.class).value()).append(",");
				fieldValues.add(pd.getReadMethod().invoke(t));
			} else if(field.isAnnotationPresent(Column.class)) {
				fieldNames.append(field.getAnnotation(Column.class).value()).append(",");
				fieldValues.add(pd.getReadMethod().invoke(t));
			}
			placeholders.append("?").append(",");
		}
		
		//删除最后一个逗号
		fieldNames.deleteCharAt(fieldNames.length()-1);
		placeholders.deleteCharAt(placeholders.length()-1);
		
		//拼接sql
		StringBuilder sql = new StringBuilder("");

		sql.append("insert into ").append(tableName).append(" (").append(fieldNames.toString())
		.append(") values (").append(placeholders).append(")");
		PreparedStatement ps = JdbcDaoHelper.getConnection().prepareStatement(sql.toString()); 
		 //设置SQL参数占位符的值  
        setParameter(fieldValues, ps, false);  
        //执行sql语句
        ps.execute();
        JdbcDaoHelper.release(ps, null);
		
        System.out.println(sql+"\n"+clazz.getSimpleName()+"添加成功");
	}

	//删除数据
	//delete from t_book where id = 1
	@Override
	public void delete(Object id, Class<T> clazz)  throws Exception {
		// TODO Auto-generated method stub
		//获取表名
		String tableName = getTableName(clazz);
	
		StringBuilder sql = new StringBuilder("");
		sql.append("delete from ").append(tableName).append(" where id = ").append(id);
		PreparedStatement ps = JdbcDaoHelper.getConnection().prepareStatement(sql.toString());
		ps.execute();
		JdbcDaoHelper.release(ps, null);
		System.out.println(sql+"\n"+clazz.getSimpleName()+"删除成功");

	}
	//根据id来进行更新数据
	//update t_book set t_name=?,t_author=?,t_publishing=?,t_pubdate=?,t_price=? where t_isbn=?
	@Override
	public void update(T t) throws Exception {
		// TODO Auto-generated method stub
		Class<?> clazz = t.getClass();
		//获取表名
		String tableName = getTableName(clazz);
		

		List<String> fieldNames = new ArrayList<String>();  //存储字段名字
		List<Object> fieldValues = new ArrayList<Object>(); //存储字段值
		List<String> placeholders = new ArrayList<String>();   //占位符
		
		
		String idFieldName = "";  
        Object idFieldValue = "";
		//获取字段名字和值
		Field[] fields = clazz.getDeclaredFields();
		for(Field field:fields) {
			PropertyDescriptor pd = new PropertyDescriptor(field.getName(), t.getClass());
			if(field.isAnnotationPresent(Id.class)) {
				idFieldName = field.getAnnotation(Id.class).value();
				idFieldValue = pd.getReadMethod().invoke(t);
			} else if(field.isAnnotationPresent(Column.class)) {
				fieldNames.add(field.getAnnotation(Column.class).value());
				fieldValues.add(pd.getReadMethod().invoke(t));
				placeholders.add("?");
			}			
		}
		//ID作为更新条件，放在集合中的最后一个元素  
        fieldNames.add(idFieldName);  
        fieldValues.add(idFieldValue);  
        placeholders.add("?");  
		
		
		StringBuilder sql = new StringBuilder("");
		sql.append("update " ).append(tableName).append(" set ");
		int index = fieldNames.size()-1;
		for(int i=0; i<index; i++) {
			sql.append(fieldNames.get(i)).append("=").append(placeholders.get(i)).append(",");
		}
		sql.deleteCharAt(sql.length()-1).append(" where ").append(idFieldName).append("=").append("?");
		PreparedStatement ps = JdbcDaoHelper.getConnection().prepareStatement(sql.toString()); 
		 //设置SQL参数占位符的值  
       setParameter(fieldValues, ps, false);  
       
       //执行sql语句
       ps.execute();
       JdbcDaoHelper.release(ps, null);
       System.out.println(sql+"\n"+clazz.getSimpleName()+"更新成功");
	}

	//select t.t_isbn as isbn,t.t_name as name,t.t_author as author,t.t_publishing as publishing,t.t_pubdate as pubdate,t.t_price as price from t_book t where t.t_isbn like ? 
	@Override
	public T get(Object id, Class<T> clazz) throws Exception {
		// TODO Auto-generated method stub
	
		String idFieldName = "";  
        Field[] fields = clazz.getDeclaredFields();  
        boolean flag = false;  
        for (Field field : fields) {  
            if (field.isAnnotationPresent(Id.class)) {  
                idFieldName = field.getAnnotation(Id.class).value();  
                flag = true;  
                break;  
            }   
        }  
          
        if (!flag) {  
            throw new Exception(clazz.getName() + " object not found id property.");  
        }  
          
        //拼装SQL  
        Map<String,Object> sqlWhereMap = new HashMap<String, Object>();  
        sqlWhereMap.put(TABLE_ALIAS + "." + idFieldName, id);  
          
        List<T> list = findAllByConditions(sqlWhereMap, clazz); 
        
        return list.size() > 0 ? list.get(0) : null;
	}

	@Override
	public List<T> findAllByConditions(Map<String, Object> sqlWhereMap, Class<T> clazz) throws Exception {
		// TODO Auto-generated method stub
		List<T> list = new ArrayList<T>();  
        String tableName = getTableName(clazz);  
        String idFieldName = "";  
        //存储所有字段的信息  
        //通过反射获得要查询的字段  
        StringBuffer fieldNames = new StringBuffer();  
        Field[] fields = clazz.getDeclaredFields();  
        for (Field field : fields) {  
            String propertyName = field.getName();  
            if (field.isAnnotationPresent(Id.class)) {  
                idFieldName = field.getAnnotation(Id.class).value();  
                fieldNames.append(TABLE_ALIAS + "." + idFieldName)  
                          .append(" as ").append(propertyName).append(",");  
            } else if (field.isAnnotationPresent(Column.class)) {  
                fieldNames.append(TABLE_ALIAS + "." + field.getAnnotation(Column.class).value())  
                          .append(" as ").append(propertyName).append(",");  
            }  
        }  
        fieldNames.deleteCharAt(fieldNames.length()-1);  
          
        //拼装SQL  
        String sql = "select " + fieldNames + " from " + tableName + " " + TABLE_ALIAS;  
        PreparedStatement ps = null;  
        List<Object> values = null;  
        if (sqlWhereMap != null) {  
            List<Object> sqlWhereWithValues = getSqlWhereWithValues(sqlWhereMap);  
            if (sqlWhereWithValues != null) {  
                //拼接SQL条件  
                String sqlWhere = (String)sqlWhereWithValues.get(0);  
                sql += sqlWhere;  
                //得到SQL条件中占位符的值  
                values = (List<Object>) sqlWhereWithValues.get(1);  
            }  
        }   
          
        //设置参数占位符的值  
        if (values != null) {  
            ps = JdbcDaoHelper.getConnection().prepareStatement(sql);  
            setParameter(values, ps, true);  
        } else {  
            ps = JdbcDaoHelper.getConnection().prepareStatement(sql);  
        }  
          
          
        //执行SQL  
        ResultSet rs = ps.executeQuery();  
        while(rs.next()) {  
            T t = clazz.newInstance();  
            initObject(t, fields, rs);  
            list.add(t);  
        }  
          
        //释放资源  
        JdbcDaoHelper.release(ps, rs);  
          
        System.out.println(sql);  
        return list;  
	}
	
	/** 
     * 获得表名 
     */  
    private String getTableName(Class<?> clazz) throws Exception {  
        if (clazz.isAnnotationPresent(Entity.class)) {  
            Entity entity = clazz.getAnnotation(Entity.class);  
            return entity.value();  
        } else {  
            throw new Exception(clazz.getName() + " is not Entity Annotation.");  
        }  
    }  
	
	/** 
     * 设置SQL参数占位符的值 
     */  
    private void setParameter(List<Object> values, PreparedStatement ps, boolean isSearch)  
            throws SQLException {  
        for (int i = 1; i <= values.size(); i++) {  
            Object fieldValue = values.get(i-1);  
            Class<?> clazzValue = fieldValue.getClass();  
            if (clazzValue == String.class) {  
                if (isSearch)   
                    ps.setString(i, "%" + (String)fieldValue + "%");  
                else  
                    ps.setString(i,(String)fieldValue);  
                      
            } else if (clazzValue == boolean.class || clazzValue == Boolean.class) {  
                ps.setBoolean(i, (Boolean)fieldValue);  
            } else if (clazzValue == byte.class || clazzValue == Byte.class) {  
                ps.setByte(i, (Byte)fieldValue);  
            } else if (clazzValue == char.class || clazzValue == Character.class) {  
                ps.setObject(i, fieldValue,Types.CHAR);  
            } else if (clazzValue == Date.class) {  
                ps.setTimestamp(i, new Timestamp(((Date) fieldValue).getTime()));  
            } else if (clazzValue.isArray()) {  
                Object[] arrayValue = (Object[]) fieldValue;  
                StringBuffer sb = new StringBuffer();  
                for (int j = 0; j < arrayValue.length; j++) {  
                    sb.append(arrayValue[j]).append("、");  
                }  
                ps.setString(i, sb.deleteCharAt(sb.length()-1).toString());  
            } else {  
                ps.setObject(i, fieldValue, Types.NUMERIC);  
            }  
        }  
    } 
    
    
    /** 
     * 根据结果集初始化对象 
     */  
    private void initObject(T t, Field[] fields, ResultSet rs)  
            throws SQLException, IntrospectionException,  
            IllegalAccessException, InvocationTargetException {  
        for (Field field : fields) {  
            String propertyName = field.getName();  
            Object paramVal = null;  
            Class<?> clazzField = field.getType();  
            if (clazzField == String.class) {  
                paramVal = rs.getString(propertyName);  
            } else if (clazzField == short.class || clazzField == Short.class) {  
                paramVal = rs.getShort(propertyName);  
            } else if (clazzField == int.class || clazzField == Integer.class) {  
                paramVal = rs.getInt(propertyName);  
            } else if (clazzField == long.class || clazzField == Long.class) {  
                paramVal = rs.getLong(propertyName);  
            } else if (clazzField == float.class || clazzField == Float.class) {  
                paramVal = rs.getFloat(propertyName);  
            } else if (clazzField == double.class || clazzField == Double.class) {  
                paramVal = rs.getDouble(propertyName);  
            } else if (clazzField == boolean.class || clazzField == Boolean.class) {  
                paramVal = rs.getBoolean(propertyName);  
            } else if (clazzField == byte.class || clazzField == Byte.class) {  
                paramVal = rs.getByte(propertyName);  
            } else if (clazzField == char.class || clazzField == Character.class) {  
                paramVal = rs.getCharacterStream(propertyName);  
            } else if (clazzField == Date.class) {  
                paramVal = rs.getDate(propertyName);
                //getTimestamp(propertyName);  
            } else if (clazzField.isArray()) {  
                paramVal = rs.getString(propertyName).split(",");   //以逗号分隔的字符串  
            }   
            PropertyDescriptor pd = new PropertyDescriptor(propertyName,t.getClass());  
            pd.getWriteMethod().invoke(t, paramVal);  
        }  
    }  
    
    
    /** 
     * 根据条件，返回sql条件和条件中占位符的值 
     * @param sqlWhereMap key：字段名 value：字段值 
     * @return 第一个元素为SQL条件，第二个元素为SQL条件中占位符的值 
     */  
    private List<Object> getSqlWhereWithValues(Map<String,Object> sqlWhereMap) {  
        if (sqlWhereMap.size() <1 ) return null;  
        List<Object> list = new ArrayList<Object>();  
        List<Object> fieldValues = new ArrayList<Object>();  
        StringBuffer sqlWhere = new StringBuffer(" where ");  
        Set<Entry<String, Object>> entrySets = sqlWhereMap.entrySet();  
        for (Iterator<Entry<String, Object>> iteraotr = entrySets.iterator();iteraotr.hasNext();) {  
            Entry<String, Object> entrySet = iteraotr.next();  
            fieldValues.add(entrySet.getValue());  
            Object value = entrySet.getValue();  
            if (value.getClass() == String.class) {  
                sqlWhere.append(entrySet.getKey()).append(" like ").append("?").append(" and ");  
            } else {  
                sqlWhere.append(entrySet.getKey()).append("=").append("?").append(" and ");  
            }  
        }  
        sqlWhere.delete(sqlWhere.lastIndexOf("and"), sqlWhere.length());  
        list.add(sqlWhere.toString());  
        list.add(fieldValues);  
        return list;  
    }  

	
}
