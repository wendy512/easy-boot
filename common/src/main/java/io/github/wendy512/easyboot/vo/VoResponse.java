package io.github.wendy512.easyboot.vo;

import java.util.HashMap;
import java.util.Map;

import io.github.wendy512.easyboot.common.exception.BizException;

/**
 * 视图层响应客户端统一对象
 * @param <T> 泛型
 */
public class VoResponse<T> {
    
    public static final String CODE_KEY = "code";
    public static final String DATA_KEY = "data";
    private String code;
    private String msg;
    private T data;
    private Map<String,Object> extra;
    private boolean success;

    public VoResponse(String code, String msg, T data, Map<String, Object> extra, boolean success) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.extra = extra;
        this.success = success;
    }

    public VoResponse() {}

    public static class VoResponseBuilder<T> {
        private String code;
        private String msg;
        private T data;

        public VoResponse<T> build() {
            Map<String, Object> extra = buildExtra();
            return new VoResponse(this.code, this.msg, this.data, extra, ResponseCode.SUCCESS.getCode().equals(this.code));
        }

        private Map<String, Object> buildExtra() {
            Map<String,Object> extra = new HashMap<>(4);
            Long start = DurationContext.start();

            extra.put("start", start);
            extra.put("tid", TraceContext.traceId());
            Long end = DurationContext.end();

            if (end == null) {
                end = System.currentTimeMillis();
            }
            extra.put("end", end == null ? System.currentTimeMillis() : end);

            if (null != start) {
                extra.put("duration", end - start);
            }
            return extra;
        }

        public VoResponse<T> failure() {
            return resp(ResponseCode.SYSTEM_ERROR).build();
        }

        public VoResponse<T> failure(Exception e) {
            if (e instanceof BizException) {
                return code(((BizException)e).getCode()).msg(((BizException)e).getMsg())
                    .build();
            }
            return resp(ResponseCode.SYSTEM_ERROR).build();
        }

        public VoResponse<T> ok() {
            return ok(null);
        }

        public VoResponse<T> ok(T data) {
            return resp(ResponseCode.SUCCESS).data(data).build();
        }

        public VoResponseBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        public VoResponseBuilder<T> msg(String msg) {
            this.msg = msg;
            return this;
        }

        public VoResponseBuilder<T> code(String code) {
            this.code = code;
            return this;
        }

        public VoResponseBuilder<T> resp(ResponseCode responseCode) {
            this.code = responseCode.getCode();
            this.msg = responseCode.getMsg();
            return this;
        }
    }

    public static <T> VoResponseBuilder<T> builder() {
        return new VoResponseBuilder();
    }

    public boolean getSuccess() {
        return this.success;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
    
    public static class DurationContext {
        
        private static final ThreadLocal<Long> startTime = new ThreadLocal<>();
        private static final ThreadLocal<Long> endTime = new ThreadLocal<>();
        
        public static void start(long ts) {
            if (null == startTime.get()) {
                startTime.set(ts);
            }
        }
        
        public static Long start() {
            return startTime.get();
        }

        public static void end(long ts) {
            if (null == endTime.get()) {
                endTime.set(ts);
            }
        }

        public static Long end() {
            return endTime.get();
        }

        public static void removeAll() {
            startTime.remove();
            endTime.remove();
        }
    }
}
