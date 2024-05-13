import axios from 'axios';
import { IFromMe } from './fromme';

const path = '/fromme';

export const getFromMe = async (username: string, pageNum: number, pageSize: number) => {
  const res = await axios.get<IFromMe[]>(
    `${path}?page=${pageNum}&size=${pageSize}&username=${username}`
  );
  return res.data;
};
