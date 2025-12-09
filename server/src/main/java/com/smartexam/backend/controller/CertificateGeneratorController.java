package com.smartexam.backend.controller;

import com.smartexam.backend.entity.*;
import com.smartexam.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/certificate/generator")
public class CertificateGeneratorController {

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private UserCertificateRepository userCertificateRepository;

    @Autowired
    private ExamRecordRepository examRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExamPaperRepository examPaperRepository;

    // 考试通过后自动颁发证书
    @PostMapping("/auto-issue")
    public ResponseEntity<?> autoIssueCertificate(@RequestBody Map<String, Long> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long examRecordId = request.get("examRecordId");
            if (examRecordId == null) {
                response.put("code", 400);
                response.put("message", "考试记录ID不能为空");
                return ResponseEntity.ok(response);
            }

            // 获取考试记录
            Optional<ExamRecord> optionalRecord = examRecordRepository.findById(examRecordId);
            if (!optionalRecord.isPresent()) {
                response.put("code", 404);
                response.put("message", "考试记录不存在");
                return ResponseEntity.ok(response);
            }

            ExamRecord record = optionalRecord.get();

            // 检查考试是否通过（60分及格）
            double passScore = record.getTotalScore() * 0.6;
            if (record.getObtainedScore() < passScore) {
                response.put("code", 400);
                response.put("message", "考试未通过，无法颁发证书");
                return ResponseEntity.ok(response);
            }

            // 获取试卷信息
            Optional<ExamPaper> optionalPaper = examPaperRepository.findById(record.getExamPaperId());
            if (!optionalPaper.isPresent()) {
                response.put("code", 404);
                response.put("message", "试卷不存在");
                return ResponseEntity.ok(response);
            }

            ExamPaper paper = optionalPaper.get();

            // 获取关联的证书模板
            // 这里假设试卷和证书是一对多关系，实际项目中可能需要更复杂的关联逻辑
            List<Certificate> certificates = certificateRepository.findBySubjectId(Math.toIntExact(paper.getSubject().getId()));
            if (certificates.isEmpty()) {
                response.put("code", 404);
                response.put("message", "该科目暂无证书模板");
                return ResponseEntity.ok(response);
            }

            // 选择合适的证书模板（这里简单取第一个，实际项目中可能需要更复杂的逻辑）
            Certificate certificate = certificates.get(0);

            // 检查用户是否已获得该证书
            UserCertificate existingUserCertificate = userCertificateRepository.findByUserIdAndCertificateId(record.getUserId(), certificate.getId());
            if (existingUserCertificate != null) {
                response.put("code", 400);
                response.put("message", "用户已获得该证书");
                response.put("data", existingUserCertificate);
                return ResponseEntity.ok(response);
            }

            // 创建用户证书
            UserCertificate userCertificate = new UserCertificate();
            userCertificate.setUser(record.getUser());
            userCertificate.setCertificate(certificate);
            userCertificate.setScore(record.getObtainedScore());
            userCertificate.setIssueDate(System.currentTimeMillis());
            userCertificate.setStatus(1); // 有效状态
            userCertificate.setCreateTime(System.currentTimeMillis());
            userCertificate.setUpdateTime(System.currentTimeMillis());

            // 计算过期日期
            if (certificate.getValidityPeriod() != null) {
                long expireDate = userCertificate.getIssueDate() + (long) certificate.getValidityPeriod() * 365 * 24 * 60 * 60 * 1000L;
                userCertificate.setExpireDate(expireDate);
            }

            // 生成证书编号
            String certificateNumber = generateCertificateNumber(userCertificate);
            userCertificate.setCertificateNumber(certificateNumber);

            UserCertificate savedUserCertificate = userCertificateRepository.save(userCertificate);

            response.put("code", 200);
            response.put("message", "证书颁发成功");
            response.put("data", savedUserCertificate);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "证书颁发失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 手动颁发证书
    @PostMapping("/manual-issue")
    public ResponseEntity<?> manualIssueCertificate(@RequestBody UserCertificate userCertificate) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 检查用户是否存在
            if (userCertificate.getUser() == null || userCertificate.getUser().getId() == null) {
                response.put("code", 400);
                response.put("message", "用户信息不能为空");
                return ResponseEntity.ok(response);
            }

            // 检查证书模板是否存在
            if (userCertificate.getCertificate() == null || userCertificate.getCertificate().getId() == null) {
                response.put("code", 400);
                response.put("message", "证书模板不能为空");
                return ResponseEntity.ok(response);
            }

            // 检查用户是否已获得该证书
            UserCertificate existingUserCertificate = userCertificateRepository.findByUserIdAndCertificateId(
                    userCertificate.getUser().getId(), userCertificate.getCertificate().getId());
            if (existingUserCertificate != null) {
                response.put("code", 400);
                response.put("message", "用户已获得该证书");
                response.put("data", existingUserCertificate);
                return ResponseEntity.ok(response);
            }

            // 设置默认值
            if (userCertificate.getIssueDate() == null) {
                userCertificate.setIssueDate(System.currentTimeMillis());
            }
            if (userCertificate.getStatus() == null) {
                userCertificate.setStatus(1); // 有效状态
            }
            if (userCertificate.getCreateTime() == null) {
                userCertificate.setCreateTime(System.currentTimeMillis());
            }
            if (userCertificate.getUpdateTime() == null) {
                userCertificate.setUpdateTime(System.currentTimeMillis());
            }

            // 计算过期日期
            Optional<Certificate> optionalCertificate = certificateRepository.findById(userCertificate.getCertificate().getId());
            if (optionalCertificate.isPresent()) {
                Certificate certificate = optionalCertificate.get();
                if (certificate.getValidityPeriod() != null) {
                    long expireDate = userCertificate.getIssueDate() + (long) certificate.getValidityPeriod() * 365 * 24 * 60 * 60 * 1000L;
                    userCertificate.setExpireDate(expireDate);
                }
            }

            // 生成证书编号
            String certificateNumber = generateCertificateNumber(userCertificate);
            userCertificate.setCertificateNumber(certificateNumber);

            UserCertificate savedUserCertificate = userCertificateRepository.save(userCertificate);

            response.put("code", 200);
            response.put("message", "证书颁发成功");
            response.put("data", savedUserCertificate);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "证书颁发失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 生成证书编号
    private String generateCertificateNumber(UserCertificate userCertificate) {
        // 证书编号格式：CERT-YYYYMMDD-USERID-CERTID-随机数
        Date now = new Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(now);
        String randomStr = String.format("%04d", new Random().nextInt(10000));
        return "CERT-" + dateStr + "-" + userCertificate.getUserId() + "-" + userCertificate.getCertificateId() + "-" + randomStr;
    }

    // 检查证书有效性
    @PostMapping("/check-validity")
    public ResponseEntity<?> checkCertificateValidity(@RequestBody Map<String, Long> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userCertificateId = request.get("userCertificateId");
            if (userCertificateId == null) {
                response.put("code", 400);
                response.put("message", "用户证书ID不能为空");
                return ResponseEntity.ok(response);
            }

            // 获取用户证书
            Optional<UserCertificate> optionalUserCertificate = userCertificateRepository.findById(userCertificateId);
            if (!optionalUserCertificate.isPresent()) {
                response.put("code", 404);
                response.put("message", "用户证书不存在");
                return ResponseEntity.ok(response);
            }

            UserCertificate userCertificate = optionalUserCertificate.get();
            Map<String, Object> validityInfo = new HashMap<>();
            validityInfo.put("certificate", userCertificate);
            
            // 检查状态
            boolean isValid = userCertificate.getStatus() == 1;
            validityInfo.put("statusValid", isValid);
            
            // 检查是否过期
            boolean isExpired = false;
            if (userCertificate.getExpireDate() != null) {
                isExpired = System.currentTimeMillis() > userCertificate.getExpireDate();
            }
            validityInfo.put("expired", isExpired);
            
            // 综合有效性
            validityInfo.put("valid", isValid && !isExpired);
            
            // 剩余有效期（天）
            if (userCertificate.getExpireDate() != null) {
                long remainingDays = (userCertificate.getExpireDate() - System.currentTimeMillis()) / 1000 / 60 / 60 / 24;
                validityInfo.put("remainingDays", Math.max(0, remainingDays));
            } else {
                validityInfo.put("remainingDays", "永久有效");
            }

            response.put("code", 200);
            response.put("message", "证书有效性检查成功");
            response.put("data", validityInfo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "证书有效性检查失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 更新过期证书状态
    @PostMapping("/update-expired-status")
    public ResponseEntity<?> updateExpiredCertificates() {
        Map<String, Object> response = new HashMap<>();
        try {
            // 获取所有有效状态的用户证书
            List<UserCertificate> userCertificates = userCertificateRepository.findAll()
                    .stream()
                    .filter(uc -> uc.getStatus() == 1)
                    .collect(Collectors.toList());

            long currentTime = System.currentTimeMillis();
            int updatedCount = 0;

            // 检查并更新过期证书
            for (UserCertificate userCertificate : userCertificates) {
                if (userCertificate.getExpireDate() != null && userCertificate.getExpireDate() < currentTime) {
                    userCertificate.setStatus(2); // 设置为过期状态
                    userCertificate.setUpdateTime(currentTime);
                    userCertificateRepository.save(userCertificate);
                    updatedCount++;
                }
            }

            response.put("code", 200);
            response.put("message", "过期证书状态更新成功");
            response.put("data", Map.of("totalChecked", userCertificates.size(), "updatedCount", updatedCount));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "过期证书状态更新失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据证书编号查询证书
    @GetMapping("/by-number/{certificateNumber}")
    public ResponseEntity<?> getCertificateByNumber(@PathVariable String certificateNumber) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 这里需要添加根据证书编号查询的方法到UserCertificateRepository
            // UserCertificate userCertificate = userCertificateRepository.findByCertificateNumber(certificateNumber);
            // 暂时使用模拟数据
            response.put("code", 200);
            response.put("message", "根据证书编号查询证书成功");
            response.put("data", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "根据证书编号查询证书失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 批量生成证书
    @PostMapping("/batch-issue")
    public ResponseEntity<?> batchIssueCertificates(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Long> examRecordIds = (List<Long>) request.get("examRecordIds");
            if (examRecordIds == null || examRecordIds.isEmpty()) {
                response.put("code", 400);
                response.put("message", "考试记录ID列表不能为空");
                return ResponseEntity.ok(response);
            }

            List<UserCertificate> issuedCertificates = new ArrayList<>();
            List<Long> failedRecordIds = new ArrayList<>();

            // 遍历考试记录，批量生成证书
            for (Long examRecordId : examRecordIds) {
                try {
                    Map<String, Long> autoIssueRequest = new HashMap<>();
                    autoIssueRequest.put("examRecordId", examRecordId);
                    ResponseEntity<?> issueResponse = autoIssueCertificate(autoIssueRequest);
                    Map<String, Object> issueResponseData = (Map<String, Object>) issueResponse.getBody();
                    if (issueResponseData.get("code").equals(200)) {
                        issuedCertificates.add((UserCertificate) issueResponseData.get("data"));
                    } else {
                        failedRecordIds.add(examRecordId);
                    }
                } catch (Exception e) {
                    failedRecordIds.add(examRecordId);
                }
            }

            response.put("code", 200);
            response.put("message", "批量证书颁发完成");
            response.put("data", Map.of(
                    "total", examRecordIds.size(),
                    "success", issuedCertificates.size(),
                    "failed", failedRecordIds.size(),
                    "issuedCertificates", issuedCertificates,
                    "failedRecordIds", failedRecordIds
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "批量证书颁发失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}