package com.afollestad.silk;

import java.io.Serializable;

public interface SilkComparable<Type> extends Serializable {

    public void setSilkId(long id);

    public long getSilkId();

    public boolean equalTo(Type other);
}
