package com.f4w.utils;

import com.alibaba.fastjson.JSON;
import com.xxl.job.core.biz.model.ReturnT;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author houxm
 * @version 1.01 2018/4/17 19:33
 * @description
 */
@RestControllerAdvice("com.f4w")
@Slf4j
public class ExceptionHandlerAdvice {
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(value = HttpStatus.OK)
    public Object handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex, final ServletWebRequest req) {
        Result<String> result = new Result<>();
        result.setCode(ResultCode.PARAMS_ERROR);
        try {
            List<String> errorMap = this.validRequestParams(ex.getBindingResult());
            if ("请输入正确的手机号".equals(errorMap.get(0))) {
                result.setMessage(errorMap.get(0));
            }
            result.setErrorMsg(errorMap.get(0));
            //result.setErrorMsg(getValidError(ex.getBindingResult()));
        } catch (Exception e) {
            result.setErrorMsg("参数不合法");
        }
        return result;
    }

    /**
     * @return :
     * @Description: 统一异常处理 用于@RequestParam 的参数校验
     * @auther : wujingxiong
     * @date : 2019/3/18 15:43
     */
    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    @ResponseStatus(value = HttpStatus.OK)
    public Object handleMissingServletRequestParameterException(final MissingServletRequestParameterException ex, final ServletWebRequest req) {
        Result<String> result = new Result<>();
        result.setCode(ResultCode.PARAMS_ERROR);
        try {
            result.setErrorMsg(ex.getMessage());
        } catch (Exception e) {
            result.setErrorMsg("参数不合法");
        }
        return result;
    }

    /**
     * @return :
     * @Description: 统一异常处理 用于@RequestBody 的参数校验
     * @auther : wujingxiong
     * @date : 2019/3/18 15:43
     */
    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    @ResponseStatus(value = HttpStatus.OK)
    public Object handleHttpMessageNotReadableException(final HttpMessageNotReadableException ex, final ServletWebRequest req) {
        Result<String> result = new Result<>();
        result.setCode(ResultCode.PARAMS_ERROR);
        try {
            result.setErrorMsg("非法JSON字符串");
        } catch (Exception e) {
            result.setErrorMsg("参数不合法");
        }
        return result;
    }

    private List<String> validRequestParams(BindingResult result) {
        if (result.hasErrors()) {
            List<ObjectError> allErrors = result.getAllErrors();
            List<String> lists = new ArrayList<>();
            for (ObjectError objectError : allErrors) {
                lists.add(objectError.getDefaultMessage());
            }
            return lists;
        }
        return null;
    }

    @ExceptionHandler(value = {ShowException.class})
    @ResponseStatus(value = HttpStatus.OK)
    public Object handleShowExceptions(final Exception ex, final ServletWebRequest req) {
        Result result = new Result();
        ForeseenException fex = (ForeseenException) ex;
        result.setSuccess(false);
        if (fex.getErrorEnum() != null) {
            result.setCode(fex.getErrorEnum().getCode());
            result.setMessage(fex.getErrorEnum().getDescription());
            return result;
        }
        return result;
    }

    @ExceptionHandler(value = {ForeseenException.class})
    @ResponseStatus(value = HttpStatus.OK)
    public Object handleCustomerExceptions(final Exception ex, final ServletWebRequest req) {
        Result<Object> result = new Result<>();
        ForeseenException fex = (ForeseenException) ex;
        result.setSuccess(false);
        if (fex.getErrorEnum() != null) {
            result.setCode(fex.getErrorEnum().getCode());
            result.setMessage(fex.getErrorEnum().getDescription());
            return result;
        }
        result.setCode(fex.getCode());
        result.setErrorMsg(fex.getMessage());
        return result;
    }

    @ExceptionHandler(value = {BindException.class})
    @ResponseStatus(value = HttpStatus.OK)
    public Object handleBindExceptions(final Exception ex, final ServletWebRequest req) {
        Result result = new Result();
        BindException fex = (BindException) ex;
        result.setSuccess(false);
        result.setCode(ResultCode.RESULT_FAILURE);
        result.setErrorMsg(fex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        try {
            log.info("接口返回值：" + JSON.toJSONString(result));
        } catch (Exception innerex) {

        }
        return result;
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    @ResponseStatus(value = HttpStatus.OK)
    public Object handleConstraintViolationExceptions(final Exception ex, final ServletWebRequest req) {
        Result result = new Result();
        ConstraintViolationException fex = (ConstraintViolationException) ex;
        result.setSuccess(false);
        result.setCode(ResultCode.RESULT_FAILURE);
        for (ConstraintViolation<?> constraintViolation : fex.getConstraintViolations()) {
            result.setErrorMsg(constraintViolation.getMessage());
            break;
        }
        try {
            log.info("接口返回值：" + JSON.toJSONString(result));
        } catch (Exception innerex) {

        }
        return result;
    }

    @ExceptionHandler(value = {ExpiredJwtException.class})
    @ResponseStatus(value = HttpStatus.OK)
    public Object expiredJwtException(final Exception ex, final ServletWebRequest req) {
        Result result = new Result();
        ConstraintViolationException fex = (ConstraintViolationException) ex;
        result.setSuccess(false);
        result.setCode(ResultCode.AUTH_FAILED);
        for (ConstraintViolation<?> constraintViolation : fex.getConstraintViolations()) {
            result.setErrorMsg(constraintViolation.getMessage());
            break;
        }
        log.info("身份过期");
        return result;
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(value = HttpStatus.OK)
    public Object handleOtherExceptions(final Exception ex, final ServletWebRequest req) {
        Result<Object> result = new Result<>();
        log.error("全局异常", ex);
        if (ex.getCause() != null) {
            result.setErrorMsg(ex.getCause().getMessage());
        }
        result.setSuccess(false);
        result.setCode(ResultCode.RESULT_FAILURE);
        try {
            //过滤掉超时异常
            if (!"org.apache.catalina.connector.ClientAbortException".equals(ex.getClass().getName())) {
                //mailUtil.sendSimpleMail("907399951@qq.com", JSON.toJSONString(ex));
            }
            log.info("接口返回值：" + JSON.toJSONString(result));
        } catch (Exception innerex) {

        }
        return result;
    }

    @ExceptionHandler(value = {JobException.class})
    @ResponseStatus(value = HttpStatus.OK)
    public Object handleJobException(final Exception ex, final ServletWebRequest req) {
        log.error("定时任务执行异常", ex);
        return new ReturnT<>(ex.getMessage());
    }

    /**
     * @return :
     * @Description: 获取 注解校验中的所有异常信息;
     * @auther : wujingxiong
     * @date : 2019/3/18 16:21
     */
    private String getValidError(BindingResult bindingResult) {
        StringBuffer sb = new StringBuffer();
        for (ObjectError objectError : bindingResult.getAllErrors()) {
            sb.append(((FieldError) objectError).getField() + " : ").append(objectError.getDefaultMessage() + "; ");
        }
        return sb.toString();
    }
}
