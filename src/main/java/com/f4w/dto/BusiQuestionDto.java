package com.f4w.dto;

import lombok.Data;
import com.f4w.entity.BusiQuestion;
import org.apache.commons.lang3.ArrayUtils;

/**
 * createBy:2018-11-19 15:18:38
 *
 * @author yp
 */
@Data
public class BusiQuestionDto extends BusiQuestion {
    private static final String[] pList = {"A", "B", "C", "D", "E", "H"};

    public String toString() {
        String out = "题目：" + this.getTitle() + "\n";
//        String[] question = this.getQuestions().split("&");
//        if (ArrayUtils.isNotEmpty(question)) {
//            out += "选项：" + "\n";
//            for (int i = 0; i < question.length; i++) {
//                out += pList[i] + "：" + question[i] + "\n";
//            }
//        }
        out += "正确答案：" + pList[this.getRight()] + "\n";
        return out;
    }
}
