// Node.js脚本，用于将用户提供的单选题转换为系统要求的格式

const fs = require('fs');
const path = require('path');

// 用户提供的新格式单选题文本（答案直接跟在题干后，格式：题干：答案）
const questionTextNewFormat = `单选题 
 1. 一名54岁男性，因“上消化道出血”入院。胃镜示十二指肠球部溃疡（A2期），Hp（+）。经PPI治疗后出血停止。关于后续抗Hp治疗，最佳方案是：B 
 A. 继续单用PPI 8周 
 B. PPI + 克拉霉素 + 阿莫西林，疗程14天 
 C. 铋剂 + 甲硝唑 + 四环素，疗程14天 
 D. 根据药敏试验结果选择方案 
 2. 关于急性ST段抬高型心肌梗死（STEMI）的再灌注治疗，以下说法正确的是：B 
 A. 直接PCI（经皮冠状动脉介入治疗）的时间窗为发病后12小时内 
 B. 静脉溶栓治疗的主要禁忌症包括主动脉夹层 
 C. 发病超过24小时的患者，无需进行再灌注治疗 
 D. 对于心源性休克患者，应优先选择溶栓而非PCI 
 3. 一名8月龄婴儿，因“发热、咳嗽4天，气促1天”入院。胸片示双肺点片状阴影，以下哪项最可能是病原体？A 
 A. 呼吸道合胞病毒 
 B. 肺炎链球菌 
 C. 金黄色葡萄球菌 
 D. 肺炎支原体 
 4. 关于新生儿败血症，以下描述错误的是：D 
 A. 早发型败血症多由垂直传播引起，病原体以G-杆菌为主 
 B. 晚发型败血症多由院内感染或社区获得，病原体更具多样性 
 C. 血培养是诊断的金标准，但阳性率不高 
 D. C-反应蛋白（CRP）在感染后立即升高，是早期诊断的特异性指标 
 5. 一名32岁孕妇，G1P0，孕28周，超声提示“完全性前置胎盘”，无阴道流血。最恰当的处理是：B 
 A. 立即住院，行急诊剖宫产术 
 B. 住院绝对卧床，促胎肺成熟，期待治疗至34-36周 
 C. 门诊随访，禁止性生活，出现出血随时就诊 
 D. 行宫颈环扎术预防早产 
 6. 关于妊娠期急性脂肪肝（AFLP），错误的是：C 
 A. 是妊娠晚期的特发性疾病，可能与胎儿长链3-羟酰基辅酶A脱氢酶（LCHAD）缺乏有关 
 B. 典型临床表现为恶心呕吐、上腹痛、进行性黄疸 
 C. 实验室检查特征包括白细胞显著升高、转氨酶轻度升高、直接胆红素升高为主 
 D. 一旦确诊，无论病情轻重，均应尽快终止妊娠 
 7. 一名腹部刀刺伤患者，血压80/50mmHg，心率130次/分。剖腹探查发现肝右叶裂伤，活动性出血，肠系膜血肿但无肠管破裂。首选的处理是：C 
 A. 肝动脉结扎 
 B. 肝部分切除术 
 C. 纱布填塞止血，二期手术 
 D. 应用止血材料、缝合修补 
 8. 关于腹腔间隔室综合征（ACS），以下说法错误的是：D 
 A. 诊断标准包括腹腔内压力（IAP）持续 > 20mmHg，并伴有新发的器官功能障碍 
 B. 可引起少尿、呼吸衰竭、颅内压增高等 
 C. 非手术治疗包括镇静镇痛、胃肠减压、负水平衡 
 D. 膀胱测压是间接测量IAP的可靠方法，测量时需注入50ml生理盐水 
 9. 为一名使用呼吸机辅助通气的ARDS患者进行吸痰操作时，以下哪项措施是错误的？C 
 A. 吸痰前给予纯氧吸入2分钟（预充氧） 
 B. 选择外径不超过气管插管内径1/2的吸痰管 
 C. 负压调节至150-200mmHg 
 D. 在无负压状态下插入吸痰管，遇到阻力后上提1cm再加负压 
 10. 关于PICC（经外周静脉穿刺中心静脉置管）的维护，错误的是：A 
 A. 穿刺后24小时内应更换敷料 
 B. 透明敷料应每7天更换一次，纱布敷料应每2天更换一次 
 C. 输液接头应随敷料一同更换，或至少每7天更换一次 
 D. 冲管和封管应使用10ml及以上注射器，采用脉冲式冲管、正压封管 
 11. 一名肾功能不全（CKD 4期，eGFR 20ml/min）患者因肺部感染需要使用抗菌药物。下列哪种药物需要显著调整剂量？D 
 A. 莫西沙星 
 B. 头孢曲松 
 C. 阿奇霉素 
 D. 万古霉素 
 12. 关于华法林与药物的相互作用，以下组合中会导致INR（国际标准化比值）显著下降，抗凝效果减弱的是：B 
 A. 华法林 + 胺碘酮 
 B. 华法林 + 利福平 
 C. 华法林 + 甲硝唑 
 D. 华法林 + 阿司匹林 
 13. 患者症见身热夜甚，心烦不寐，斑疹隐隐，舌绛无苔，脉细数。辨证应属：C 
 A. 卫分证 
 B. 气分证 
 C. 营分证 
 D. 血分证 
 14. 方剂“补中益气汤”中，具有“引清气上行”作用的药物是：D 
 A. 人参、黄芪 
 B. 白术、甘草 
 C. 当归、陈皮 
 D. 升麻、柴胡 
 15. 在凝血功能检查中，可以反映共同凝血途径的指标是：C 
 A. 凝血酶原时间（PT） 
 B. 活化部分凝血活酶时间（APTT） 
 C. 凝血酶时间（TT） 
 D. 纤维蛋白原（FIB） 
 16. 关于肿瘤标志物，以下说法错误的是：B 
 A. 甲胎蛋白（AFP）对原发性肝细胞癌具有较高的特异性和敏感性 
 B. 癌胚抗原（CEA）升高仅见于结直肠癌 
 C. 前列腺特异性抗原（PSA）用于前列腺癌的筛查和疗效监测 
 D. CA125主要用于卵巢上皮性癌的监测 
 17. 关于肺栓塞的CT肺动脉造影（CTPA）诊断，直接征象不包括：C 
 A. 肺动脉内充盈缺损 
 B. 肺动脉截断 
 C. “马赛克”征（肺灌注不均） 
 D. 管壁偏心性充盈缺损 
 18. 关于MRI的禁忌症，绝对禁忌症是：D 
 A. 妊娠3个月内 
 B. 装有非磁性材料的人工关节 
 C. 幽闭恐惧症 
 D. 体内有心脏起搏器`;

