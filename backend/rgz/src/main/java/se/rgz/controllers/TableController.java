package se.rgz.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.rgz.models.Responses.ErrorResponse;
import se.rgz.models.Requests.SetValueRequest;
import se.rgz.models.Table.Table;
import se.rgz.models.Table.TableManager;
import se.rgz.models.Table.Types.ITableType;

import java.io.IOException;

@RestController
@RequestMapping("/tables")
@CrossOrigin(origins = "http://localhost:5173")
public class TableController  {

    // 1. Создание Таблицы
    @PostMapping("/{name}")
    public ResponseEntity<?> createTable(@PathVariable String name){
        try {
            boolean created = TableManager.createTable(name);
            if(!created) {
                ErrorResponse error = new ErrorResponse("FILE_EXISTS", "Файл с таким именем уже существует.");
                return new ResponseEntity<>(error, HttpStatus.CONFLICT); // 409 Conflict
            }
        } catch (IOException e) {
            ErrorResponse error = new ErrorResponse("IO_ERROR", "Непредвиденная ошибка при создании таблицы.");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
        return new ResponseEntity<>(new MessageResponse("success"), HttpStatus.CREATED); // 201 Created
    }

    @PatchMapping("/{name}")
    public ResponseEntity<?> updateTable(@PathVariable String name){
        try {
            TableManager.saveTable(TableManager.getTableDescriptor(name));
        } catch (IOException e) {
            ErrorResponse error = new ErrorResponse("IO_ERROR", "Непредвиденная ошибка при сохранении таблицы.");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
        return new ResponseEntity<>(new MessageResponse("success"), HttpStatus.OK);
    }

    // 2. Добавление Строки
    @PostMapping("/{name}/row")
    public ResponseEntity<?> addRow(@PathVariable String name){
        try {
            TableManager.getTable(name).addRow();
        } catch (IOException e) {
            ErrorResponse error = new ErrorResponse("IO_ERROR", "Не удалось получить таблицу из репозитория.");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
        return new ResponseEntity<>(new MessageResponse("success"), HttpStatus.OK); // 200 OK
    }

    // 3. Добавление Столбца
    @PostMapping("/{name}/column")
    public ResponseEntity<?> addColumn(@PathVariable String name, @RequestBody AddColumnRequest columnRequest){
        try {
            ITableType tableType = Table.getTypeFromName(columnRequest.getType());
            TableManager.getTable(name).addColumn(tableType, columnRequest.getName());
        } catch (IOException e) {
            ErrorResponse error = new ErrorResponse("IO_ERROR", "Не удалось получить таблицу из репозитория.");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        } catch (IllegalArgumentException e) {
            ErrorResponse error = new ErrorResponse("INVALID_TYPE", "Неверный тип столбца: " + columnRequest.getType());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
        return new ResponseEntity<>(new MessageResponse("success"), HttpStatus.OK); // 200 OK
    }

    @DeleteMapping("/{name}/column/{colIndex}")
    public ResponseEntity<?> deleteColumn(@PathVariable String name, @PathVariable int colIndex){
        try{
            TableManager.getTable(name).removeColumn(colIndex);
        } catch (IOException e) {
            ErrorResponse error = new ErrorResponse("IO_ERROR", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new MessageResponse("success"), HttpStatus.OK);
    }

    @DeleteMapping("/{name}/row/{rowIndex}")
    public ResponseEntity<?> deleteRow(@PathVariable String name, @PathVariable int rowIndex){
        try{
            TableManager.getTable(name).removeRow(rowIndex);
        } catch (IOException e) {
            ErrorResponse error = new ErrorResponse("IO_ERROR", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new MessageResponse("success"), HttpStatus.OK);
    }

    // 4. Получение Таблицы
    @GetMapping("/{name}")
    public ResponseEntity<?> getTable(@PathVariable String name){
        try {
            Table.TableDescriptor descriptor = TableManager.getTableDescriptor(name);
            if(descriptor == null){
                ErrorResponse error = new ErrorResponse("NOT_FOUND", "Таблица с именем " + name + " не найдена.");
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // 404 Not Found
            }
            return new ResponseEntity<>(descriptor, HttpStatus.OK); // 200 OK
        } catch (IOException e) {
            //ErrorResponse error = new ErrorResponse("IO_ERROR", "Не удалось получить таблицу из репозитория.");
            ErrorResponse error = new ErrorResponse("IO_ERROR", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    // 5. Установка Значения
    @PutMapping("/{name}")
    public ResponseEntity<?> setValue(@PathVariable String name, @RequestBody SetValueRequest request){
        try {
            TableManager.getTable(name).setValue(new Table.Coords(request.getRow(), request.getColumn()), request.getValue());
        } catch (IOException e) {
            ErrorResponse error = new ErrorResponse("IO_ERROR", "Не удалось получить таблицу из репозитория.");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        } catch (IndexOutOfBoundsException e) {
            ErrorResponse error = new ErrorResponse("INDEX_OUT_OF_BOUNDS", "Некорректные номера строки или столбца.");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
        catch (IllegalArgumentException e){
            ErrorResponse error = new ErrorResponse("INDEX_OUT_OF_BOUNDS", "Некорректное значение.");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
        return new ResponseEntity<>(new MessageResponse("success"), HttpStatus.OK); // 200 OK
    }

    @GetMapping
    public ResponseEntity<?> getAllTables(){
        try{
            return new ResponseEntity<>(TableManager.getTableList(),HttpStatus.OK);
        } catch (IOException e) {
            ErrorResponse error = new ErrorResponse("IO_ERROR", "Не удалось получить список таблиц из репозитория.");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }
    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteTable(@PathVariable String name){
        try{
            if(TableManager.deleteTable(name)){
                return new ResponseEntity<>(new MessageResponse("success"), HttpStatus.OK);
            }else{
                ErrorResponse error = new ErrorResponse("FILE_ERROR", "Не удалось удалить таблицу.");
                return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            }
        } catch (IOException e) {
            ErrorResponse error = new ErrorResponse("FILE_ERROR", "Не удалось удалить таблицу.");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    @AllArgsConstructor @Getter @Setter
    public static class MessageResponse {
        private String message;
    }
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class AddColumnRequest{
        private String type;
        private String name;
    }
}