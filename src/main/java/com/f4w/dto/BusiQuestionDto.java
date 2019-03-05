package com.f4w.dto;

import com.f4w.entity.BusiQuestion;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * createBy:2018-11-19 15:18:38
 *
 * @author yp
 */
@Slf4j
@Data
public class BusiQuestionDto extends BusiQuestion {
    private static final String[] pList = {"A", "B", "C", "D", "E", "H"};

    public String toString() {
        if (null == this.getType()) {
            return "";
        }
//        if (3 == this.getType()) {
//            if (StringUtils.equals("1", this.getAnswer())) {
//                return "正确";
//            }
//            return "错误";
//        }


        String out = "题目：" + this.getTitle() + "\n";
        out += "答案： \n";
        if (3 == this.getType()) {
            if (StringUtils.equals("1", getAnswer())) {
                return "正确\n";
            }
            return "错误\n";
        } else {
            String[] queArray = getQuestions().split("&");
            if (ArrayUtils.isEmpty(queArray)) {
                return "";
            }
            Integer l = getAnswer().length();
            for (Integer i = 0; i < l; i++) {
                char a = getAnswer().charAt(i);
                try {
                    out += queArray[Integer.valueOf(String.valueOf(a))] + "\n";
                } catch (Exception e) {
                    out += "无效答案\n";
                    log.error("匹配答案失败:" + getId());
                }
            }
        }
        return out;
    }
}
