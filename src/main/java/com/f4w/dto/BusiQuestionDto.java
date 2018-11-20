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
    public String toString() {
        String out = this.getTitle() + "/n";
        String[] question = this.getQuestions().split("&");
        if (ArrayUtils.isNotEmpty(question)) {
            for (int i = 0; i < question.length; i++) {
                out += question[i] + "/n";
            }
        }
        return out;
    }
}
