package com.kcube.trns.sunjin.common;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.Map;

/*****************************************************
 *
 *  partition parameter 를 찾아내기위한 역직렬화 util
 *
 ****************************************************/
public class BatchContextDeserializer {

    public static void main(String[] args) throws Exception {

        // select short_context from batch_step_execution_context where step_execution_id = ${해당하는 step 의 id};
        String short_context = "rO0ABXNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZhY3RvckkACXRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAAHdAAgSmRiY1BhZ2luZ0l0ZW1SZWFkZXIuc3RhcnQuYWZ0ZXJzcgAXamF2YS51dGlsLkxpbmtlZEhhc2hNYXA0wE5cEGzA+wIAAVoAC2FjY2Vzc09yZGVyeHEAfgAAP0AAAAAAAAx3CAAAABAAAAABdAAKZG9jdW1lbnRpZHNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAm+ReAB0AAVtYXhJZHNyAA5qYXZhLmxhbmcuTG9uZzuL5JDMjyPfAgABSgAFdmFsdWV4cQB+AAcAAAAAAAN1InQAH0pkYmNQYWdpbmdJdGVtUmVhZGVyLnJlYWQuY291bnRzcQB+AAYAABdwdAARYmF0Y2gudGFza2xldFR5cGV0AD1vcmcuc3ByaW5nZnJhbWV3b3JrLmJhdGNoLmNvcmUuc3RlcC5pdGVtLkNodW5rT3JpZW50ZWRUYXNrbGV0dAAFbWluSWRzcQB+AAoAAAAAAAJOHXQADWJhdGNoLnZlcnNpb250AAU1LjIuMnQADmJhdGNoLnN0ZXBUeXBldAA3b3JnLnNwcmluZ2ZyYW1ld29yay5iYXRjaC5jb3JlLnN0ZXAudGFza2xldC5UYXNrbGV0U3RlcHg=";
        byte[] data = Base64.getDecoder().decode(short_context);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object obj = ois.readObject();
        ois.close();

        if (obj instanceof Map) {
            Map<?, ?> context = (Map<?, ?>) obj;
            context.forEach((k, v) -> System.out.println(k + " = " + v));
        } else {
            System.out.println("ExecutionContext가 Map이 아님: " + obj.getClass().getName());
        }
    }

    /** 결과 예시 **
     JdbcPagingItemReader.start.after = {documentid=158376}
     maxId = 160001 -----------------------------------------------------------------------------------> 필요로 하는 값
     JdbcPagingItemReader.read.count = 54000
     batch.taskletType = org.springframework.batch.core.step.item.ChunkOrientedTasklet
     minId = 80001 -----------------------------------------------------------------------------------> 필요로 하는 값
     batch.version = 5.2.2
     batch.stepType = org.springframework.batch.core.step.tasklet.TaskletStep
     */
}
