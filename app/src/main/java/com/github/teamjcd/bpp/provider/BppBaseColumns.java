package com.github.teamjcd.bpp.provider;

import android.provider.BaseColumns;

public abstract class BppBaseColumns implements BaseColumns {
    private int id;
    private String name;
    private long value;
    private int isDefault;

    protected BppBaseColumns(String name, long value) {
        setName(name);
        setValue(value);
    }

    protected BppBaseColumns(int id, String name, long value, int isDefault) {
        this(name, value);
        setId(id);
        setIsDefault(isDefault);
    }

    protected BppBaseColumns() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isDefault() {
        return isDefault == 1;
    }
}
