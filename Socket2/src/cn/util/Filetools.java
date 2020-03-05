package cn.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Filetools {
   private String Username;
   
   public Filetools(String Username) {
	   this.Username = Username;
   }
	
   //判断某个文件夹是否存在
   public boolean isdirectory(String path) {
	   File file = new File(path);
	   return file.isDirectory();
   }
   
   //创建文件夹
   public void create_directory(String path) {
	   File file = new File(path);
	   file.mkdirs();
   }
   
   //创建文件
   public void create_file(String path) {
	   File file = new File(path);
		try {
	   if (file.createNewFile()){
			System.out.println("File is created!");
			}else{
			System.out.println("File already exists.");
			}
	}catch(IOException e){
		e.printStackTrace();
	}
   }
   
   //把聊天信息写入文本
   public void file_writer(String path,String text) {
	   File file = new File(path);
	   try {
		 //true或false代表不同的文本信息追加方式
		   FileWriter out =new FileWriter(file,true);
		   out.write(text+"\n");
		   out.close();
	   }catch(IOException e) {
		   e.printStackTrace();
	   }
   }
   
   //读取指定行数的文本信息
   public String file_reader_targetLine(String path,int number) {
	   File file = new File(path);
	   String Msg = null;
	   try {
	    InputStreamReader fr = new InputStreamReader(new FileInputStream(file), "UTF-8");
		BufferedReader in = new BufferedReader(fr);
		int line = 0;
        String s;
		while((s=in.readLine())!=null) {
			line++;
			if((line-number)==0){
				Msg = s;
				break;
			}
		}
		in.close();
		fr.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	   return Msg;
   }

   //读取文本所有信息
	public String[] file_reader(String path) {
		File file = new File(path);
		String Msg[]=new String[1000];//����һ����������������ͺͳ���
		try {
			InputStreamReader fr = new InputStreamReader(new FileInputStream(file), "UTF-8");
			BufferedReader in = new BufferedReader(fr);
			String s = null;
			int i = 0;
			while((s=in.readLine())!=null) {
				i++;
				Msg[i]=s;
			}
			Msg[0]=Integer.toString(i);
			in.close();
			fr.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		return Msg;
	}


   //获取当前文本文件行数
   public int file_lines(String path){
   	int lines = 0;
   	File file = new File(path);
   	try {
   		InputStreamReader fr = new InputStreamReader(new FileInputStream(file),"UTF-8");
   		BufferedReader in = new BufferedReader(fr);
   		int line = 0;
   		while ((in.readLine())!=null){
   			line++;
		}
		lines=line;
		in.close();
		fr.close();
	}catch (IOException e){
   		e.printStackTrace();
	}
   	return lines;
   }

   public String get_targetTimeContent(String date){
   	String time,year,month,day;
   	year = date.substring(2,4);
   	month = date.substring(5,7);
   	day = date.substring(8,10);
    time = year+"/"+month+"/"+day;
   	return time;
   }
}
