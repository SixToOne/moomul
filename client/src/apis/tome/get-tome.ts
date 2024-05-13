import axios from 'axios';
import { IToMe } from './tome';

const path = '/tome/not-replied';

export const getNotRepliedToMe = async (username: string, pageNum: number, pageSize: number) => {
  const res = await axios.get<IToMe[]>(
    `${path}?page=${pageNum}&size=${pageSize}&username=${username}`
  );
  return res.data;
};
