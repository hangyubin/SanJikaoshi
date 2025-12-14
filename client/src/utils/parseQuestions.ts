// 解析单选题文本，转换为系统要求的格式

interface Question {
  content: string;
  options: Record<string, string>;
  answer: string;
  type: number;
  difficulty: number;
  score: number;
  analysis?: string;
}

/**
 * 解析单选题文本（支持带答案和不带答案两种格式）
 * @param text 单选题文本
 * @returns 解析后的题目数组
 */
export const parseQuestions = (text: string): Question[] => {
  const questions: Question[] = [];
  
  // 处理不同换行符
  const normalizedText = text.replace(/\r\n/g, '\n').replace(/\r/g, '\n');
  const lines = normalizedText.split('\n');
  let currentQuestion: Question | null = null;
  let lineNumber = 0;
  
  for (let line of lines) {
    lineNumber++;
    line = line.trim();
    if (!line) continue;
    
    // 1. 匹配题目编号行：支持更多格式
    // 支持：
    // - "1. 题目内容：(A)"
    // - "1、题目内容？B"
    // - "1、题目内容"
    // - "1. 题目内容"
    // - "1 题目内容"
    const questionMatch = line.match(/^(\d+)\s*[.、]?\s+(.+?)(?:\s*[:：？]\s*(?:\(?([A-E])\)?)?)?$/);
    if (questionMatch) {
      // 保存上一题
      if (currentQuestion) {
        questions.push(currentQuestion);
      }
      
      // 开始新题目
      currentQuestion = {
        content: questionMatch[2]?.trim() || '',
        options: {},
        answer: questionMatch[3] || '', // 提取答案，如果存在的话
        type: 1, // 单选题
        difficulty: 2, // 中等难度
        score: 10 // 默认10分
      };
      continue;
    }
    
    // 2. 匹配选项行：支持更多格式
    // 支持：
    // - "A. 选项内容"
    // - "A、选项内容"
    // - "A 选项内容"
    const optionMatch = line.match(/^([A-E])\s*[.、]?\s+(.+)$/);
    if (optionMatch && currentQuestion) {
      const [, letter, optionContent] = optionMatch;
      if (letter && optionContent) {
        // 确保选项键为大写
        const upperLetter = letter.toUpperCase();
        currentQuestion.options[upperLetter] = optionContent.trim();
      }
      continue;
    }
    
    // 3. 匹配答案行：支持更多格式
    // 支持：
    // - "答案：B"
    // - "正确答案：B"
    // - "正确选项：B"
    // - "答案:B"
    // - "答案 B"
    const answerMatch = line.match(/^(答案|正确答案|正确选项)\s*[:：]?\s*(\(?[A-E]\)?)$/i);
    if (answerMatch && currentQuestion) {
      // 提取答案，去除可能的括号
      const answer = answerMatch[2]?.replace(/[()]/g, '')?.toUpperCase() || '';
      if (/^[A-E]$/.test(answer)) {
        currentQuestion.answer = answer;
      }
      continue;
    }
    
    // 4. 如果当前没有题目，但匹配到了选项或答案，尝试创建一个默认题目
    if (!currentQuestion) {
      // 这种情况通常是因为题目格式不符合要求
      // 创建一个临时题目，标记为无效，以便后续过滤
      currentQuestion = {
        content: `未识别的题目内容 (行 ${lineNumber})`,
        options: {},
        answer: '',
        type: 1,
        difficulty: 2,
        score: 10
      };
    }
  }
  
  // 保存最后一题
  if (currentQuestion) {
    questions.push(currentQuestion);
  }
  
  // 过滤掉无效题目，并确保所有题目都有必填字段
  return questions.filter(q => {
    // 基本验证：题目内容不能为空
    if (!q.content || q.content.includes('未识别的题目内容')) {
      return false;
    }
    
    // 确保答案有效
    if (!q.answer || !/^[A-E]$/.test(q.answer)) {
      // 尝试从选项中推断答案（如果只有一个选项）
      const options = Object.keys(q.options);
      if (options.length === 1) {
        // 确保options[0]不是undefined，添加类型断言
        q.answer = options[0] as string;
      } else {
        return false;
      }
    }
    
    // 确保至少有一个选项
    if (Object.keys(q.options).length === 0) {
      return false;
    }
    
    return true;
  });
};

/**
 * 将题目转换为系统API要求的格式
 * @param questions 解析后的题目数组
 * @param subjectId 科目ID
 * @returns 系统API要求的题目格式
 */
export const convertToSystemFormat = (questions: Question[], subjectId: string) => {
  return questions.map(q => ({
    subjectId,
    type: q.type,
    difficulty: q.difficulty,
    content: q.content,
    options: JSON.stringify(q.options), // 转换为JSON字符串
    answer: q.answer,
    analysis: q.analysis || '',
    score: q.score
  }));
};

/**
 * 生成Excel导入模板数据
 * @param questions 解析后的题目数组
 * @returns Excel导入模板数据
 */
export const generateExcelData = (questions: Question[]) => {
  return questions.map(q => ({
    '题目类型': '单选题',
    '题目内容': q.content,
    '选项A': q.options.A || '',
    '选项B': q.options.B || '',
    '选项C': q.options.C || '',
    '选项D': q.options.D || '',
    '选项E': q.options.E || '',
    '正确答案': q.answer,
    '解析': q.analysis || '',
    '难度': q.difficulty === 1 ? '简单' : q.difficulty === 2 ? '中等' : '困难',
    '分值': q.score
  }));
};
