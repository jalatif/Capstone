/**
 * Created with IntelliJ IDEA.
 * User: jalatif
 * Date: 4/10/13
 * Time: 1:14 AM
 * To change this template use File | Settings | File Templates.
 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class SerializationBox implements Serializable {

    private byte serializableProp = 10;

    public byte getSerializableProp() {
        return serializableProp;
    }
}

public class Serialization {

    public static void main(String args[]) throws IOException,
            FileNotFoundException, ClassNotFoundException {

        SerializationBox serialB = new SerializationBox();
        serialize("serial.out", serialB);
        SerializationBox sb = (SerializationBox) deSerialize("serial.out");
        System.out.println(sb.getSerializableProp());
        List<Integer> l = new ArrayList<Integer>();
        l.add(1);
        l.add(3);
        l.add(2);
    }

    public static void serialize(String outFile, Object serializableObject)
            throws IOException {
        FileOutputStream fos = new FileOutputStream(outFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(serializableObject);
    }

    public static Object deSerialize(String serilizedObject)
            throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(serilizedObject);
        ObjectInputStream ois = new ObjectInputStream(fis);
        return ois.readObject();
    }
}