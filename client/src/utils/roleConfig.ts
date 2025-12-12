// 角色配置管理

export interface RoleInfo {
  value: string;         // 角色值
  label: string;         // 角色显示名称
  type: string;          // 标签类型：primary、success、warning等
  description: string;   // 角色描述
  permissions?: string[]; // 默认权限
}

// 默认角色配置，确保至少有一个角色
export const defaultRole: RoleInfo = {
  value: 'user',
  label: '普通用户',
  type: 'success',
  description: '普通学员用户，只能进行学习和考试',
  permissions: []
};

// 角色配置列表
export const roleConfig: RoleInfo[] = [
  defaultRole,
  {
    value: 'admin',
    label: '管理员',
    type: 'warning',
    description: '普通管理员，可进行基本管理操作',
    permissions: []
  },
  {
    value: 'sysadmin',
    label: '系统管理员',
    type: 'primary',
    description: '系统最高权限管理员',
    permissions: ['user', 'question', 'resource', 'task', 'setting']
  }
];

// 根据角色值获取角色信息
export const getRoleInfo = (roleValue: string, username?: string): RoleInfo => {
  // 特殊处理admin账号
  if (username === 'admin') {
    const sysadminRole = roleConfig.find(role => role.value === 'sysadmin')
    return sysadminRole || defaultRole;
  }
  
  const foundRole = roleConfig.find(role => role.value === roleValue)
  return foundRole || defaultRole;
};

// 根据角色值获取角色标签类型
export const getRoleType = (roleValue: string, username?: string): string => {
  return getRoleInfo(roleValue, username).type;
};

// 根据角色值获取角色显示名称
export const getRoleLabel = (roleValue: string, username?: string): string => {
  return getRoleInfo(roleValue, username).label;
};

// 权限配置
export const permissionConfig = {
  user: {
    label: '用户管理',
    children: [
      { id: 'user:list', label: '查看用户列表' },
      { id: 'user:add', label: '添加用户' },
      { id: 'user:edit', label: '编辑用户' },
      { id: 'user:delete', label: '删除用户' },
      { id: 'user:status', label: '修改状态' }
    ]
  },
  question: {
    label: '题库管理',
    children: [
      { id: 'question:list', label: '查看题库' },
      { id: 'question:add', label: '添加题目' },
      { id: 'question:edit', label: '编辑题目' },
      { id: 'question:delete', label: '删除题目' }
    ]
  },
  resource: {
    label: '资源管理',
    children: [
      { id: 'resource:list', label: '查看资源列表' },
      { id: 'resource:add', label: '添加资源' },
      { id: 'resource:edit', label: '编辑资源' },
      { id: 'resource:delete', label: '删除资源' }
    ]
  },
  task: {
    label: '任务管理',
    children: [
      { id: 'task:list', label: '查看任务列表' },
      { id: 'task:create', label: '创建任务' },
      { id: 'task:edit', label: '编辑任务' },
      { id: 'task:delete', label: '删除任务' }
    ]
  },
  setting: {
    label: '系统设置',
    children: [
      { id: 'setting:basic', label: '基本设置' },
      { id: 'setting:permission', label: '权限设置' }
    ]
  }
};

// 权限列表（用于权限分配）
export const permissionList = [
  { id: 'user', label: '用户管理' },
  { id: 'question', label: '题库管理' },
  { id: 'resource', label: '资源管理' },
  { id: 'task', label: '任务管理' },
  { id: 'setting', label: '系统设置' }
];
