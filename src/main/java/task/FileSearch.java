package task;

import app.FileMeta;
import util.DButil;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FileSearch {
    public static List<FileMeta> search(String dir, String content){
        List<FileMeta> metas=new ArrayList<>();
        Connection connection=null;
        PreparedStatement statement=null;
        ResultSet resultSet=null;
        try {
            connection= DButil.getConnection();
            String sql="select name,path,is_directory,size,last_modified"+
                    " from file_meta where"+
                    " (path=? or path like ?)";
            if(content!=null&&content.trim().length()!=0){
                sql +=" and (name like ? or pinyin like ? or pinyin_first like ?)";
            }
            statement=connection.prepareStatement(sql);
            statement.setString(1,dir);
            statement.setString(2,dir+ File.separator+"%");
            if (content!=null&&content.trim().length()!=0){
                statement.setString(3,"%"+content+"%");
                statement.setString(4,"%"+content+"%");
                statement.setString(5,"%"+content+"%");
            }
            resultSet=statement.executeQuery();
            while(resultSet.next()){
                String name=resultSet.getString("name");
                String path=resultSet.getString("path");
                Boolean isdirectory=resultSet.getBoolean("is_directory");
                Long size=resultSet.getLong("size");
                Timestamp lastmodified=resultSet.getTimestamp("last_modified");
                FileMeta meta=new FileMeta(name,path,size,isdirectory,
                        new java.util.Date(lastmodified.getTime()));
                System.out.printf("文件信息:name=%s,path=%s\n",name,path);
                metas.add(meta);
            }
        } catch (Exception e) {
            throw new RuntimeException("数据库文件查询失败,路径:"+dir+"搜索内容"+content,e);
        } finally {
            DButil.close(connection,statement,resultSet);
        }
        return metas;
    }
}