// 用户提供的原始单选题文本（无答案）
const questionText = `单选题 
 1. 一名54岁男性，因“上消化道出血”入院。胃镜示十二指肠球部溃疡（A2期），Hp（+）。经PPI治疗后出血停止。关于后续抗Hp治疗，最佳方案是： 
 A. 继续单用PPI 8周 
 B. PPI + 克拉霉素 + 阿莫西林，疗程14天 
 C. 铋剂 + 甲硝唑 + 四环素，疗程14天 
 D. 根据药敏试验结果选择方案 
 2. 关于急性ST段抬高型心肌梗死（STEMI）的再灌注治疗，以下说法正确的是： 
 A. 直接PCI（经皮冠状动脉介入治疗）的时间窗为发病后12小时内 
 B. 静脉溶栓治疗的主要禁忌症包括主动脉夹层 
 C. 发病超过24小时的患者，无需进行再灌注治疗 
 D. 对于心源性休克患者，应优先选择溶栓而非PCI 
 3. 一名8月龄婴儿，因“发热、咳嗽4天，气促1天”入院。胸片示双肺点片状阴影，以下哪项最可能是病原体？ 
 A. 呼吸道合胞病毒 
 B. 肺炎链球菌 
 C. 金黄色葡萄球菌 
 D. 肺炎支原体 
 4. 关于新生儿败血症，以下描述错误的是： 
 A. 早发型败血症多由垂直传播引起，病原体以G-杆菌为主 
 B. 晚发型败血症多由院内感染或社区获得，病原体更具多样性 
 C. 血培养是诊断的金标准，但阳性率不高 
 D. C-反应蛋白（CRP）在感染后立即升高，是早期诊断的特异性指标 
 5. 一名32岁孕妇，G1P0，孕28周，超声提示“完全性前置胎盘”，无阴道流血。最恰当的处理是： 
 A. 立即住院，行急诊剖宫产术 
 B. 住院绝对卧床，促胎肺成熟，期待治疗至34-36周 
 C. 门诊随访，禁止性生活，出现出血随时就诊 
 D. 行宫颈环扎术预防早产 
 6. 关于妊娠期急性脂肪肝（AFLP），错误的是： 
 A. 是妊娠晚期的特发性疾病，可能与胎儿长链3-羟酰基辅酶A脱氢酶（LCHAD）缺乏有关 
 B. 典型临床表现为恶心呕吐、上腹痛、进行性黄疸 
 C. 实验室检查特征包括白细胞显著升高、转氨酶轻度升高、直接胆红素升高为主 
 D. 一旦确诊，无论病情轻重，均应尽快终止妊娠 
 7. 一名腹部刀刺伤患者，血压80/50mmHg，心率130次/分。剖腹探查发现肝右叶裂伤，活动性出血，肠系膜血肿但无肠管破裂。首选的处理是： 
 A. 肝动脉结扎 
 B. 肝部分切除术 
 C. 纱布填塞止血，二期手术 
 D. 应用止血材料、缝合修补 
 8. 关于腹腔间隔室综合征（ACS），以下说法错误的是： 
 A. 诊断标准包括腹腔内压力（IAP）持续 > 20mmHg，并伴有新发的器官功能障碍 
 B. 可引起少尿、呼吸衰竭、颅内压增高等 
 C. 非手术治疗包括镇静镇痛、胃肠减压、负水平衡 
 D. 膀胱测压是间接测量IAP的可靠方法，测量时需注入50ml生理盐水 
 9. 为一名使用呼吸机辅助通气的ARDS患者进行吸痰操作时，以下哪项措施是错误的？ 
 A. 吸痰前给予纯氧吸入2分钟（预充氧） 
 B. 选择外径不超过气管插管内径1/2的吸痰管 
 C. 负压调节至150-200mmHg 
 D. 在无负压状态下插入吸痰管，遇到阻力后上提1cm再加负压 
 10. 关于PICC（经外周静脉穿刺中心静脉置管）的维护，错误的是： 
 A. 穿刺后24小时内应更换敷料 
 B. 透明敷料应每7天更换一次，纱布敷料应每2天更换一次 
 C. 输液接头应随敷料一同更换，或至少每7天更换一次 
 D. 冲管和封管应使用10ml及以上注射器，采用脉冲式冲管、正压封管 
 11. 一名肾功能不全（CKD 4期，eGFR 20ml/min）患者因肺部感染需要使用抗菌药物。下列哪种药物需要显著调整剂量？ 
 A. 莫西沙星 
 B. 头孢曲松 
 C. 阿奇霉素 
 D. 万古霉素 
 12. 关于华法林与药物的相互作用，以下组合中会导致INR（国际标准化比值）显著下降，抗凝效果减弱的是： 
 A. 华法林 + 胺碘酮 
 B. 华法林 + 利福平 
 C. 华法林 + 甲硝唑 
 D. 华法林 + 阿司匹林 
 13. 患者症见身热夜甚，心烦不寐，斑疹隐隐，舌绛无苔，脉细数。辨证应属： 
 A. 卫分证 
 B. 气分证 
 C. 营分证 
 D. 血分证 
 14. 方剂“补中益气汤”中，具有“引清气上行”作用的药物是： 
 A. 人参、黄芪 
 B. 白术、甘草 
 C. 当归、陈皮 
 D. 升麻、柴胡 
 15. 在凝血功能检查中，可以反映共同凝血途径的指标是： 
 A. 凝血酶原时间（PT） 
 B. 活化部分凝血活酶时间（APTT） 
 C. 凝血酶时间（TT） 
 D. 纤维蛋白原（FIB） 
 16. 关于肿瘤标志物，以下说法错误的是： 
 A. 甲胎蛋白（AFP）对原发性肝细胞癌具有较高的特异性和敏感性 
 B. 癌胚抗原（CEA）升高仅见于结直肠癌 
 C. 前列腺特异性抗原（PSA）用于前列腺癌的筛查和疗效监测 
 D. CA125主要用于卵巢上皮性癌的监测 
 17. 关于肺栓塞的CT肺动脉造影（CTPA）诊断，直接征象不包括： 
 A. 肺动脉内充盈缺损 
 B. 肺动脉截断 
 C. “马赛克”征（肺灌注不均） 
 D. 管壁偏心性充盈缺损 
 18. 关于MRI的禁忌症，绝对禁忌症是： 
 A. 妊娠3个月内 
 B. 装有非磁性材料的人工关节 
 C. 幽闭恐惧症 
 D. 体内有心脏起搏器`;

