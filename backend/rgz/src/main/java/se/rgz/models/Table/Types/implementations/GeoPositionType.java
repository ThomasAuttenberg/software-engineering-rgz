package se.rgz.models.Table.Types.implementations;

import lombok.Getter;
import lombok.NonNull;
import se.rgz.models.Table.Types.ITableType;

import java.util.Objects;

public class GeoPositionType implements ITableType {

    static private final String type = "GeoPosition";
    @Override
    public String getType() {
        return type;
    }

    @Getter
    public static class GeoPosition {

        private Double latitude;
        private Double longitude;

        public GeoPosition(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public void setLatitude(Double latitude) {
            if (latitude != null && (latitude < -90.0 || latitude > 90.0)) {
                throw new IllegalArgumentException("Широта должна быть в диапазоне от -90 до 90.");
            }
            this.latitude = latitude;
        }

        public void setLongitude(Double longitude) {
            if (longitude != null && (longitude < -180.0 || longitude > 180.0)) {
                throw new IllegalArgumentException("Долгота должна быть в диапазоне от -180 до 180.");
            }
            this.longitude = longitude;
        }

        @Override
        public String toString() {
            if (latitude != null && longitude != null) {
                return latitude + ", " + longitude;
            }
            return "";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof GeoPosition other)) return false;
            return Objects.equals(this.latitude, other.latitude) &&
                    Objects.equals(this.longitude, other.longitude);
        }

        @Override
        public int hashCode() {
            return Objects.hash(latitude, longitude);
        }
    }

    private GeoPosition geoPosition = null;

    @Override
    public String getValue() {
        if (geoPosition != null) {
            return geoPosition.toString();
        }
        return null;
    }

    @Override
    public void setValue(String value) {

        if(value == null || value.isEmpty()) {
            this.geoPosition = null;
            return;
        }

        String trimmedValue = value.trim();
        String[] parts = trimmedValue.split(",");

        if (parts.length != 2) {
            // Некорректный формат, устанавливаем null
            throw new IllegalArgumentException("Illegal argument");
        }

        try {
            double latitude = Double.parseDouble(parts[0].trim());
            double longitude = Double.parseDouble(parts[1].trim());

            // Проверка диапазона координат
            if (latitude < -90.0 || latitude > 90.0 || longitude < -180.0 || longitude > 180.0) {
                // Некорректные значения, устанавливаем null
                throw new IllegalArgumentException("Illegal argument");
            }

            this.geoPosition = new GeoPosition(latitude, longitude);
        } catch (NumberFormatException e) {
            // Некорректные числа, устанавливаем null
            throw new IllegalArgumentException("Illegal argument");
        }
    }

    @Override
    public ITableType sum(ITableType other) {
        // Сложение двух GPS-координат не определено
        return this.clone();
    }

    @Override
    public int compareTo(@NonNull ITableType o) {
        if (o instanceof GeoPositionType otherGeo) {
            if (this.geoPosition == null && otherGeo.geoPosition == null) return 0;
            if (this.geoPosition == null) return -1;
            if (otherGeo.geoPosition == null) return 1;
            int latComparison = Double.compare(this.geoPosition.getLatitude(), otherGeo.geoPosition.getLatitude());
            if (latComparison != 0) {
                return latComparison;
            }
            return Double.compare(this.geoPosition.getLongitude(), otherGeo.geoPosition.getLongitude());
        }
        // Политика сравнения различных типов
        return 0;
    }

    @Override
    public ITableType clone() {
        GeoPositionType cloned = new GeoPositionType();
        if (this.geoPosition != null) {
            cloned.geoPosition = new GeoPosition(
                    this.geoPosition.getLatitude(),
                    this.geoPosition.getLongitude()
            );
        }
        return cloned;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GeoPositionType other)) return false;
        return Objects.equals(this.geoPosition, other.geoPosition);
    }

    @Override
    public int hashCode() {
        return (geoPosition != null) ? geoPosition.hashCode() : 0;
    }
}