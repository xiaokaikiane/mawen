package util;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DButil {
    private static volatile DataSource DATA_SOURCE;

    /**
     * 提供获取数据库连接池的功能
     * 使用单例模式(多线程版本)
     * @return
     */
    private static DataSource getDataSource(){
        if (DATA_SOURCE==null){
            //刚开始
            synchronized (DButil.class){
                if(DATA_SOURCE==null){
                    //初始化操作,使用volatile关键字禁止指令重排序,建立内存屏障
                    SQLiteConfig config=new SQLiteConfig();
                    config.setDateStringFormat(Util.DATE_PATTERN);
                    DATA_SOURCE=new SQLiteDataSource(config);
                    ((SQLiteDataSource)DATA_SOURCE).setUrl(geturl());
                }
            }
        }
        return DATA_SOURCE;
    }
    private static  String geturl(){
        try {
            //获取target编译文件夹的路径
            //通过classloader.getResource()/classloader.getResourceAsStream()这样的方法
            //默认的根路径为编译文件(target/classes)
            URL classesURL=DButil.class.getClassLoader().getResource("./");
            String dir=new File(classesURL.getPath()).getParent();//获取target/classes文件夹的父目录路径
            String url="jdbc:sqlite://"+dir+File.separator+"maven.db";
            //newSqliteDateSource() 把这个对象的url设置进去,才会创建这个文件
            //如果文件已经存在,就会读取这个文件
            url= URLDecoder.decode(url,"utf-8");
            System.out.println("获取数据库文件路径:"+url);
            return url;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException("获取数据库文件失败",e);
        }
    }
    /**
     *提供获取数据库连接池的方法
     * 从数据库连接池DataSource.getConnection()来获取数据库连接
     * @return
     */
    public static Connection getConnection()throws SQLException {
        return getDataSource().getConnection();
    }

    public static void main(String[] args)throws SQLException {
        System.out.println(getConnection());
    }

    /**
     * 释放数据库资源
     * @param connection
     * @param statement
     */
    public static void close(Connection connection, Statement statement,ResultSet resultSet) {
        try {
            if (connection!=null){
                connection.close();
            }
            if(statement!=null){
                statement.close();
            }
            if(resultSet!=null){
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("释放数据库资源错误",e);
        }
    }

}
