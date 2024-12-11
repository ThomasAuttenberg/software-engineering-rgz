package se.rgz.models.Table;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class TableManager {

    public static Table cache = null;
    public static boolean createTable(String name) throws IOException {
        File file = new File("tables/"+name);
        if(file.exists()) return false;
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
            bw.write(new Table(name).getTableDescriptor().getTableJSON());
        }
        return true;
    }

    public static void saveTable(Table.TableDescriptor table) throws IOException {
        File file = new File("tables/"+table.name);
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
            bw.write(table.getTableJSON());
        }
    }

    public static ArrayList<String> getTableList() throws IOException {
        File directory = new File("tables");
        ArrayList<String> tables = new ArrayList<>();
        if (directory.exists() && directory.isDirectory()) {
            ObjectMapper mapper = new ObjectMapper();
            for(File file : Objects.requireNonNull(directory.listFiles())){
                tables.add(file.getName());
            }
        }
        return tables;
    }
    public static Table getTable(String name) throws IOException {
        if(cache != null && cache.getName().equals(name)) {
            return cache;
        }
        File file = new File("tables/"+name);
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line = br.readLine();
            Table.TableDescriptor descriptor = new ObjectMapper().readValue(line, Table.TableDescriptor.class);
            return cache = Table.getTableFromDescriptor(descriptor);
        }
    }

    public static boolean deleteTable(String name) throws IOException {
        File file = new File("tables/"+name);
        if(!file.exists()) return false;
        return file.delete();
    }

    public static Table.TableDescriptor getTableDescriptor(String name) throws IOException {
        if(cache != null && cache.getName().equals(name)) {
            return cache.getTableDescriptor();
        }
        File file = new File("tables/"+name);
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line = br.readLine();
            Table.TableDescriptor descriptor = new ObjectMapper().readValue(line, Table.TableDescriptor.class);
            cache = Table.getTableFromDescriptor(descriptor);
            return descriptor;
        }
    }
}
