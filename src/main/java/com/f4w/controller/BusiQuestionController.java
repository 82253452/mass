package com.f4w.controller;


import com.f4w.annotation.CurrentUser;
import com.f4w.entity.BusiQuestion;
import com.f4w.entity.SysUser;
import com.f4w.mapper.BusiQuestionMapper;
import com.f4w.utils.R;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/busiQuestion")
public class BusiQuestionController {
    @Resource
    public BusiQuestionMapper busiQuestionMapper;

    @PostMapping("/importQue")
    public R importQue(@CurrentUser SysUser user, String appId, @RequestParam("file") MultipartFile file) throws IOException {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(xssfWorkbook.getActiveSheetIndex());
        XSSFRow row;
        int i = 0;
        String t = "";
        StringBuffer data = new StringBuffer();
        DataFormatter formatter = new DataFormatter();
        while (true) {
            row = xssfSheet.getRow(i);
            t = formatter.formatCellValue(row.getCell(0));
            if ("end".equals(t)) {
                break;
            }
            i++;
            data.append(t);
            data.append("&");
            if (i > 10000) {
                break;
            }
        }
        String d = data.toString();
        System.out.println(d);
        saveContent(user.getId(), appId, d);
        return R.ok().put("data", "");
    }

    @GetMapping("/selectByPage")
    public R selectByPage(@CurrentUser SysUser user, @RequestParam Map map) {
        BusiQuestion busiQuestion = new BusiQuestion();
        busiQuestion.setUid(user.getId());
        PageHelper.startPage(MapUtils.getIntValue(map, "page", 1), MapUtils.getIntValue(map, "rows", 10));
        List<BusiQuestion> list = busiQuestionMapper.select(busiQuestion);
        PageInfo<BusiQuestion> page = new PageInfo<>(list);
        return R.ok().put("data", page);
    }

    @PostMapping("/insert")
    public R insert(@CurrentUser SysUser user, @RequestBody BusiQuestion BusiQuestion) {
        BusiQuestion.setUid(user.getId());
        int r = busiQuestionMapper.insert(BusiQuestion);
        return R.ok().put("data", r);
    }

    @GetMapping("/selectById")
    public R selectById(String id) {
        BusiQuestion r = busiQuestionMapper.selectByPrimaryKey(id);
        return R.ok().put("data", r);
    }

    @PostMapping("/updateById")
    public R updateById(@RequestBody BusiQuestion BusiQuestion) {
        int r = busiQuestionMapper.updateByPrimaryKeySelective(BusiQuestion);
        return R.ok().put("data", r);
    }

    @PostMapping("/deleteByIds")
    public R deleteByIds(@RequestBody String ids) {
        int r = busiQuestionMapper.deleteByIds(ids);
        return R.ok().put("data", r);
    }

    @PostMapping("/deleteById")
    public R deleteById(@RequestBody Map param) {
        int r = busiQuestionMapper.deleteByPrimaryKey(MapUtils.getInteger(param, "id"));
        return R.ok().put("data", r);
    }

