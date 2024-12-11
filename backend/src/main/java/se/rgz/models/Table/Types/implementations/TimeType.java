package se.rgz.models.Table.Types.implementations;

import se.rgz.models.Table.Types.ITableType;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TimeType implements ITableType {

    static private final String type = "Time";
    @Override
    public String getType() {
        return type;
    }

    private LocalTime time = null;

    // Список распространённых форматеров для распознавания пользовательского ввода
    private static final List<DateTimeFormatter> FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("HH:mm:ss"),
            DateTimeFormatter.ofPattern("HH:mm"),
            DateTimeFormatter.ofPattern("h:mm a"),
            DateTimeFormatter.ofPattern("hh:mm a"),
            DateTimeFormatter.ofPattern("HHmm"),
            DateTimeFormatter.ofPattern("hmm a"),
            DateTimeFormatter.ofPattern("hhmm a")
    );

    @Override
    public String getValue() {
        if (time != null) {
            return time.format(DateTimeFormatter.ISO_LOCAL_TIME);
        }
        return null;
    }

    @Override
    public void setValue(String value) {
        if(value == null || value.isEmpty()) {
            this.time = null;
            return;
        }
        String trimmedValue = value.trim();
        LocalTime parsedTime = null;

        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                parsedTime = LocalTime.parse(trimmedValue, formatter);
                break; // Успешно распарсено, выходим из цикла
            } catch (DateTimeParseException e) {
                // Попытка с этим форматером не удалась, продолжаем
            }
        }

        if (parsedTime != null) {
            this.time = parsedTime;
        } else {
            throw new IllegalArgumentException("Illegal argument");
        }
    }

    @Override
    public ITableType sum(ITableType other) {
        if (other instanceof TimeType otherTime) {
            int thisSeconds = (this.time == null) ? 0 : this.time.toSecondOfDay();
            int otherSeconds = (otherTime.time == null) ? 0 : otherTime.time.toSecondOfDay();

            int totalSeconds = (thisSeconds + otherSeconds) % (24 * 3600); // Ограничиваем до 24 часов
            TimeType result = new TimeType();
            result.time = LocalTime.ofSecondOfDay(totalSeconds);
            return result;
        }
        return this.clone();
    }

    @Override
    public int compareTo(ITableType o) {
        if (o instanceof TimeType) {
            TimeType otherTime = (TimeType) o;
            if (this.time == null && otherTime.time == null) return 0;
            if (this.time == null) return -1;
            if (otherTime.time == null) return 1;
            return this.time.compareTo(otherTime.time);
        }
        // Политика сравнения при разных типах
        return 0;
    }

    @Override
    public ITableType clone() {
        TimeType cloned = new TimeType();
        cloned.time = this.time;
        return cloned;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TimeType other)) return false;
        return Objects.equals(this.time, other.time);
    }

    @Override
    public int hashCode() {
        return (time != null) ? time.hashCode() : 0;
    }
}