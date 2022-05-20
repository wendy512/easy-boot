package io.github.wendy512.serialization;

/**
 * 序列化方式
 * 
 * @author taowenwu
 * @date 2021-05-18 11:04:11:04
 * @since 1.0.0
 */
public enum SerializeType {
    KRYO("kryo"), HESSIAN("hessian"), PROTOSTUFF("protostuff"), JDK("jdk"), JSON("json");

    private String name;

    SerializeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
