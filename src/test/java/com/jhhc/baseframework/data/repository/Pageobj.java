package com.jhhc.baseframework.data.repository;

/**
 *
 * @author yecq
 */
public class Pageobj {

    private String id;
    private String value;

    public Pageobj() {
    }

    public Pageobj(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
