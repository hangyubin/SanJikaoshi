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
 * 解析单选题文本
 * @param text 单选题文本
 * @returns 解析后的题目数组
 */
export const parseQuestions = (text: string): Question[] => {
  const questions: Question[] = [];
  
  // 按题目拆分文本
  const questionRegex = /(\d+)\.\s+(.+?)\s+(A\..+?)(?=\n(?:\d+\.|$))/gs;
  let match;
  
  while ((match = questionRegex.exec(text)) !== null) {
    const [, id, content, optionsText] = match;
    
    // 解析选项
    const options: Record<string, string> = {};
    const optionRegex = /([A-E])\.\s+(.+?)(?=\n[A-E]\.|$)/gs;
    let optionMatch;
    
    while ((optionMatch = optionRegex.exec(optionsText)) !== null) {
      const [, letter, optionContent] = optionMatch;
      options[letter] = optionContent.trim();
    }
    
    // 暂时将答案设为空，需要手动填写
    questions.push({
      content: content.trim(),
      options,
      answer: '', // 正确答案需要手动填写
      type: 1, // 单选题
      difficulty: 2, // 中等难度
      score: 10 // 默认10分
    });
  }
  
  return questions;
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
