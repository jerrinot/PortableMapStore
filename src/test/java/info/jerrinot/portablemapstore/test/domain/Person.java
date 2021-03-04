package info.jerrinot.portablemapstore.test.domain;

import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

import java.io.IOException;

public class Person implements Portable {
    private int id;
    private String name;
    private String lastname;
    private double doubleField;
    private boolean booleanField;

    public double getDoubleField() {
        return doubleField;
    }

    public void setDoubleField(double doubleField) {
        this.doubleField = doubleField;
    }

    public boolean isBooleanField() {
        return booleanField;
    }

    public void setBooleanField(boolean booleanField) {
        this.booleanField = booleanField;
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

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public int getFactoryId() {
        return 1;
    }

    @Override
    public int getClassId() {
        return 1;
    }

    @Override
    public void writePortable(PortableWriter w) throws IOException {
        w.writeInt("id", id);
        w.writeUTF("name", name);
        w.writeUTF("lastname", lastname);
        w.writeDouble("double", doubleField);
        w.writeBoolean("boolean", booleanField);
    }

    @Override
    public void readPortable(PortableReader r) throws IOException {
        id = r.readInt("id");
        name = r.readUTF("name");
        lastname = r.readUTF("lastname");
        doubleField = r.readDouble("double");
        booleanField = r.readBoolean("boolean");
    }
}
