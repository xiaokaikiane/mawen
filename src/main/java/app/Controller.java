package app;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import task.*;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private GridPane rootPane;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<FileMeta> fileTable;

    @FXML
    private Label srcDirectory;
    private Thread task;

    public void initialize(URL location, ResourceBundle resources) {
        //界面初始化时,需要初始化数据库表
        DBInit.init();
        // 添加搜索框监听器，内容改变时执行监听事件
        searchField.textProperty().addListener(new ChangeListener<String>() {

            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                freshTable();
            }
        });
    }

    /**
     * 点击"选择目录"按钮,发生的事件方法
     *
     * @param event
     */
    public void choose(Event event) {
        // 选择文件目录
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Window window = rootPane.getScene().getWindow();
        File file = directoryChooser.showDialog(window);
        if (file == null)
            return;
        // 获取选择的目录路径，并显示
        String path = file.getPath();
        srcDirectory.setText(path);
        //选择了目录,执行目录扫描任务
        if (task != null) {
            task.interrupt();
        }
        task=new Thread(new Runnable() {
            @Override
            public void run() {
                //文件夹扫描回调接口,做文件夹下一级子文件和子文件夹保存数据库的操作
                ScanCallback callback = new FileSave();
                FileScanner scanner = new FileScanner(callback);//传入扫描任务类
                    try {
                        System.out.println("执行文件扫描任务");
                        scanner.Scan(path);//为了提高效率,多线程执行扫描任务
                        //等待文件扫描任务完成
                        scanner.waitFinish();
                        System.out.println("所有任务执行完毕,刷新表格");
                        //刷新表格:将扫描出来的子文件,子文件夹展示出来
                        freshTable();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

            }
        });
        task.start();
    }

    // 刷新表格数据
    private void freshTable() {
        ObservableList<FileMeta> metas = fileTable.getItems();
        metas.clear();
       //如果选择了某个目录,代表需要根据搜索框的内容,来进行数据库文件夹的查询
        String dir=srcDirectory.getText();
        if(dir!=null&&dir.trim().length()!=0){
            String content=searchField.getText();
            //提供数据库的查询方法
            List<FileMeta> fileMetas= FileSearch.search(dir,content);
            metas.addAll(fileMetas);
        }
        //方法返回后,javafx通过获取filemeta类型中的属性(app.fxml文件中定义的属性)
    }
}