// 解析题目（支持带答案和不带答案两种格式）
function parseQuestions(text) {
  const questions = [];
  
  // 简化解析逻辑，逐行处理
  const lines = text.split('\n');
  let currentQuestion = null;
  
  for (let line of lines) {
    line = line.trim();
    if (!line) continue;
    
    // 匹配题目编号行：支持多种格式，如 "1. 题目内容：B"、"1. 题目内容？A" 或 "1. 题目内容"
    const questionMatch = line.match(/^(\d+)\.\s+(.+?)(?:\s*[:：？]\s*([A-E]))?$/);
    if (questionMatch) {
      // 保存上一题
      if (currentQuestion) {
        questions.push(currentQuestion);
      }
      
      // 开始新题目
      currentQuestion = {
        content: questionMatch[2].trim(),
        options: {},
        answer: questionMatch[3] || '', // 提取答案，如果存在的话
        type: 1, // 单选题
        difficulty: 2, // 中等难度
        score: 10 // 默认10分
      };
      continue;
    }
    
    // 匹配选项行：例如 "A. 选项内容"
    const optionMatch = line.match(/^([A-E])\.\s+(.+)$/);
    if (optionMatch && currentQuestion) {
      const [, letter, optionContent] = optionMatch;
      currentQuestion.options[letter] = optionContent;
      continue;
    }
    
    // 匹配答案行：例如 "答案：B" 或 "正确答案：B" 或 "正确选项：B"
    const answerMatch = line.match(/^(答案|正确答案|正确选项)[:：]\s*([A-E])$/);
    if (answerMatch && currentQuestion) {
      currentQuestion.answer = answerMatch[2];
      continue;
    }
  }
  
  // 保存最后一题
  if (currentQuestion) {
    questions.push(currentQuestion);
  }
  
  return questions;
}

