package com.zy.simpleORM;

import java.sql.Date;

import com.zy.annotation.Column;
import com.zy.annotation.Entity;
import com.zy.annotation.Id;

/** 
 * 图书 
 */  
@Entity("t_book")   //表名  
public class Book {  
  
    /** 
     * 图书编号 
     */  
    @Id("t_isbn")  
    private String isbn;  
  
    /** 
     * 书名 
     */  
    @Column("t_name")  
    private String name;  
  
    /** 
     * 作者 
     */  
    @Column("t_author")  
    private String author;  
  
    /** 
     * 出版社 
     */  
    @Column("t_publishing")  
    private String publishing;  
  
    /** 
     * 出版时间 
     */  
    @Column(value = "t_pubdate")  
    private Date pubdate;  
  
    /** 
     * 价格 
     */  
    @Column(value = "t_price")  
    private double price;  
  
    public String getIsbn() {  
        return isbn;  
    }  
  
    public void setIsbn(String isbn) {  
        this.isbn = isbn;  
    }  
  
    public String getName() {  
        return name;  
    }  
  
    public void setName(String name) {  
        this.name = name;  
    }  
  
    public String getAuthor() {  
        return author;  
    }  
  
    public void setAuthor(String author) {  
        this.author = author;  
    }  
  
    public String getPublishing() {  
        return publishing;  
    }  
  
    public void setPublishing(String publishing) {  
        this.publishing = publishing;  
    }  
  
    public Date getPubdate() {  
        return pubdate;  
    }  
  
    public void setPubdate(Date pubdate) {  
        this.pubdate = pubdate;  
    }  
  
    public double getPrice() {  
        return price;  
    }  
  
    public void setPrice(double price) {  
        this.price = price;  
    }  
  
    @Override  
    public String toString() {  
        return "书名: " + name + " 图书编号: " + isbn + " 作者: " + author  
                + " 出版社: " + publishing + " 出版时间: " + pubdate  
                + " 价格: " + price;  
    }  
}  
