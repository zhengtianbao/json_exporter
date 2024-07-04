package com.example.json_exporter.pojo;


public class TokenResponse {
    private Integer status;
    private String data;
    private Boolean rel;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Boolean getRel() {
        return rel;
    }

    public void setRel(Boolean rel) {
        this.rel = rel;
    }

    @Override
    public String toString() {
        return "TokenResponse{" +
                "status=" + status +
                ", data='" + data + '\'' +
                ", rel=" + rel +
                '}';
    }
}
