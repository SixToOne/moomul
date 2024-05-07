import axios from 'axios';
import { ILoginForm } from './user';

const path = '/users/login';

interface ReturnType {
  userId: string;
  accessToken: string;
  refreshToken: string;
}

export const postLogin = async (data: ILoginForm) => {
  const res = await axios.post<ReturnType>(`${path}`, data);
  return res.data;
};
