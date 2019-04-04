package com.tegongdete.luckyme.util;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class ProtoUtil {
    public static <T> byte[] getBytes(T obj) {
        Class<T> cls = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = RuntimeSchema.getSchema(cls);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    public static <T> void getObj(byte[] bytes, T obj) {
        Class<T> cls = (Class<T>) obj.getClass();
        Schema<T> schema = RuntimeSchema.getSchema(cls);
        //Person p = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
    }
}
