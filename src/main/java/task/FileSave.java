package task;

import app.FileMeta;
import util.DButil;
import util.Util;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileSave implements ScanCallback {
    public void callback(File dir) {
        //文件夹下一级子文件和子文件夹保存的数据库
        //获取本地目录下一级文件夹和子文件夹
        //集合框架中使用自定义类型,判断是否某个对象在集合存在:对比两个集合中的元素
        File[] child = dir.listFiles();
        List<FileMeta> locals = new ArrayList<>();
        if (child != null) {
            for (File ch : child) {
                locals.add(new FileMeta(ch));
            }
        }
        //获取数据库保存的dir 目录的下一级子文件和子文件夹 (select)
        List<FileMeta> metas=query(dir);
        // 数据库有,本地没有,作删除(delete)
        for(FileMeta meta:metas){
            if(!locals.contains(meta)){
                //删除本身
                //如果是文件夹,子文件夹和文件也要删除
                delete(meta);
            }
        }
        //本地有,数据库没有,做插入(insert)
        for(FileMeta meta:locals){
            if(!metas.contains(meta)){
                save(meta);
            }
        }
    }

    private void delete(FileMeta meta) {
        Connection connection=null;
        PreparedStatement statement=null;
        try {
            connection=DButil.getConnection();
            String sql="delete from file_meta where"+
                    " (name=? and path=? and is_directory=?)";//删除文件本身
            if(meta.isDirectory()){//如果是文件夹
                sql +=" or path=?"+//匹配数据库文件夹的儿子
                        " or path like ?";//匹配文件夹的孙子
            }
            statement=connection.prepareStatement(sql);
            statement.setString(1,meta.getName());
            statement.setString(2,meta.getPath());
            statement.setBoolean(3,meta.isDirectory());
            if(meta.isDirectory()){
                statement.setString(4,meta.getPath()+File.separator+meta.getName());
                statement.setString(5,meta.getPath()+
                        File.separator+meta.getName()+File.separator);
            }
            System.out.printf("删除文件是%s\n",meta.getPath()+File.separator+meta.getName());
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("删除出现错误",e);
        } finally {
            DButil.close(connection,statement,null);
        }
    }

    private List<FileMeta> query(File dir) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<FileMeta> metas = new ArrayList<>();
        try {
            //1.创建数据库连接
            connection = DButil.getConnection();
            String sql = "select name,path,is_directory,size,last_modified" +
                    " from file_meta where path=?";
            //2.创建操作对象
            statement = connection.prepareStatement(sql);
            statement.setString(1, dir.getPath());
            //3.执行sql语句
            resultSet = statement.executeQuery();
            //4.处理结果集
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String path = resultSet.getString("path");
                Boolean isdirectory = resultSet.getBoolean("is_directory");
                Long size = resultSet.getLong("size");
                Timestamp lastModified = resultSet.getTimestamp("last_modified");
                FileMeta meta = new FileMeta(name, path, size,
                        isdirectory, new java.util.Date(lastModified.getTime()));
                System.out.printf("查询文件信息:name=%s,path=%s,is_directory=%s," +
                                "size=%s,last_modified=%s\n", name, path, String.valueOf(isdirectory),
                        String.valueOf(size), Util.parseDate(new java.util.Date(lastModified.getTime())));
                metas.add(meta);
            }
            return metas;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("查询文件信息错误", e);
        } finally {
            DButil.close(connection, statement, resultSet);
        }
    }

    /**
     * @param meta
     */
    private void save(FileMeta meta) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            //1.获取数据库连接
            connection = DButil.getConnection();
            String sql = "insert into file_meta" +
                    "(name,path,is_directory,size,last_modified,pinyin,pinyin_first)" +
                    "values(?,?,?,?,?,?,?)";
            //2.获取sql操作命令对象
            statement = connection.prepareStatement(sql);
            statement.setString(1, meta.getName());
            statement.setString(2, meta.getPath());
            statement.setBoolean(3, meta.isDirectory());
            statement.setLong(4, meta.getSize());
            //数据库日期类型,可以接受数据库设置的日期格式,以字符串传入
            statement.setString(5, meta.getLastmodifiedText());
            statement.setString(6,meta.getPinyin());
            statement.setString(7,meta.getPinyinFirst());
            System.out.printf("insert name=%s,path=%s\n",meta.getName(),meta.getPath());
            //3.执行sql
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("文件保存失败,检查sql语句", e);
        } finally {
            //释放资源
            DButil.close(connection, statement, null);
        }
    }

    public static void main(String[] args) {
//        DBInit.init();
//        File file=new File("F:\\作业");
//        FileSave fileSave=new FileSave();
//        fileSave.save(file);
//        fileSave.query(file.getParentFile());
//        List<FileMeta> locals=new ArrayList<>();
//        locals.add(new FileMeta("新建文件夹","D:\\tmp",
//                0,true,new Date()));
//        locals.add(new FileMeta("中华人民共和国2","D:\\tmp",
//                0,true,new Date()));
//        locals.add(new FileMeta("音乐.txt","D:\\tmp\\中华人民共和国2",
//                0,true,new Date()));
//        List<FileMeta> metas=new ArrayList<>();
//        metas.add(new FileMeta("新建文件夹","D:\\tmp",
//                0,true,new Date()));
//        metas.add(new FileMeta("中华人民共和国2 - 副本","D:\\tmp",
//                0,true,new Date()));
//        metas.add(new FileMeta("音乐.txt","D:\\tmp\\中华人民共和国2 - 副本",
//                0,true,new Date()));
//        for(FileMeta meta:locals){
//          if(!metas.contains(meta)){
//              System.out.println(meta);
//          }
//        }
    }
}

