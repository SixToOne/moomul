import axios from 'axios';

const path = '/fromme';

export interface INewFromMe {
  content: string;
  options: string[];
}

export const postFromMe = async (username: string, newToMe: INewFromMe) => {
  const res = await axios.post<INewFromMe>(`${path}?username=${username}`, newToMe);
  return res.data;
};
