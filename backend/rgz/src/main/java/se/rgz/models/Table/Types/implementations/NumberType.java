package se.rgz.models.Table.Types.implementations;

import lombok.NonNull;
import se.rgz.models.Table.Types.ITableType;

import java.util.Objects;

public class NumberType implements ITableType {

    static private final String type = "Number";
    @Override
    public String getType() {
        return type;
    }

    private Double value = null;

    @Override
    public String getValue() {
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    @Override
    public void setValue(String value) {

        if(value == null || value.isEmpty()) {
            this.value = null;
            return;
        }

        String trimmedValue = value.trim();
        try {
            this.value = Double.parseDouble(trimmedValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Illegal argument");
        }
    }


    @Override
    public ITableType sum(ITableType other) {
        if (other instanceof NumberType otherNumber) {
            NumberType result = new NumberType();
            result.value = (this.value == null ? 0 : this.value) + (otherNumber.value == null ? 0 : otherNumber.value);
            return result;
        }
        return this.clone();
    }

    @Override
    public int compareTo(@NonNull ITableType o) {
        if (o instanceof NumberType otherNumber) {
            if (this.value == null && otherNumber.value == null) return 0;
            if (this.value == null) return -1;
            if (otherNumber.value == null) return 1;
            return this.value.compareTo(otherNumber.value);
        }
        // Политика сравнения при разных типах
        return 0;
    }

    @Override
    public ITableType clone() {
        NumberType cloned = new NumberType();
        cloned.value = this.value;
        return cloned;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof NumberType other)) return false;
        return Objects.equals(this.value, other.value);
    }

    @Override
    public int hashCode() {
        return (value != null) ? value.hashCode() : 0;
    }
}