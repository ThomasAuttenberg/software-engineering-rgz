package se.rgz.models.Table.Types.implementations;

import lombok.NonNull;
import se.rgz.models.Table.Types.ITableType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class DateType implements ITableType {
    static private final String type = "Date";
    private LocalDate date = null;

    // Список распространённых форматеров для распознавания пользовательского ввода
    private static final List<DateTimeFormatter> FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("MM-dd-yyyy"),
            DateTimeFormatter.ofPattern("dd MMM yyyy"),
            DateTimeFormatter.ofPattern("MMM dd, yyyy"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd"),
            DateTimeFormatter.ofPattern("dd-MMM-yyyy"),
            DateTimeFormatter.ofPattern("MMM dd yyyy")
    );

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getValue() {
        if (date != null) {
            // Стандартный формат для отображения
            return date.format(DateTimeFormatter.ISO_DATE);
        }
        return null;
    }


    @Override
    public void setValue(String value) {

        if(value == null || value.isEmpty()) {
            this.date = null;
            return;
        }

        String trimmedValue = value.trim();
        LocalDate parsedDate = null;

        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                parsedDate = LocalDate.parse(trimmedValue, formatter);
                break; // Успешно распарсено, выходим из цикла
            } catch (DateTimeParseException e) {
                // Попытка с этим форматером не удалась, продолжаем
            }
        }

        if (parsedDate != null) {
            this.date = parsedDate;
        } else {
            // Если ни один форматер не смог распарсить дату, устанавливаем null
            throw new IllegalArgumentException("Illegal argument");
        }
    }


    @Override
    public ITableType sum(ITableType other) {
        // Сложение двух дат не определено
        return this.clone();
    }

    @Override
    public int compareTo(@NonNull ITableType other) {
        if (other instanceof DateType otherDate) {
            if (this.date == null && otherDate.date == null) return 0;
            if (this.date == null) return -1;
            if (otherDate.date == null) return 1;
            return this.date.compareTo(otherDate.date);
        }
        // Политика сравнения при различных типах
        return 0;
    }

    @Override
    public ITableType clone() {
        DateType cloned = new DateType();
        cloned.date = this.date;
        return cloned;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DateType other)) return false;
        return (this.date == null && other.date == null) ||
                (this.date != null && this.date.equals(other.date));
    }

    @Override
    public int hashCode() {
        return (date != null) ? date.hashCode() : 0;
    }
}