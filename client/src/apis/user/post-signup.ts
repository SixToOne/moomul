import axios from 'axios';
import { ISignupForm } from './user';

const path = '/users/signup';

export const postSignup = async (data: ISignupForm) => {
  const res = await axios.post(`${path}`, data);
  return res.data;
};
