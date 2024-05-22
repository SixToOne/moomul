import axios from 'axios';
import { IToMe } from './tome';

const path = '/tome';

export const getNotRepliedToMe = async (username: string, pageNum: number, pageSize: number) => {
  const res = await axios.get<IToMe[]>(
    `${path}/not-replied?page=${pageNum}&size=${pageSize}&username=${username}`
  );
  return res.data;
};

export const getRepliedToMe = async (username: string, pageNum: number, pageSize: number) => {
  const res = await axios.get<IToMe[]>(
    `${path}/replied?page=${pageNum}&size=${pageSize}&username=${username}`
  );
  return res.data;
};
