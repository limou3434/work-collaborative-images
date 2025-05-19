/**
 * 空间常量
 */

// 空间等级常量
export const SPACE_LEVEL_ENUM = {
  COMMON: 0,
  PROFESSIONAL: 1,
  FLAGSHIP: 2,
} // 审核状态码

export const SPACE_LEVEL_MAP: { [key in 0 | 1 | 2]: string } = {
  0: '普通版',
  1: '专业版',
  2: '旗舰版',
} // 审核状态码映射文本

export const SPACE_LEVEL_OPTIONS = Object.entries(SPACE_LEVEL_MAP).map(([key, label]) => ({
  label,
  value: key,
}))
