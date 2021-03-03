package info.jerrinot.portablemapstore.test.domain;

import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

import java.io.IOException;

public class Person implements Portable {
    private int id;
    private String name;
    private String lastname;

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                '}';
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
    }

    @Override
    public void readPortable(PortableReader r) throws IOException {
        id = r.readInt("id");
        name = r.readUTF("name");
        lastname = r.readUTF("lastname");
    }
}
