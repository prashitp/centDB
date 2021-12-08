package com.example.models.erd;
import com.example.models.Metadata;
import com.example.models.enums.Entity;
import com.example.services.metadata.MetadataService;
import com.example.services.metadata.MetadataServiceImpl;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Erd {
    public void generate(String databaseName) throws IOException {
    File file = new File("storage/erd/"+databaseName+"_ERD.txt");
        file.createNewFile();
        FileWriter fileWriter = new FileWriter(file, true);;

        MetadataService metadataService = new MetadataServiceImpl();
        Metadata metadata = metadataService.read(Entity.DATABASE, databaseName);
        metadata.getAllTablesFromDatabase()
                .forEach(table -> {
                    if (table.getForeignKeys() != null) {
                        System.out.println(table.getForeignKeys().size());
                    }
                    else {
                        System.out.println("hello");
                    }
                    try {
                        fileWriter.write(String.format("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"));
                        fileWriter.write(String.format("\n%80s%n", "Table Name:"+table.getName()));
                        fileWriter.write(String.format("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"));
                        fileWriter.write(String.format("\n%-25s |", "Column Names"));
                        table.getColumns().forEach(col ->
                                {
                                    try {
                                        fileWriter.write(String.format("%20s |", col.getName()));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                        );
                        fileWriter.write(String.format("\n-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"));
                        fileWriter.write(String.format("\n%-25s |", "Column Type"));
                        table.getColumns().forEach(col ->
                                {
                                    try {
                                        fileWriter.write(String.format("%20s |", col.getDataType()));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                        );
                        fileWriter.write(String.format("\n-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"));
                        fileWriter.write(String.format("\n%-25s |", "Primary Key"));
                        table.getColumns().forEach(col -> {
                                    try {
                                        if (table.getPrimaryKey().getName().equals(col.getName())) {
                                            fileWriter.write(String.format("%20s |", "PK"));
                                        }
                                        else {
                                            fileWriter.write(String.format("%20s |", ""));
                                        }
                                    }
                                    catch (Exception e) {
                                        try {
                                            fileWriter.write(String.format("%20s |", ""));
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                }
                        );
                        if (table.getForeignKeys() != null) {
                            System.out.println(table.getForeignKeys().size());
                            fileWriter.write(String.format("\n-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"));
                            fileWriter.write(String.format("\n%-25s |", "Foreign Key"));
                            table.getColumns().forEach(col -> {
                                        try {
                                            table.getForeignKeys().forEach(fKey -> {
                                                if (col.getName().equals(fKey.getForeignKeyColumn())) {
                                                    try {
                                                        fileWriter.write(String.format("%20s |", "FK"));
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                else {
                                                    try {
                                                        fileWriter.write(String.format("%20s |", ""));
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }});

                                        } catch (Exception e) {
                                            try {
                                                fileWriter.write(String.format("%20s |", ""));
                                            } catch (IOException ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                    }
                            );
                            fileWriter.write(String.format("\n-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"));
                            fileWriter.write(String.format("\n%-25s |", "Reference Table Name"));
                            table.getColumns().forEach(col -> {
                                        try {
                                            table.getForeignKeys().forEach(fKey -> {
                                                if (col.getName().equals(fKey.getForeignKeyColumn())) {
                                                    try {
                                                        fileWriter.write(String.format("%20s |",fKey.getReferenceTableName()));
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                else {
                                                    try {
                                                        fileWriter.write(String.format("%20s |", ""));
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }});

                                        } catch (Exception e) {
                                            try {
                                                fileWriter.write(String.format("%20s |", ""));
                                            } catch (IOException ex) {
                                                ex.printStackTrace();
                                            }

                                        }
                                    }
                            );
                            fileWriter.write(String.format("\n-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"));
                            fileWriter.write(String.format("\n%-25s |", "Reference Column Name"));
                            table.getColumns().forEach(col -> {
                                        try {
                                            table.getForeignKeys().forEach(fKey -> {
                                                if (col.getName().equals(fKey.getForeignKeyColumn())) {
                                                    try {
                                                        fileWriter.write(String.format("%20s |", fKey.getReferenceColumnName()));
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                else {
                                                    try {
                                                        fileWriter.write(String.format("%20s |", ""));
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }});

                                        } catch (Exception e) {
                                            try {
                                                fileWriter.write(String.format("%20s |", ""));
                                            } catch (IOException ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                    }
                            );
                        }
                        fileWriter.write(String.format("\n-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"));
                        fileWriter.write(String.format("\n\n\n"));
                    }
                    catch (Exception e) {
                        try {
                            fileWriter.write(String.format("%20s |", ""));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                });
        fileWriter.close();
        System.out.println("ERD is created for database "+databaseName+" with the name of "+databaseName+"_ERD.txt");
    }
}

