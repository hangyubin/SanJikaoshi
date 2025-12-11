package com.smartexam.backend.controller;

import com.smartexam.backend.entity.Question;
import com.smartexam.backend.repository.QuestionRepository;
import com.smartexam.backend.repository.SubjectRepository;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/questions/import")
public class QuestionImportController {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    // 生成题目导入模板
    @GetMapping("/template")
    public ResponseEntity<?> generateQuestionTemplate(@RequestParam(defaultValue = "xlsx") String format) {
        try {
            // 创建工作簿
            Workbook workbook = "xlsx".equalsIgnoreCase(format) ? new XSSFWorkbook() : new HSSFWorkbook();
            Sheet sheet = workbook.createSheet("题目模板");

            // 创建标题行样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // 创建数据行样式
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setVerticalAlignment(VerticalAlignment.TOP);

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "题型", "科目", "难度", "分值", "题目内容", 
                    "选项A", "选项B", "选项C", "选项D", "选项E", 
                    "正确答案", "解析", "状态", "创建者"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 20 * 256); // 设置列宽
            }

            // 创建示例数据行
            Row exampleRow = sheet.createRow(1);
            String[] exampleData = {
                    "1", "基本理论", "1", "10", "以下关于三基考试的描述，正确的是？",
                    "A. 三基考试只针对医生", "B. 三基考试包括基本理论、基础知识、基本技能", "C. 三基考试每年只举行一次", "D. 三基考试不需要复习", "",
                    "B", "三基考试是针对所有医务人员的考试，包括医生、护士等，每年举行多次，需要认真复习。", "1", "admin"
            };

            for (int i = 0; i < exampleData.length; i++) {
                Cell cell = exampleRow.createCell(i);
                cell.setCellValue(exampleData[i]);
                cell.setCellStyle(dataStyle);
            }

            // 添加说明行
            Row noteRow = sheet.createRow(2);
            CellStyle noteStyle = workbook.createCellStyle();
            noteStyle.setWrapText(true);
            noteStyle.setVerticalAlignment(VerticalAlignment.TOP);
            Cell noteCell = noteRow.createCell(0);
            String note = "说明：\n" +
                    "1. 题型：1-单选题，2-多选题，3-判断题，4-填空题，5-简答题\n" +
                    "2. 科目：基本理论、基础知识、基本技能\n" +
                    "3. 难度：1-简单，2-中等，3-困难\n" +
                    "4. 分值：题目分值，如10\n" +
                    "5. 正确答案：单选题和判断题填写选项字母（如A、B、C、D），多选题填写多个字母（如AB、ABC），判断题填写A（正确）或B（错误）\n" +
                    "6. 状态：1-启用，0-禁用\n" +
                    "7. 选项E：如果是单选题或多选题，可以留空\n" +
                    "8. 填空题和简答题的选项可以留空\n" +
                    "9. 解析：可以留空\n" +
                    "10. 创建者：可以留空";
            noteCell.setCellValue(note);
            noteCell.setCellStyle(noteStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(2, 2, 0, headers.length - 1));

            // 输出工作簿
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            // 设置响应头
            String fileName = "题目导入模板." + format;
            HttpHeaders headers1 = new HttpHeaders();
            headers1.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers1.setContentDispositionFormData("attachment", fileName);

            return ResponseEntity.ok()
                    .headers(headers1)
                    .body(outputStream.toByteArray());
        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "生成模板失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 批量导入题目
    @PostMapping("/batch")
    public ResponseEntity<?> batchImportQuestions(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (file.isEmpty()) {
                response.put("code", 400);
                response.put("message", "文件不能为空");
                return ResponseEntity.ok(response);
            }

            // 检查文件类型
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
                response.put("code", 400);
                response.put("message", "只支持Excel文件(.xlsx, .xls)");
                return ResponseEntity.ok(response);
            }

