package com.github.teamjcd.bpp.provider;

import android.provider.BaseColumns;

public abstract class BppBaseColumns implements BaseColumns {
    private int id;
    private String name;
    private int value;
    private int isDefault;

    public BppBaseColumns(String name, int value) {
        setName(name);
        setValue(value);
    }

    public BppBaseColumns(int id, String name, int value, int isDefault) {
        this(name, value);
        setId(id);
        setIsDefault(isDefault);
    }

    public BppBaseColumns() {
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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
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
