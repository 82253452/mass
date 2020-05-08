package com.f4w.utils;

public class Result<T> {
    private boolean success = true;

    private Integer code;

    private String msg;

    private String errorMsg;

    private T data;

    public Result() {
    }

    public Result(boolean success) {
        this.success = success;
    }

    public Result(boolean success, String msg, String errorMsg) {
        this.success = success;
        this.msg = msg;
        this.errorMsg = errorMsg;
    }

    public Result(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String message) {
        this.msg = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    /**
     * 生成action成功返回值
     *
     * @return
     * @throws ForeseenException
     */
    public static Result ok() throws ForeseenException {
        return ok(ResultCode.RESULT_SUCCESS, "");
    }

    /**
     * 生成action成功返回值
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return
     */
    public static <T> Result<T> ok(T data) throws ForeseenException {
        return ok(data, "");
    }

    /**
     * 生成action成功返回值
     *
     * @param data 数据
     * @param msg  提示消息
     * @param <T>  数据类型
     * @return
     */
    public static <T> Result<T> ok(T data, String msg) throws ForeseenException {
        return generate(true, ResultCode.RESULT_SUCCESS, data, msg, null);
    }

    public static <T> Result<T> ok(Integer code, String msg) throws ForeseenException {
        return generate(true, code, null, msg, null);
    }


    public static Result generate(Boolean success, String msg, String errorMsg) throws ForeseenException {
        return generate(success, null, null, msg, errorMsg);
    }

    public static Result generate(Boolean success) throws ForeseenException {
        return generate(success, null, null, null, null);
    }

    /**
     * 生成action失败返回值
     *
     * @param errorMsg 提示消息
     * @return
     * @throws ForeseenException
     */
    public static Result error(String errorMsg) throws ForeseenException {
        return generate(false, ResultCode.RESULT_FAILURE, null, null, errorMsg);
    }

    /**
     * 生成action失败返回值
     *
     * @param code     错误码
     * @param errorMsg 错误消息
     * @return
     * @throws ForeseenException
     */
    public static Result error(int code, String errorMsg) throws ForeseenException {
        return generate(false, code, null, null, errorMsg);
    }

    /**
     * 生成结果
     *
     * @param success
     * @param code
     * @param data
     * @param msg
     * @param <T>
     * @return
     * @throws ForeseenException
     */
    public static <T> Result<T> generate(Boolean success, Integer code, T data, String msg, String errorMsg) throws ForeseenException {
        if (success) {
            Result result = new Result<T>();
            result.setCode(code);
            result.setMessage(msg);
            result.setData(data);
            result.setSuccess(success);
            return result;
        } else {
            throw new ForeseenException(code == null ? ResultCode.RESULT_FAILURE : code, errorMsg);
        }
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}