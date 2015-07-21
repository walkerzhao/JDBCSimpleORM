package com.zy.simpleORM;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcDaoHelper {
	
	private static final String USER = "root"; //用户名
	private static final String PASSWORD = ""; //密码
	private static final String URL = "jdbc:mysql://localhost:3306/test";
	private static Connection con;
	
	private static Properties properties = null;
	
	static {
		properties = new Properties();	
		InputStream in = (InputStream) JdbcDaoHelper.class.getResourceAsStream("mysqlDB.properties");  
		
	    try {  
	        properties.load(in);  
	    } catch (IOException ex) {    
	        System.out.println(ex.getMessage());  
	        ex.printStackTrace();  
	    }
	}

	
	//获取数据库的连接,加载驱动，建立到数据库的连接
	public static Connection getConnection() {
		//将配置放在配置文件中		
		
		try {
			if(con == null) {
				Class.forName(properties.getProperty("DBDriver"));
				try {
					con = DriverManager.getConnection(properties.getProperty("URL"), properties.getProperty("USERNAME"), properties.getProperty("PASSWORD"));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;		
	}
		
	//释放数据库资源
	public static void release(PreparedStatement ps, ResultSet rs) {
		if(con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			con = null;
		}
		
		if( ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ps = null;
		}
		
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rs = null;
		}
	}

}
