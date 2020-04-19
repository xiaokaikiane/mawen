package app;
import util.Util;
import util.pinyinUtil;
import java.io.File;
import java.util.Date;
import java.util.Objects;
public class FileMeta {
    private String name;//文件名
    private  String path;//文件所在父路径
    private  Long size;//文件大小
    private Date lastmodified;//文件上一次修改时间
    private String sizeText;//客户端控件使用,和fxml一致
    private String lastmodifiedText;//同
    private String pinyin;//拼音
    private String pinyinFirst;//拼首
    private boolean isDirectory;//是否是文件夹
    //通过文件设置属性
    public FileMeta(File file){
        this(file.getName(),file.getParent(),file.length(),file.isDirectory(),
                new Date(file.lastModified()));
    }
    //通过数据库获取的数据来设置FileMeta
    public FileMeta(String name,String path,long size,
                    Boolean isDirectory,Date lastmodified){
        this.name=name;
        this.path=path;
        this.size=size;
        this.lastmodified=lastmodified;
        if(pinyinUtil.containsChinese(name)){
            String[] pinyins=pinyinUtil.get(name);
            pinyin=pinyins[0];
            pinyinFirst=pinyins[1];
        }
        //客户端表格控件文件大小,文件上次修改时间的设置
        sizeText= Util.parseSize(size);
        lastmodifiedText=Util.parseDate(lastmodified);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileMeta fileMeta = (FileMeta) o;
        return isDirectory == fileMeta.isDirectory &&
                Objects.equals(name, fileMeta.name) &&
                Objects.equals(path, fileMeta.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path, isDirectory);
    }

    @Override
    public String toString() {
        return "FileMeta{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", isDirectory=" + isDirectory +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Date getLastmodified() {
        return lastmodified;
    }

    public void setLastmodified(Date lastmodified) {
        this.lastmodified = lastmodified;
    }

    public String getSizeText() {
        return sizeText;
    }

    public void setSizeText(String sizeText) {
        this.sizeText = sizeText;
    }

    public String getLastmodifiedText() {
        return lastmodifiedText;
    }

    public void setLastmodifiedText(String lastmodifiedText) {
        this.lastmodifiedText = lastmodifiedText;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getPinyinFirst() {
        return pinyinFirst;
    }

    public void setPinyinFirst(String pinyinFirst) {
        this.pinyinFirst = pinyinFirst;
    }
}
