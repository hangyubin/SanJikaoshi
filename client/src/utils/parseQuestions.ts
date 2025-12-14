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
  
  // 简化解析逻辑，逐行处理
  const lines = text.split('\n');
  let currentQuestion: Question | null = null;
  
  for (let line of lines) {
    line = line.trim();
    if (!line) continue;
    
    // 匹配题目编号行：支持多种格式，如 "1. 题目内容：(B)"、"1. 题目内容？A" 或 "1. 题目内容"
    const questionMatch = line.match(/^(\d+)\.\s+(.+?)(?:\s*[:：？]\s*\(?([A-E])\)?)?$/);
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
    
    // 匹配选项行：例如 "A. 选项内容"
    const optionMatch = line.match(/^([A-E])\.\s+(.+)$/);
    if (optionMatch && currentQuestion) {
      const [, letter, optionContent] = optionMatch;
      if (letter && optionContent) {
        currentQuestion.options[letter] = optionContent;
      }
      continue;
    }
    
    // 匹配答案行：例如 "答案：B" 或 "正确答案：B" 或 "正确选项：B"
    const answerMatch = line.match(/^(答案|正确答案|正确选项)[:：]\s*([A-E])$/);
    if (answerMatch && currentQuestion) {
      currentQuestion.answer = answerMatch[2] || '';
      continue;
    }
  }
  
  // 保存最后一题
  if (currentQuestion) {
    questions.push(currentQuestion);
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
