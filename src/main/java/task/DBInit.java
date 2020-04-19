package task;

import util.DButil;

import java.io.*;
import java.sql.Connection;
import java.sql.Statement;


/**
 * 初始化数据库
 * 调用DButil.getConnection()来完成数据库初始化
 * 读取sql文件
 * 执行sql文件来初始化表
 */
public class DBInit {

    public static String[] readSQL(){
        try {
            //通过classloader获取流
            InputStream is=DBInit.class.getClassLoader()
                    .getResourceAsStream("init.sql");
            //字节流转换为字符流
            BufferedReader br=new BufferedReader(
                    new InputStreamReader(is,"utf-8"));
            StringBuilder sb=new StringBuilder();
            String line;
            while((line=br.readLine())!=null){
                if(line.contains("--")){ //去掉--的注释代码
                    line=line.substring(0,line.indexOf("--"));
                }
                sb.append(line);
            }
            String[] sqls=sb.toString().split(";");
            return sqls;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("读取sql文件错误", e);
        }
    }
    public static void init(){
     //数据库jdbc操作,sql语句的执行
        Connection connection=null;
        Statement statement=null;
        try {
            //1.建立数据库连接
            connection= DButil.getConnection();
            //2.创建statement对象
            statement=connection.createStatement();
            String[] sqls=readSQL();
            for(String sql:sqls){
                //3.执行sql语句
                statement.executeUpdate(sql);
            }
            //4.处理结果集

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("数据库表初始化操作失败",e);
        }finally {
            //5.释放资源
            DButil.close(connection,statement,null);
        }
    }

    public static void main(String[] args) {
        String[] sqls=readSQL();
        for(String sql:sqls){
            System.out.println(sql);
        }
       // System.out.println(Arrays.toString(readSQL()));
        init();
    }
}
