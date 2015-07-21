package com.zy.simpleORM;

import junit.framework.TestCase;


import java.io.InputStream;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;



/**
 * 测试泛型DAO的CRUD操作
 */
public class GenericDaoTest  {
	
	private GenericDAO<Book> bookDao = new GenericDAOImpl<Book>();	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		is = XmlParserTest.class.getResourceAsStream("/books.xml");
	}
	    
	@SuppressWarnings("deprecation")
	@Test
	public void testSave() throws Exception {
		//构造一本书，插入到数据库中
		Book book = new Book();	
		book.setIsbn("1234");
		book.setAuthor("zy");
		book.setName("pingfan");
		book.setPrice(55);
		book.setPubdate(new Date(105, 11, 12));  //这里的年是距离1900年的
		book.setPublishing("renmingchubanshe");
		bookDao.save(book);
	}		
	
	@Test
	public void testDelete() throws Exception {
		bookDao.delete(1, Book.class);
	}
	
	//更新数据测试，应该先search到数据，然后在数据上面设置值，然后update到数据库
	@SuppressWarnings("deprecation")
	@Test
	public void testUpdate() throws Exception {
		Book book = new Book();
		book.setIsbn("1234");  
        book.setName("test");  
        book.setAuthor("test");  
        book.setPublishing("test");     
        book.setPubdate(new Date(110,10,11));  
        book.setPrice(50.6);
		bookDao.update(book);
		
	}
	

	@Test
	public void testGet() throws Exception {
		bookDao.get("1234",Book.class);		
	}
	
	@Test
	public void testFindAllByConditions() throws Exception {
		System.out.println("\n-------------更新、删除前,测试根据条件查询所有记录--------------------");  
        Map<String,Object> sqlWhereMap = new HashMap<String, Object>();  
        //sqlWhereMap.put("t_isbn", "9787111213826");  
        //sqlWhereMap.put("t_name", "Java");  
        sqlWhereMap.put("t_publishing", "test");  
        //sqlWhereMap.put("t_pubdate", new Date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2007-01-01 12:06:00").getTime()));  
        List<Book> books = bookDao.findAllByConditions(sqlWhereMap, Book.class);  
        for (Book book : books) {  
            System.out.println(book);  
        } 
	}

}