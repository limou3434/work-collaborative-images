/**
 * 图片常量
 */

// 图片审核常量
export const PIC_REVIEW_STATUS_ENUM = {
  REVIEWING: 0,
  PASS: 1,
  REJECT: 2,
  NOTODO: 3,
} // 审核状态码

export const PIC_REVIEW_STATUS_MAP: { [key in 0 | 1 | 2 | 3]: string } = {
  0: '待审',
  1: '通过',
  2: '拒绝',
  3: '无事',
} // 审核状态码映射文本

export const PIC_REVIEW_STATUS_OPTIONS = Object.entries(PIC_REVIEW_STATUS_MAP).map(([key, label]) => ({
  label,
  value: key,
}))
