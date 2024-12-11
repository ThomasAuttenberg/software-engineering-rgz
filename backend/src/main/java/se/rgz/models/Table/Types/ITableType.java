package se.rgz.models.Table.Types;

public interface ITableType extends Cloneable, Comparable<ITableType> {
    String getType();
    String getValue();
    void setValue(String value) throws IllegalArgumentException;
    ITableType sum(ITableType other);
    ITableType clone();
}
