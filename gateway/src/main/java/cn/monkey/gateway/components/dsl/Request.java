package cn.monkey.gateway.components.dsl;


public class Request {

    private String method;
    private String path;

    public String getPath() {
        return path;
    }


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
