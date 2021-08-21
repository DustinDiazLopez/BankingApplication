package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Persist {

    /**
     * Write/dump a serializable object to a file.
     * @param file the path to the file
     * @param o the object to be written
     * @param <T> type of the object
     * @return whether the object was written to the file (may not be accurate)
     */
    public static <T> boolean write(final String file, final T o) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(o);
            out.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Reads the written/dumped object from a given file
     * @param file the path to the file
     * @param cls the class if the object in the file
     * @param <T> type of the object in the file
     * @return the object read from the file
     */
    public static <T> T read(final String file, final Class<T> cls) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return cls.cast(in.readObject());
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
