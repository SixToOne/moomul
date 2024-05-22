import axios from 'axios';
import { INewToMe } from './tome';

const path = '/tome';

export const postToMe = async (username: string, newToMe: INewToMe) => {
  const res = await axios.post<INewToMe>(`${path}?username=${username}`, newToMe);
  return res.data;
};