// 转换为系统要求的格式
function convertToSystemFormat(questions) {
  return questions.map(q => ({
    subjectId: '1', // 默认科目ID为1，根据实际情况修改
    type: q.type,
    difficulty: q.difficulty,
    content: q.content,
    options: JSON.stringify(q.options), // 转换为JSON字符串
    answer: q.answer,
    analysis: '',
    score: q.score
  }));
}

// 生成Excel导入模板数据
function generateExcelData(questions) {
  return questions.map(q => ({
    '题目类型': '单选题',
    '题目内容': q.content,
    '选项A': q.options.A || '',
    '选项B': q.options.B || '',
    '选项C': q.options.C || '',
    '选项D': q.options.D || '',
    '选项E': q.options.E || '',
    '正确答案': q.answer,
    '解析': '',
    '难度': q.difficulty === 1 ? '简单' : q.difficulty === 2 ? '中等' : '困难',
    '分值': q.score
  }));
}

// 主函数
function main() {
  console.log('=== 解析无答案的题目 ===');
  
  // 解析无答案的题目
  const questionsWithoutAnswer = parseQuestions(questionText);
  console.log(`成功解析 ${questionsWithoutAnswer.length} 道无答案题目`);
  
  // 转换为系统要求的格式
  const systemFormatWithoutAnswer = convertToSystemFormat(questionsWithoutAnswer);
  
  // 生成Excel导入模板数据
  const excelDataWithoutAnswer = generateExcelData(questionsWithoutAnswer);
  
  console.log('\n=== 解析带答案的题目 ===');
  
  // 解析带答案的题目
  const questionsWithAnswer = parseQuestions(questionTextNewFormat);
  console.log(`成功解析 ${questionsWithAnswer.length} 道带答案题目`);
  
  // 转换为系统要求的格式
  const systemFormatWithAnswer = convertToSystemFormat(questionsWithAnswer);
  
  // 生成Excel导入模板数据
  const excelDataWithAnswer = generateExcelData(questionsWithAnswer);
  
  // 写入文件
  const outputDir = path.join(__dirname, 'output');
  if (!fs.existsSync(outputDir)) {
    fs.mkdirSync(outputDir);
  }
  
  // 写入无答案的系统格式文件
  fs.writeFileSync(
    path.join(outputDir, 'system_format_without_answer.json'),
    JSON.stringify(systemFormatWithoutAnswer, null, 2),
    'utf8'
  );
  
  // 写入无答案的Excel导入模板数据
  fs.writeFileSync(
    path.join(outputDir, 'excel_template_without_answer.json'),
    JSON.stringify(excelDataWithoutAnswer, null, 2),
    'utf8'
  );
  
  // 写入带答案的系统格式文件
  fs.writeFileSync(
    path.join(outputDir, 'system_format_with_answer.json'),
    JSON.stringify(systemFormatWithAnswer, null, 2),
    'utf8'
  );
  
  // 写入带答案的Excel导入模板数据
  fs.writeFileSync(
    path.join(outputDir, 'excel_template_with_answer.json'),
    JSON.stringify(excelDataWithAnswer, null, 2),
    'utf8'
  );
  
  console.log('\n题目转换完成！');
  console.log('输出文件：');
  console.log(`- 无答案系统格式：${path.join(outputDir, 'system_format_without_answer.json')}`);
  console.log(`- 无答案Excel模板：${path.join(outputDir, 'excel_template_without_answer.json')}`);
  console.log(`- 带答案系统格式：${path.join(outputDir, 'system_format_with_answer.json')}`);
  console.log(`- 带答案Excel模板：${path.join(outputDir, 'excel_template_with_answer.json')}`);
  
  // 显示前3道无答案题目的解析结果
  console.log('\n=== 前3道无答案题目的解析结果 ===');
  questionsWithoutAnswer.slice(0, 3).forEach((q, index) => {
    console.log(`\n题目 ${index + 1}：${q.content}`);
    console.log('选项：');
    Object.entries(q.options).forEach(([key, value]) => {
      console.log(`  ${key}. ${value}`);
    });
    console.log(`答案：${q.answer || '（未填写，可在系统中修改）'}`);
  });
  
  // 显示前3道带答案题目的解析结果
  console.log('\n=== 前3道带答案题目的解析结果 ===');
  questionsWithAnswer.slice(0, 3).forEach((q, index) => {
    console.log(`\n题目 ${index + 1}：${q.content}`);
    console.log('选项：');
    Object.entries(q.options).forEach(([key, value]) => {
      console.log(`  ${key}. ${value}`);
    });
    console.log(`答案：${q.answer || '（未填写）'}`);
  });
}

// 执行主函数
main();
