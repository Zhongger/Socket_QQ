package cn.util;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class JDBCUtils {
    private static String driver="";
    private static String url="";
    private static String user="";
    private static String password="";
    /*
    文件的读取，只需要读取一次即可拿到这些值，使用静态代码块
     */
    static {

        try {
            //读取资源文件，获取值
            //1.Properties集合类
            Properties properties=new Properties();
            //获取src路径下的文件的方式--->ClassLoader类加载器
            /*ClassLoader classLoader=JDBCUtils.class.getClassLoader();
            URL res=classLoader.getResource("jdbc.properties");
            String path=res.getPath();
            properties.load(new FileReader(path));中文乱码*/
            ClassLoader classLoader=JDBCUtils.class.getClassLoader();
            String path=classLoader.getResource("jdbc.properties").getFile();
            path=java.net.URLDecoder.decode(path,"utf-8");
            properties.load(new FileReader(path));
            //2.加载文件
            //properties.load(new FileReader("E:\\Java项目\\JDBC_Test\\demo1\\src\\jdbc.properties"));

            //3.获取数据，赋值
            url=properties.getProperty("url");
            user=properties.getProperty("user");
            password=properties.getProperty("password");
            driver=properties.getProperty("driver");
            //4.注册驱动
            Class.forName(driver);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static Connection getconnection() throws SQLException {
        return DriverManager.getConnection(url,user,password);
    }
    public static void close(Statement statement,Connection connection){
        if (statement!=null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection!=null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public static void close(ResultSet resultSet,Statement statement,Connection connection){
        if (resultSet!=null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement!=null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection!=null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Reservename {
     private File Imagefile;

     public Reservename(File Imagefile) {
         this.Imagefile=Imagefile;
     }

     public String getname() {
         String[] information=Imagefile.getName().split(".png");
         return information[0];
     }
    }
}
