/**
 * 格式化文件大小
 */
export const formatSize = (size?: number) => {
  if (size == 0) return 0 + ' B'
  if (!size) return '未知'
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB'
  return (size / (1024 * 1024)).toFixed(2) + ' MB'
}
