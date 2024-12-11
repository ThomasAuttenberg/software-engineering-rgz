package se.rgz.models.Table.Types.implementations;

import lombok.NonNull;
import se.rgz.models.Table.Types.ITableType;

import java.util.Objects;

public class IntegerType implements ITableType {

    static private final String type = "Integer";
    @Override
    public String getType() {
        return type;
    }

    private Integer value = null;

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
            this.value = Integer.parseInt(trimmedValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Illegal argument");
        }
    }

    @Override
    public ITableType sum(ITableType other) {
        if (other instanceof IntegerType otherInteger) {
            IntegerType result = new IntegerType();
            result.value = (this.value == null ? 0 : this.value) + (otherInteger.value == null ? 0 : otherInteger.value);
            return result;
        }
        return this.clone();
    }

    @Override
    public int compareTo(@NonNull ITableType o) {
        if (o instanceof IntegerType otherInteger) {
            if (this.value == null && otherInteger.value == null) return 0;
            if (this.value == null) return -1;
            if (otherInteger.value == null) return 1;
            return this.value.compareTo(otherInteger.value);
        }
        // Политика сравнения разных типов
        return 0;
    }

    @Override
    public ITableType clone() {
        IntegerType cloned = new IntegerType();
        cloned.value = this.value;
        return cloned;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof IntegerType other)) return false;
        return Objects.equals(this.value, other.value);
    }

    @Override
    public int hashCode() {
        return (value != null) ? value.hashCode() : 0;
    }
}
