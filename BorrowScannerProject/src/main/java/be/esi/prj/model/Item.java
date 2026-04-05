package be.esi.prj.model;

public class Item {

    private String id;
    private String name;
    private String category;
    private String status;
    private String qrPath;

    public Item(String id, String name, String category, String status, String qrPath) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.status = status;
        this.qrPath = qrPath;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }

    public String getQrPath() {
        return qrPath;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
