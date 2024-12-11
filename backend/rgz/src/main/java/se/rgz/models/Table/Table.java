package se.rgz.models.Table;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import se.rgz.models.Table.Types.ITableType;
import se.rgz.models.Table.Types.implementations.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;

public class Table{

    @Getter @Setter
    private String name;

    private static class  Column{
        String name = null;
        final String typeName;
        @JsonIgnore
        final ITableType typeInstance;

        Column(@NonNull ITableType type){
            typeInstance = type;
            typeName = type.getType();
        }
        Column(@NonNull ITableType type, String name){
            typeInstance = type;
            typeName = type.getType();
            this.name = name;
        }

    }
    public Table(){

    }
    public Table(String name){
        this.name = name;
    }

    private final ArrayList<Column> columns = new ArrayList<>();
    private final ArrayList<Row> rows = new ArrayList<>();

    private static class Row{
        final ArrayList<ITableType> fields = new ArrayList<>();
        Row(ArrayList<Column> columns){
            for(Column column : columns){
                fields.add(column.typeInstance.clone());
            }
        }
        public Row cloneRow() {
            Row clonedRow = new Row(new ArrayList<>()); // Создаём пустую строку
            for (ITableType field : this.fields) {
                clonedRow.fields.add(field.clone()); // Клонируем каждое поле
            }
            return clonedRow;
        }
    }

    public LinkedList<String> sum(int srcRowIndex, int sumWithRowIndex){
        if(srcRowIndex >= rows.size() || sumWithRowIndex >= rows.size() || srcRowIndex < 0 || sumWithRowIndex < 0){
            throw new IndexOutOfBoundsException();
        }
        LinkedList<String> result = new LinkedList<>();
        Row srcRow = rows.get(srcRowIndex);
        Row sumWithRow = rows.get(sumWithRowIndex);
        for (int i = 0; i < columns.size(); i++) {
            result.add(srcRow.fields.get(i).sum(sumWithRow.fields.get(i)).getValue());
        }
        return result;
    }


    public void sortByColumn(int column){
        rows.sort(new Comparator<Row>() {
            @Override
            public int compare(Row o1, Row o2) {
                int a = o1.fields.get(column).compareTo(o2.fields.get(column));
                return a;
            }
        });
    }

    public void addColumn(ITableType type){
        columns.add(new Column(type, "VAR "+ String.valueOf(columns.size()) ) );
        if(!rows.isEmpty()){
            for(Row row : rows){
                row.fields.add(type.clone());
            }
        }
    }
    public void addColumn(ITableType type, String name){
        columns.add(new Column(type, name));
        if(!rows.isEmpty()){
          for(Row row : rows){
              row.fields.add(type.clone());
          }
        }
    }

    public void addRow(){
        rows.add(new Row(columns));
    }

    public void cloneRow(int index){
        if(index >= rows.size() || index < 0)
            throw new IndexOutOfBoundsException();
        rows.add(rows.get(index).cloneRow());
    }

    public void removeColumn(int index){
        if(index >= columns.size() || index < 0)
            throw new IndexOutOfBoundsException();

        columns.remove(index);
        for(Row row : rows){
            row.fields.remove(index);
        }
    }

    public void removeRow(int index){
        if(index >= rows.size() || index < 0){
            throw new IndexOutOfBoundsException();
        }
        rows.remove(index);
    }

    @AllArgsConstructor
    public static class Coords{
        public int rowNum;
        public int columnNum;
    }

    public void setValue(@NonNull Coords coords, String value) {
        if(coords.rowNum >= rows.size() || coords.columnNum >= columns.size()){
            throw new IndexOutOfBoundsException("Range exception");
        }
        rows.get(coords.rowNum).fields.get(coords.columnNum).setValue(value);
    }
    public ITableType getValue(@NonNull Coords coords) {
        if(coords.rowNum >= rows.size() || coords.columnNum >= columns.size()){
            throw new IndexOutOfBoundsException("Range exception");
        }
        return rows.get(coords.rowNum).fields.get(coords.columnNum);
    }

    public static class TableDescriptor{
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ColumnDescriptor{
            public String typeName = null;
            public String name = null;
        }
        public String name;
        public ColumnDescriptor[] columns;
        public String[][] data;
        @JsonIgnore
        public String getTableJSON(){
            TableDescriptor descriptor = this;
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            try {
                return mapper.writeValueAsString(descriptor);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public TableDescriptor getTableDescriptor(){
        TableDescriptor descriptor = new TableDescriptor();
        descriptor.name = name;
        descriptor.columns = new TableDescriptor.ColumnDescriptor[columns.size()];
        descriptor.data = new String[rows.size()][columns.size()];
        for(int i = 0; i< columns.size(); i++){
            descriptor.columns[i] = new TableDescriptor.ColumnDescriptor(columns.get(i).typeName, columns.get(i).name);
            for(int j = 0; j< rows.size(); j++){
                descriptor.data[j][i] = rows.get(j).fields.get(i).getValue();
            }
        }
        return descriptor;
    }

    public static Table getTableFromDescriptor(TableDescriptor descriptor) {
        Table table = new Table();
        table.name = descriptor.name;

        for (TableDescriptor.ColumnDescriptor column : descriptor.columns) {
            ITableType type = getTypeFromName(column.typeName);
            table.columns.add(new Column(type, column.name));
        }

        for (int i = 0; i < descriptor.data.length; i++) {
            table.addRow();
            Row row = table.rows.get(i);
            for (int j = 0; j < descriptor.data[i].length; j++) {
                row.fields.get(j).setValue(descriptor.data[i][j]);
            }
        }

        return table;
    }

    public static ITableType getTypeFromName(String typeName) {
        return switch (typeName) {
            case "Date" -> new DateType();
            case "Integer" -> new IntegerType();
            case "GeoPosition" -> new GeoPositionType();
            case "Time" -> new TimeType();
            case "Number" -> new NumberType();
            default -> throw new IllegalArgumentException("Unknown type: " + typeName);
        };
    }


}
