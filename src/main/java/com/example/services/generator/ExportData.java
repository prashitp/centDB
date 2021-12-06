package com.example.services.generator;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class ExportData {
    public static final String DB_DIRECTORY = "storage/METADATA/";
    public static final String TB_DIRECTORY = "storage/";

    public static void main(String[] args) {
        createSqlDump("CENT_DB2");
    }

    public static boolean createSqlDump(String DB_NAME) {
        File[] dbFiles = new File(DB_DIRECTORY).listFiles();
        boolean dbFoundFlag = false;
        for (int i=0; i < dbFiles.length; i++){
            String fileName = dbFiles[i].toString();
            if ((DB_NAME+".txt").equals(fileName.split("_")[1]+"_"+fileName.split("_")[2])){
                dbFoundFlag = true;
                BufferedReader br = null;
                try {
                    String dbFilePath = DB_DIRECTORY + "MD_" + DB_NAME + ".txt";
                    //func to generate create db query
                    writeDbInFile(DB_NAME);
                    br = new BufferedReader(new FileReader(dbFilePath));
                    //func to generate create table query
                    parseTableQuery(br, DB_NAME);
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return dbFoundFlag;
    }

    public static void writeDbInFile(String DB_NAME){
        String DB_FORMAT = "`"+DB_NAME+"`";
        try {
            PrintWriter writer = new PrintWriter(DB_NAME+".sql", "UTF-8");
            writer.write("CREATE DATABASE IF NOT EXISTS "+DB_FORMAT+"\n");
            writer.write("USE "+DB_FORMAT+";\n");
            writer.close();
        }catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void parseTableQuery(BufferedReader br, String DB_NAME){
        String line;
        try{
            File file = new File(DB_NAME+".sql");
            FileWriter fr = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fr);
            Set<String> tableNames = new HashSet<String>();
            while ((line = br.readLine()) != null){
                switch (line.split("\\|")[0]){
                    case "TB":
                        String tableName = line.split("\\|")[1];
                        bufferedWriter.write("\n");
                        bufferedWriter.write("\n");
                        bufferedWriter.write("--\n");
                        bufferedWriter.write("-- Table structure for table "+ "`" +tableName+"`"+"\n");
                        bufferedWriter.write("--\n");
                        bufferedWriter.write("\n");
                        bufferedWriter.write("\n");
                        bufferedWriter.write("DROP TABLE IF EXISTS "+"`"+tableName+"`"+"\n");
                        bufferedWriter.write("CREATE TABLE "+"`"+tableName+"`"+" ("+"\n");
                        tableNames.add(tableName);
                        break;
                    case "CL":
                        bufferedWriter.write("`"+line.split("\\|")[1]+"` "+line.split("\\|")[2]+",\n");
                        break;
                    case "PK":
                        bufferedWriter.write("PRIMARY KEY "+"(`"+line.split("\\|")[1]+"`)\n");
                        bufferedWriter.write(");\n");
                        break;
                }
            }
            bufferedWriter.close();
            for(String table: tableNames)
                insertTableData(DB_NAME, table);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void insertTableData(String DB_NAME, String table){
        String tableDataLocation = TB_DIRECTORY+DB_NAME+"/"+"TB_"+table+".txt";
        BufferedReader bufferReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            String line;
            bufferReader = new BufferedReader(new FileReader(tableDataLocation));
            File file = new File(DB_NAME+".sql");
            FileWriter fr = new FileWriter(file, true);
            bufferedWriter = new BufferedWriter(fr);
            bufferedWriter.write("\n");
            bufferedWriter.write("\n");
            bufferedWriter.write("--\n");
            bufferedWriter.write("-- Dumping data for table "+"`"+table+"`\n");
            bufferedWriter.write("--\n");
            bufferedWriter.write("\n");
            bufferedWriter.write("LOCK TABLES "+"`"+table+"`"+" WRITE;\n");
            bufferedWriter.write("INSERT INTO "+"`"+table+"`"+" VALUES ");
            while ((line = bufferReader.readLine()) != null) {
                String[] splited = line.split("\\|");
                bufferedWriter.write("(");
                for (int i = 1; i < splited.length; i++) {
                    if (i != splited.length-1){
                        bufferedWriter.write("'"+splited[i]+"'"+",");
                    }else {
                        bufferedWriter.write("'"+splited[i]+"'");
                    }
                }
                if (bufferReader.read() != -1){
                    bufferedWriter.write("),");
                }else {
                    bufferedWriter.write(");");
                }
            }
            bufferedWriter.write("\n");
            bufferedWriter.write("UNLOCK TABLES;");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            bufferedWriter.close();
            bufferReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
