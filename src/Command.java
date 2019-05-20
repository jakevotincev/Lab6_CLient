import java.io.Serializable;

public class Command implements Serializable {
    private static final long serialVersionUID =2;
    private String name;
    private Animal object;
    private String file;

    public void setName(String name) {
        this.name = name;
    }

    public void setObject(Animal object) {
        this.object = object;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public Animal getObject() {
        return object;
    }

    public String getFile() {
        return file;
    }
}