    public void saveContent(Long uid, String appId, String content) {
        String s = content;
//        String pattern = "(?:(?:\\d&(?:(?:【(.*?)】(.*?)(（(.*?)）)?.*?&(?:([A-Za-z])(.*?)&)*)|(?:(.*?)(（(.*?)）)?.*?&(([A-Za-z])(.*?)&)*(正确答案：(.*?)&)?)))|(?:&+)|(?:.*?（.*?）&))";
        String pattern = "(?:(?:\\d&(?:(?:【(.*?)】((.*?)(（(.*?)）)?(.*?))&((([A-Da-d])(.*?)&)?(([A-Da-d])(.*?)&)?(([A-Da-d])(.*?)&)?(([A-Da-d])(.*?)&)?)?)" +
                "|(?:(((.*?)(（(.*?)）)?).*?)&((([A-Da-d])(.*?)&)?(([A-Da-d])(.*?)&)?(([A-Da-d])(.*?)&)?(([A-Da-d])(.*?)&)?)?(正确答案：(.*?)&)?)))" +
                "|(?:&+)" +
                "|(?:.*?（.*?）&))";
        String titlePattern = "(.*?)(（(.*?)）)";
        String answerPattern = "正确答案：(.*?)&";
        Pattern r = Pattern.compile(pattern);
        Pattern titleR = Pattern.compile(titlePattern);
        Matcher m = r.matcher(s);
        Pattern answerR = Pattern.compile(answerPattern);
        while (m.find()) {
            int i = m.groupCount();
//            for (int i1 = 0; i1 < i; i1++) {
//                System.out.println(m.group(i1) + "----" + i1);
//            }
            String type = m.group(1);
            int typeP = 1;
            String title = "";
            String answer = "";
            String options = "";
            String titleAndAnswer = "";
            String answerStr = "";
            String answerSortStr = "abcd";

            if (null != type) {
                if ("多选题".equals(type)) {
                    typeP = 2;
                } else if ("判断题".equals(type)) {
                    typeP = 3;
                    titleAndAnswer = m.group(2);
                    Matcher titleMatcher = titleR.matcher(titleAndAnswer);
                    if (titleMatcher.find()) {
                        title = titleMatcher.group(1);
                        answerStr = titleMatcher.group(3).toLowerCase();
                        if (StringUtils.containsAny(answerStr, "正确", "是", "√")) {
                            answer = "1";
                        } else if (StringUtils.containsAny(answerStr, "错误", "否", "×")) {
                            answer = "0";
                        }
                    }
                }
                if (typeP != 3) {
                    titleAndAnswer = m.group(2);
                    Matcher titleMatcher = titleR.matcher(titleAndAnswer);
                    if (titleMatcher.find()) {
                        title = titleMatcher.group(1);
                        answerStr = titleMatcher.group(3).toLowerCase();
                        if (StringUtils.indexOfAny(answerStr, "a", "b", "c", "d") != -1) {
                            for (int i1 = 0; i1 < answerStr.length(); i1++) {
                                answer += String.valueOf(answerSortStr.indexOf(answerStr.charAt(i1)));
                            }
//                            if (answer.length() > 1) {
////                                answer = answer.substring(0, answer.length() - 1);
////                            }
                        }
                    }
                    if (null != m.group(10)) {
                        options += m.group(10).replaceAll("&", "");
                        answer = answerInOption(answer, answerStr, "0", m.group(10));
                    }
                    if (null != m.group(13)) {
                        options += "&" + m.group(13).replaceAll("&", "");
                        answer = answerInOption(answer, answerStr, "1", m.group(13));
                    }
                    if (null != m.group(16)) {
                        options += "&" + m.group(16).replaceAll("&", "");
                        answer = answerInOption(answer, answerStr, "2", m.group(16));
                    }
                    if (null != m.group(19)) {
                        options += "&" + m.group(19).replaceAll("&", "");
                        answer = answerInOption(answer, answerStr, "3", m.group(19));
                    }
                    options = options.replaceAll("、", "");
                }

            } else {
                answerStr = StringUtils.trim(m.group(39));
                if (StringUtils.isBlank(answerStr)) {
                    continue;
                }
                title = m.group(20);
                if (StringUtils.isBlank(title)) {
                    continue;
                }
                if (null != m.group(28)) {
                    options += m.group(28).replaceAll("&", "");
                }
                if (null != m.group(31)) {
                    options += "&" + m.group(31).replaceAll("&", "");
                }
                if (null != m.group(34)) {
                    options += "&" + m.group(34).replaceAll("&", "");
                }
                if (null != m.group(37)) {
                    options += "&" + m.group(37).replaceAll("&", "");
                }
                options = options.replaceAll("、", "");
                if (StringUtils.indexOfAny(answerStr.toLowerCase(), "a", "b", "c", "d") != -1) {
                    if (answerStr.length() > 1) {
                        typeP = 2;
                    }
                    for (int i1 = 0; i1 < answerStr.length(); i1++) {
                        answer += String.valueOf(answerSortStr.indexOf(answerStr.toLowerCase().charAt(i1)));
                    }
//                    if (answer.length() > 1) {
//                        answer = answer.substring(0, answer.length() - 1);
//                    }

                } else if (StringUtils.indexOfAny(answerStr, "√") != -1) {
                    typeP = 3;
                    answer = "1";
                } else if (StringUtils.indexOfAny(answerStr, "×") != -1) {
                    typeP = 3;
                    answer = "0";
                }
            }
            if (StringUtils.isAnyBlank(title, answer)) {
                continue;
            }
            BusiQuestion busiQuestion = new BusiQuestion();
            busiQuestion.setUid(uid);
            busiQuestion.setAnswer(answer);
            busiQuestion.setAppId(appId);
            busiQuestion.setTitle(title);
            busiQuestion.setType(typeP);
            if (typeP != 3) {
                busiQuestion.setQuestions(options);
            }
            busiQuestionMapper.insert(busiQuestion);
//            System.out.println(typeP);
//            System.out.println(title);
//            System.out.println(answer);
//            System.out.println(options);
//            System.out.println("----------------------");
        }
    }

    private String answerInOption(String answer, String answerStr, String i, String option) {
        if (StringUtils.isNotBlank(answer)) {
            return answer;
        }
        if (StringUtils.isBlank(option)) {
            return "";
        }
        int a = option.indexOf(answerStr);
        if (a != -1) {
            return String.valueOf(a);
        }
        return "";
    }
}





