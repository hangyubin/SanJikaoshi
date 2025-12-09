-- 设置MySQL全局字符集
SET GLOBAL character_set_server = 'utf8mb4';
SET GLOBAL collation_server = 'utf8mb4_unicode_ci';

-- 设置当前会话字符集
SET NAMES utf8mb4;

-- 确保数据库使用正确的字符集
ALTER DATABASE smart_exam CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
