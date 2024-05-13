// 2024-03-29T08:14:36.105607 => 24.03.29  08:14
export const getFormattedYearMonthDayTime = (date: Date): string => {
  const year = (date.getFullYear() % 100).toString().padStart(2, '0');
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');
  return `${year}.${month}.${day}  ${hours}:${minutes}`;
};