            // 创建工作簿
            Workbook workbook = fileName.endsWith(".xlsx") ? 
                    new XSSFWorkbook(file.getInputStream()) : 
                    new HSSFWorkbook(file.getInputStream());

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                response.put("code", 400);
                response.put("message", "Excel文件中没有工作表");
                return ResponseEntity.ok(response);
            }

            // 跳过标题行和示例行
            int startRow = 2; // 从第三行开始读取数据
            int totalRows = sheet.getLastRowNum();
            List<Question> questions = new ArrayList<>();
            Map<String, Object> result = new HashMap<>();
            int successCount = 0;
            int failCount = 0;
            List<String> errorMessages = new ArrayList<>();

            for (int i = startRow; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    Question question = new Question();
                    long currentTime = System.currentTimeMillis();

                    // 读取数据
                    String typeStr = getCellValue(row.getCell(0));
                    String subjectName = getCellValue(row.getCell(1));
                    String difficultyStr = getCellValue(row.getCell(2));
                    String scoreStr = getCellValue(row.getCell(3));
                    String title = getCellValue(row.getCell(4));
                    String optionA = getCellValue(row.getCell(5));
                    String optionB = getCellValue(row.getCell(6));
                    String optionC = getCellValue(row.getCell(7));
                    String optionD = getCellValue(row.getCell(8));
                    String optionE = getCellValue(row.getCell(9));
                    String answer = getCellValue(row.getCell(10));
                    String analysis = getCellValue(row.getCell(11));
                    String statusStr = getCellValue(row.getCell(12));
                    String createBy = getCellValue(row.getCell(13));

                    // 验证必填字段
                    if (title == null || title.trim().isEmpty()) {
                        failCount++;
                        errorMessages.add("第" + (i + 1) + "行：题目内容不能为空");
                        continue;
                    }
                    if (answer == null || answer.trim().isEmpty()) {
                        failCount++;
                        errorMessages.add("第" + (i + 1) + "行：正确答案不能为空");
                        continue;
                    }

                    // 设置题目属性
                    question.setTitle(title);
                    question.setContent(title); // 题目内容和标题相同
                    question.setAnswer(answer);
                    question.setAnalysis(analysis);
                    question.setCreateTime(currentTime);
                    question.setUpdateTime(currentTime);

                    // 设置题型
                    question.setType(typeStr != null && !typeStr.isEmpty() ? Integer.parseInt(typeStr) : 1);

                    // 设置难度
                    question.setDifficulty(difficultyStr != null && !difficultyStr.isEmpty() ? Integer.parseInt(difficultyStr) : 1);

                    // 设置分值
                    question.setScore(scoreStr != null && !scoreStr.isEmpty() ? Integer.parseInt(scoreStr) : 10);

                    // 设置状态
                    question.setStatus(statusStr != null && !statusStr.isEmpty() ? Integer.parseInt(statusStr) : 1);

                    // 设置科目
                    if (subjectName != null && !subjectName.isEmpty()) {
                        com.smartexam.backend.entity.Subject subject = subjectRepository.findByName(subjectName);
                        if (subject == null) {
                            failCount++;
                            errorMessages.add("第" + (i + 1) + "行：科目不存在：" + subjectName);
                            continue;
                        }
                        question.setSubject(subject);
                    }

                    // 设置选项
                    Map<String, String> options = new HashMap<>();
                    if (optionA != null && !optionA.isEmpty()) options.put("A", optionA);
                    if (optionB != null && !optionB.isEmpty()) options.put("B", optionB);
                    if (optionC != null && !optionC.isEmpty()) options.put("C", optionC);
                    if (optionD != null && !optionD.isEmpty()) options.put("D", optionD);
                    if (optionE != null && !optionE.isEmpty()) options.put("E", optionE);
                    if (!options.isEmpty()) {
                        question.setOptions(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(options));
                    }

                    questions.add(question);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    errorMessages.add("第" + (i + 1) + "行：" + e.getMessage());
                }
            }

            // 保存题目
            if (!questions.isEmpty()) {
                questionRepository.saveAll(questions);
            }

            // 设置结果
            result.put("total", totalRows - startRow + 1);
            result.put("success", successCount);
            result.put("fail", failCount);
            result.put("errorMessages", errorMessages);

            response.put("code", 200);
            response.put("message", "批量导入完成");
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "批量导入失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 获取单元格值
    private String getCellValue(Cell cell) {
        if (cell == null) return null;

        String value = null;
        switch (cell.getCellType()) {
            case STRING:
                value = cell.getStringCellValue();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    value = cell.getDateCellValue().toString();
                } else {
                    value = String.valueOf(cell.getNumericCellValue());
                    // 移除小数部分
                    if (value.endsWith(".0")) {
                        value = value.substring(0, value.length() - 2);
                    }
                }
                break;
            case BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA:
                value = cell.getCellFormula();
                break;
            case BLANK:
                value = null;
                break;
            default:
                value = null;
        }

        return value;
    }
}


