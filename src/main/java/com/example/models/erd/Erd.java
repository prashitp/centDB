package com.example.models.erd;
import com.example.models.Column;
import com.example.models.ForeignKey;
import com.example.models.Metadata;
import com.example.models.Table;
import com.example.models.enums.Entity;
import com.example.services.metadata.MetadataService;
import com.example.services.metadata.MetadataServiceImpl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Erd {
    public void linePrinter( FileWriter fileWriter) throws IOException {
        fileWriter.write(String.format("\n-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"));
    }
    public void valuePrinter( FileWriter fileWriter, String value) throws IOException {
        fileWriter.write(String.format("%20s |", value));

    }
    public void generate(String databaseName) {
            try {
            File file = new File("storage/erd/" + databaseName + "_ERD.txt");
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file, true);
            ;

            MetadataService metadataService = new MetadataServiceImpl();
            Metadata metadata = metadataService.read(Entity.DATABASE, databaseName);
                for (Table table : metadata.getAllTablesFromDatabase()) {
                    try {
                        linePrinter(fileWriter);
                        fileWriter.write(String.format("\n%80s", "Table Name:" + table.getName()));
                        linePrinter(fileWriter);

                        fileWriter.write(String.format("\n%-25s |", "Column Names"));
                        for (Column column : table.getColumns()) {
                            valuePrinter(fileWriter, column.getName());
                        }
                        linePrinter(fileWriter);

                        fileWriter.write(String.format("\n%-25s |", "Column Type"));
                        for (Column column : table.getColumns()) {
                            valuePrinter(fileWriter, column.getDataType());
                        }
                        linePrinter(fileWriter);

                        fileWriter.write(String.format("\n%-25s |", "Primary Key"));
                        for (Column column : table.getColumns()) {
                            try {
                                if (table.getPrimaryKey().getName().equals(column.getName())) {
                                    valuePrinter(fileWriter, "PK");
                                } else {
                                    valuePrinter(fileWriter, "");
                                }
                            } catch (Exception e) {
                                valuePrinter(fileWriter, "");
                            }
                        }

                        if (table.getForeignKeys() != null) {
                            System.out.println(table.getForeignKeys().size());
                            linePrinter(fileWriter);

                            fileWriter.write(String.format("\n%-25s |", "Foreign Key"));
                            for (Column column : table.getColumns()) {
                                try {
                                    for (ForeignKey fKey : table.getForeignKeys()) {
                                        if (column.getName().equals(fKey.getForeignKeyColumn())) {
                                            valuePrinter(fileWriter, "FK");
                                        } else {
                                            valuePrinter(fileWriter, "");
                                        }
                                    }

                                } catch (Exception e) {
                                    valuePrinter(fileWriter, "");
                                }
                            }
                            linePrinter(fileWriter);

                            fileWriter.write(String.format("\n%-25s |", "Reference Table Name"));
                            for (Column column : table.getColumns()) {
                                try {
                                    for (ForeignKey fKey : table.getForeignKeys()) {
                                        if (column.getName().equals(fKey.getForeignKeyColumn())) {
                                            valuePrinter(fileWriter, fKey.getReferenceTableName());

                                        } else {
                                            valuePrinter(fileWriter, "");
                                        }
                                    }

                                } catch (Exception e) {
                                    valuePrinter(fileWriter, "");

                                }
                            }
                            linePrinter(fileWriter);

                            fileWriter.write(String.format("\n%-25s |", "Reference Column Name"));
                            for (Column col : table.getColumns()) {
                                try {
                                    for (ForeignKey fKey : table.getForeignKeys()) {
                                        if (col.getName().equals(fKey.getForeignKeyColumn())) {
                                            valuePrinter(fileWriter, fKey.getReferenceColumnName());
                                        } else {
                                            valuePrinter(fileWriter, "");

                                        }
                                    }

                                } catch (Exception e) {
                                    valuePrinter(fileWriter, "");
                                }
                            }
                        }
                        linePrinter(fileWriter);
                        fileWriter.write(String.format("\n\n"));
                    } catch (Exception e) {
                        valuePrinter(fileWriter, "");
                    }

                }
                fileWriter.close();
            System.out.println("ERD is created for database " + databaseName + " with the name of " + databaseName + "_ERD.txt");
        } catch (IOException e) {
                e.printStackTrace();
        }
    }
}

