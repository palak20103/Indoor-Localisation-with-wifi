package com.example.assignment5;

import java.io.Serializable;

public class model implements Serializable {
    String name;
    int value;

public String getName() {
        return name;
        }

public void setName(String name) {
        this.name = name;
        }

public int getvalue() {
        return value;
        }

public void setvalue(int value) {
        this.value =value;
        }

        }