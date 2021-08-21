package models;

import java.io.Serializable;

public interface Model<T> extends Comparable<T> {
    String show();
}
